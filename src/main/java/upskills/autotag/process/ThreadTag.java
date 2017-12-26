package upskills.autotag.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Issue;
import lle.crud.model.Trade;
import lle.crud.service.TradeService;
import upskills.autotag.model.TaggedObj;

public class ThreadTag extends Thread implements Runnable {
	HashMap<String, String> key_val = null;
	List<TaggedObj> mm_table = new ArrayList<>();
	List<String> mod_key_head = null;
	TradeService tradeService = DataHibernateUtil.getTradeService();
	public ThreadTag(List<TaggedObj> list, List<String> key_header)
	{
		mm_table = new ArrayList<>(list);
		mod_key_head = key_header;
	}
	
	public List<TaggedObj> getDataList()
	{
		return mm_table;
	}
	
	public void run(){
		int count=0;
		for (Iterator iterator = mm_table.iterator(); iterator.hasNext();) {
			TaggedObj taggedObj = (TaggedObj) iterator.next();
			List<Trade> trades = new ArrayList<>();
			List<Trade> org_trades=null;
			List<Trade> trade_issues = new ArrayList<>();

			key_val = (HashMap<String, String>) taggedObj.get_disp_column().entrySet().stream().filter(k->mod_key_head.contains(k.getKey())).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
			taggedObj.set_disp_column(key_val);
			
			trades = tradeService.getTradeByCriteria(key_val);
			
			trades  = trades.stream().filter(t -> t.getIssueList() != null || t.getIssueList().size() > 0)
					.collect(Collectors.toList()); // SELECT TRADE WHICH HAVE ISSUE
			org_trades = new ArrayList<Trade>(trades);
			if (trades != null && trades.size() > 0) {
				for (int i =0 ; i < trades.size(); i++) {
					Trade trade = new Trade(trades.get(i));
					trade.setIssueList(trade.getIssueList().stream()
							.filter(is -> is.getField().equalsIgnoreCase(taggedObj.get_field_name()))
							.collect(Collectors.toList()));
					if (trade.getIssueList().size() > 0)
						trade_issues.add(trade);
				}

				/*
				 * Key and Field name exists in DB
				 */
				if (trade_issues.size() > 0) {
					List<Issue> issues = trade_issues.stream().map(Trade::getIssueList).flatMap(x->x.stream()).collect(Collectors.toList());
					taggedObj.set_issues(
							issues.stream().map(is -> String.valueOf(is.getIssueId())).collect(Collectors.toList()));
					taggedObj.set_systematic(true);
				} else { // Key and Field name NOT exists in DB
					List<Issue> issues = org_trades.stream().map(Trade::getIssueList).flatMap(x->x.stream()).collect(Collectors.toList());
					taggedObj.set_issues(
							issues.stream().map(is -> String.valueOf(is.getIssueId())).collect(Collectors.toList()));
				}
			} else {
				// TODO implement for NOT existed Trade
				System.out.println("Trade not existed ... "+count++);
			}
		}
	}
}
