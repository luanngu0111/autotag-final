package upskills.autotag.model;

import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Issue;
import lle.crud.model.Trade;
import lle.crud.service.IssueService;
import lle.crud.service.TradeService;


public class MySqlDB {

	static TradeService tradeService = DataHibernateUtil.getTradeService();
	static IssueService issueService = DataHibernateUtil.getIssueService();
	public static void InsertTrade(Trade t)
	{
		tradeService.createTrade(t);
	}
	
	public static void InsertIssue(Issue i)
	{
		issueService.createIssue(i);
	}
}
