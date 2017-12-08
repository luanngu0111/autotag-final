/**
 * 
 */
package upskills.autotag.process;

import java.util.ArrayList;
import java.util.Date;
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
	 * @param arg0
	 */
	public ThreadImportTag(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ThreadImportTag(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ThreadImportTag(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ThreadImportTag(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ThreadImportTag(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ThreadImportTag(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ThreadImportTag(ThreadGroup arg0, Runnable arg1, String arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		// TODO Auto-generated method stub
		super.run();
		int count = 0;
		for (TaggedObj obj : _tag_data_lst) {
			List<Trade> trade_lst = tradeService.getTradeByCriteria(obj.get_disp_column()); // Get
																							// list
																							// of
																							// trades
																							// by
																							// predefined
																							// filter
			for (Trade trade : trade_lst) {
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
			}
			System.out.println("-- Item " + count++);
		}

		/*
		 * Save to db TODO invoke batch insertion function
		 */
		tradeIssueService.createTradeIssueMap(_trade_issue_lst);
	}
}
