/**
 * 
 */
package upskills.autotag.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Issue;
import lle.crud.model.Trade;
import lle.crud.model.TradeIssueMap;
import lle.crud.model.TradeIssueMapKey;
import lle.crud.service.TradeIssueMapService;
import lle.crud.service.TradeService;
import upskills.autotag.model.TaggedObj;

/**
 * @author LuanNgu
 *
 */
public class ThreadImportTag extends Thread implements Runnable {

	List<TaggedObj> _tag_data_lst = null;
	List<Trade> _trades = null;
	TradeService tradeService = DataHibernateUtil.getTradeService();
	TradeIssueMapService tradeIssueService = DataHibernateUtil.getTradeIssueMapService();
	List<TradeIssueMap> _trade_issue_lst = new ArrayList<TradeIssueMap>();

	/**
	 * 
	 */
	public ThreadImportTag(List<TaggedObj> tag_data_lst) {
		// TODO Auto-generated constructor stub
		this._tag_data_lst = tag_data_lst;
	}

	/**
	 * 
	 */
	public ThreadImportTag(List<TaggedObj> tag_data_lst, List<Trade> trades) {
		// TODO Auto-generated constructor stub
		this._tag_data_lst = tag_data_lst;
		this._trades = trades;
	}

	public void run() {
		// TODO Auto-generated method stub
		super.run();
		int count = 0;
		List<Trade> trade_lst = null;
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSSS");
		
		for (TaggedObj obj : _tag_data_lst) {
			System.out.println("-- Get trade start " + df.format(new Date()));
			trade_lst = tradeService.getTradeByCriteria(obj.get_disp_column()); // Get list of trades by predefined filter
			System.out.println("-- Get trade end " + df.format(new Date()));
			for (Trade trade : trade_lst) {
				System.out.println("-- Create trade issue start " + df.format(new Date()));
				for (String s : obj.get_issues()) {
					String mod_s = null;
					try {
						mod_s = s.substring(0, s.lastIndexOf("."));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						mod_s = s;
					} finally {

						TradeIssueMapKey tr_is_map = new TradeIssueMapKey(trade.getTradeNb(), Integer.parseInt(mod_s));
						_trade_issue_lst.add(new TradeIssueMap(tr_is_map, new Date()));
					}

				}
				System.out.println("-- Create trade issue end " + df.format(new Date()));
			}
			System.out.println("-- Item " + count++);
		}

		/*
		 * Save to db TODO invoke batch insertion function
		 */
		tradeIssueService.createTradeIssueMap(_trade_issue_lst);
		System.out.println("End process " + df.format(new Date()));
	}
}
