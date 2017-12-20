/**
 * 
 */
package upskills.autotag.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lle.crud.data.util.DataHibernateUtil;
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

	
	public void run()
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSSS");
		List<HashMap<String,String>> trade_issue = new ArrayList<HashMap<String,String>>();
		int count=0;
		for (TaggedObj obj : _tag_data_lst) {
			HashMap<String, String> trades = new HashMap<String,String>();
			trades.putAll(obj.get_disp_column());
			trades.put("issue", obj.get_issues().get(0));
			trade_issue.add(trades);
			System.out.println("*** [NUMBER] "+ count++);
		}
		
		for (HashMap<String, String> hashMap : trade_issue) {
			tradeIssueService.insertTradeIssue(hashMap);
		}
		System.out.println("End process " + df.format(new Date()));
	}
	
	public void execute(){
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSSS");
		List<HashMap<String,String>> trade_issue = new ArrayList<HashMap<String,String>>();
		int count=0;
		for (TaggedObj obj : _tag_data_lst) {
			HashMap<String, String> trades = new HashMap<String,String>();
			trades.putAll(obj.get_disp_column());
			trades.put("issue", obj.get_issues().get(0));
			trade_issue.add(trades);
			System.out.println("*** [NUMBER] "+ count++);
		}
		
		for (HashMap<String, String> hashMap : trade_issue) {
			tradeIssueService.insertTradeIssue(hashMap);
		}
		System.out.println("End process " + df.format(new Date()));
		
	}
}
