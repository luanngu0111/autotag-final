package upskills.autotag.resource;

public class IConstants {
	public static String HOST_NAME = "localhost";
	public static String PORT = "3306";
	public static String USER_NAME = "root";
	public static String PASSWORD = "root";
	public static String DB_NAME = "recondb";
	public static String CSV_SPLIT = ",";
	public static String FILE_PATH = "import/IRD_PL_1__20171107_201223.csv"; //Scene2_MX2__20171024_165930.xls mx2_test2__20171026_125441
	public static String HEADER_TRADE = "TRADE_NB";
	public static String HEADER_FAMILY = "TRN_FMLY";
	public static String HEADER_GROUP = "TRN_GRP";
	public static String HEADER_TYPE = "TRN_TYPE";
	public static String HEADER_CURR = "CURRENCY";
	public static String HEADER_PORT = "PORTFOLIO";
	public static String HEADER_INS = "INSTRUMENT";
	public static String HEADER_STATUS = "TRN_STATUS";
	public static String HEADER_KEY_VAL = "KeyValue";
	public static String EXPORT_EXCEL_FILE = "export/test.xlsx";
	public static String AUTOTAG_TABLE = "AutoTagOutput";
	public static String EXCEL_SHEET_NAME = "Result 0";
	public static String EXCEL_EXPORT_SHEET = "AUTO-TAGGING";
	public static int EXCEL_SHEET_ID = 2;
	public static String[] IGNORE_COLUMN = new String[] { "SourceName", "LineNumber" };
	public static String[] EXPORT_HEADER = new String[] { "Selected", "Trade Number", "Trade Family", "Trade Group", "Trade Type",
			"Currency", "Field", "Systematic", "Issue 1st", "Issue 2nd", "Issue 3rd", "Issue 4th", "Issue 5th",
			"Issue 6th", "Issue 7th", "Issue 8th" ,"Issue 9th" ,"Issue 10th" , "Issue", "Comment"};
	
	public static String[] EXPORT_HEADER_FULL = new String[] { "Selected", "Portfolio", "Instrument", "Trade Number", "Trade Family", "Trade Group", "Trade Type",
			"Currency", "Field", "Systematic", "Issue 1st", "Issue 2nd", "Issue 3rd", "Issue 4th", "Issue 5th",
			"Issue 6th", "Issue 7th", "Issue 8th" ,"Issue 9th" ,"Issue 10th" , "Issue", "Comment"};
	
	public static String[] EXPORT_HEADER_NON_TRADE = new String[] { "Selected", "Portfolio", "Instrument", 
			"Field", "Systematic", "Issue 1st", "Issue 2nd", "Issue 3rd", "Issue 4th", "Issue 5th",
			"Issue 6th", "Issue 7th", "Issue 8th" ,"Issue 9th" ,"Issue 10th" , "Issue", "Comment"};
	public static String[] EXPORT_HEADER_NEUTRAL = new String[] { "Selected", "Field", "Systematic", 
			"Issue 1st", "Issue 2nd", "Issue 3rd", "Issue 4th", "Issue 5th",
			"Issue 6th", "Issue 7th", "Issue 8th", "Issue 9th" ,"Issue 10th" , "Issue", "Comment"};
}
