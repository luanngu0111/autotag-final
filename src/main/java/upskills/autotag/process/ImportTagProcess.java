package upskills.autotag.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import excel.reader.service.ExcelReader;
import excel.util.ExcelUtils;
import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Trade;
import lle.crud.model.TradeIssueMap;
import lle.crud.service.TradeIssueMapService;
import lle.crud.service.TradeService;
import ups.mongo.fileprocess.MongoDataUtil;
import ups.mongo.model.AutoTagOutput;
import ups.mongo.service.AutoTagService;
import upskills.autotag.model.TaggedObj;
import upskills.autotag.resource.IConstants;

/**
 * @author LuanNgu
 * 
 */
public class ImportTagProcess {

	private static List<TaggedObj> _tag_data_lst = new ArrayList<TaggedObj>();
	private static List<TradeIssueMap> _trade_issue_lst = new ArrayList<TradeIssueMap>();
	private static TradeService tradeService = DataHibernateUtil.getTradeService();
	private static TradeIssueMapService tradeIssueService = DataHibernateUtil.getTradeIssueMapService();
	static String _report_id;
	static String _report_date, _recon_time;
	private static AutoTagService _autotag_svc = MongoDataUtil.getAutoTagService();

	public enum Source {
		EXCEL, DATABASE
	}

	public static void main(String[] args) {
		try {
			execute(Source.EXCEL);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<TaggedObj> readData(List<String[]> tagData) {
		String[] header = tagData.get(1);
		List<TaggedObj> tagList = new ArrayList<TaggedObj>();
		int size = tagData.size();

		for (int i = 2; i < size; i++) {
			String selected = tagData.get(i)[0];
			if (selected.trim().equals("X") || selected.trim().equals("Y")) {
				TaggedObj tag = new TaggedObj();
				String[] row = tagData.get(i);
				for (int j = 0; j < row.length; j++) {
					tag.setPropertybyName(header[j], row[j]);
				}
				tagList.add(tag);
			}
		}
		return tagList;
	}

	/** Read from excel file
	 * @param filename
	 * @return
	 */
	private static List<TaggedObj> readDatafromExcel(String filename) {
		ExcelReader reader = new ExcelReader();
		ExcelUtils utl = ExcelUtils.getNewInstance();
		utl.set_sheet_id(0);
		List<String[]> tagData = reader.readData(filename, utl);
		return readData(tagData);
	}

	/** Read data from MongoDB
	 * @param reportId
	 * @param reportingDate
	 * @param reconTime
	 * @return
	 */
	private static List<TaggedObj> readDatafromDB(String reportId, String reportingDate, String reconTime) {
		List<AutoTagOutput> list = _autotag_svc.getByReportIdAndReportingDate(reportId, reportingDate);
		AutoTagOutput tags = list.stream().filter(t -> t.getGeneratedDate().toString().equals(reconTime))
				.collect(Collectors.toList()).get(0);
		List<String[]> tagData = tags.getRows().stream().map(row -> row.split("!")).collect(Collectors.toList());
		return readData(tagData);

	}

	/** Save result to MongoDb
	 * @throws InterruptedException
	 */
	private static void saveTagstoDb() throws InterruptedException {

		System.out.println("** Start import");

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

	/** Execute Import process with indication of source
	 * @param source
	 * @throws InterruptedException
	 */
	public static void execute(Source source) throws InterruptedException {
		switch (source) {
		case EXCEL:
			_tag_data_lst = readDatafromExcel(IConstants.EXPORT_EXCEL_FILE);
			break;
		case DATABASE:
			_tag_data_lst = readDatafromDB(_report_id, _report_date,_recon_time);
			break;
		default:
			break;
		}
		saveTagstoDb();
	}

	/** Execute Import from file
	 * @param filepath
	 * @throws InterruptedException
	 */
	public static void execute(String filepath) throws InterruptedException {
		IConstants.EXPORT_EXCEL_FILE = filepath;
		_tag_data_lst = readDatafromExcel(IConstants.EXPORT_EXCEL_FILE);
		saveTagstoDb();
	}

	/** Execute Import from MongoDB
	 * @param reportId
	 * @param reportDate
	 * @throws InterruptedException
	 */
	public static void execute(String reportId, String reportDate, String reconTime) throws InterruptedException {
		_report_id = reportId;
		_report_date = reportDate;
		_recon_time = reconTime;
		_tag_data_lst = readDatafromDB(_report_id, _report_date,_recon_time);
		saveTagstoDb();
	}

}
