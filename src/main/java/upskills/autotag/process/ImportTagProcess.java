package upskills.autotag.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import excel.reader.service.ExcelReader;
import excel.util.ExcelUtils;
import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Trade;
import lle.crud.model.TradeIssueMap;
import lle.crud.model.TradeIssueMapKey;
import lle.crud.service.TradeIssueMapService;
import lle.crud.service.TradeService;
import ups.mongo.fileprocess.MongoDataUtil;
import ups.mongo.model.AutoTagOutput;
import upskills.autotag.model.TaggedObj;
import upskills.autotag.resource.IConstants;

/**
 * @author LuanNgu
 * 
 */
public class ImportTagProcess {

	private static List<TaggedObj> _tag_data_lst = new ArrayList<TaggedObj>();
	private static TradeService tradeService = DataHibernateUtil.getTradeService();
	private static TradeIssueMapService tradeIssueService = DataHibernateUtil.getTradeIssueMapService();
	static String _report_id;
	static String _report_date;
	private static MongoDataUtil _autotag_svc;
	public enum Source {
		EXCEL, DATABASE
	}

	public static void main(String[] args) {
		execute(Source.EXCEL);
	}

	private static List<TaggedObj> readData(List<String[]> tagData)
	{
		String[] header = tagData.get(1);
		List<TaggedObj> tagList = new ArrayList<TaggedObj>();
		int size = tagData.size();

		for (int i = 2; i < size; i++) {
			String selected = tagData.get(i)[0];
			if (selected.equals("X") || selected.equals("Y")) {
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
	/**
	 * @param filename
	 * @return
	 */
	private static List<TaggedObj> readDatafromExcel(String filename) {
		ExcelReader reader = new ExcelReader();
		ExcelUtils utl = ExcelUtils.getNewInstance();
		utl.set_sheet_id(2);
		List<String[]> tagData = reader.readData(filename, utl);
		return readData(tagData);
	}

	private static List<TaggedObj> readDatafromDB(String reportId, String reportingDate) {
		AutoTagOutput tags = (AutoTagOutput) _autotag_svc.getByReportIdAndReportingDate(reportId, reportingDate).get(0);
		List<String[]> tagData = tags.getRows().stream().map(row->row.split("!")).collect(Collectors.toList());
		
		return readData(tagData);

	}

	private static void saveTagstoDb() {
		for (TaggedObj obj : _tag_data_lst) {
			List<Trade> trade_lst = tradeService.getTradeByCriteria(obj.get_disp_column());
			for (Trade trade : trade_lst) {
				for (String s : obj.get_issues()) {
					TradeIssueMapKey tr_is_map = new TradeIssueMapKey(trade.getTradeNb(), Integer.parseInt(s));
					tradeIssueService.createTradeIssueMap(new TradeIssueMap(tr_is_map, new Date()));
				}
			}

		}
	}

	public static void execute(Source source) {
		switch (source) {
		case EXCEL:
			readDatafromExcel(IConstants.EXPORT_EXCEL_FILE);
			break;
		case DATABASE:
			readDatafromDB(_report_id,_report_date);
			break;
		default:
			break;
		}
		saveTagstoDb();
	}

}
