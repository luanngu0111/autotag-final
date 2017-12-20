package upskills.autotag.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.map.MultiValueMap;

import excel.reader.service.ExcelReader;
import excel.util.ExcelUtils;
import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Trade;
import lle.crud.model.TradeIssueMap;
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

	private static final int MAX_THREAD = 1; // statelessSession cannot be used in multithread
	private static List<TaggedObj> _tag_data_lst = new ArrayList<TaggedObj>();
	private static List<TradeIssueMap> _trade_issue_lst = new ArrayList<TradeIssueMap>();
	private static TradeService tradeService = DataHibernateUtil.getTradeService();
	private static TradeIssueMapService tradeIssueService = DataHibernateUtil.getTradeIssueMapService();
	static String _report_id;
	static String _report_date;
	private static MongoDataUtil _autotag_svc;

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

	/**
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

	private static List<TaggedObj> readDatafromDB(String reportId, String reportingDate) {
		AutoTagOutput tags = (AutoTagOutput) _autotag_svc.getByReportIdAndReportingDate(reportId, reportingDate).get(0);
		List<String[]> tagData = tags.getRows().stream().map(row -> row.split("!")).collect(Collectors.toList());

		return readData(tagData);

	}

	private static void saveTagstoDb() throws InterruptedException {
		
		System.out.println("** Start import");
		ThreadImportTag[] threads = new ThreadImportTag[MAX_THREAD];

		int size = _tag_data_lst.size();
		List<Trade> trades  = tradeService.getAllTrade();
		
		if (size < MAX_THREAD) {
			ThreadImportTag t = new ThreadImportTag(_tag_data_lst);
			t.start();
			threads[0] = t;
		} else {
			for (int i = 1; i <= MAX_THREAD; i++) {
				int pos_start = size / MAX_THREAD * (i - 1);
				int pos_end = (i == MAX_THREAD) ? size  : size / MAX_THREAD * i ;
				List<TaggedObj> sub_list = _tag_data_lst.subList(pos_start, pos_end);
				ThreadImportTag t = new ThreadImportTag(sub_list,trades);
				System.out.println("**[THREAD] " + i + " batch size " + pos_end);
				t.start();
				threads[i - 1] = t;
			}

			for (ThreadImportTag t : threads) {
				t.join();
			}
		}
		
	}

	public static void execute(Source source) throws InterruptedException {
		switch (source) {
		case EXCEL:
			_tag_data_lst = readDatafromExcel(IConstants.EXPORT_EXCEL_FILE);
			break;
		case DATABASE:
			_tag_data_lst = readDatafromDB(_report_id, _report_date);
			break;
		default:
			break;
		}
		saveTagstoDb();
	}

}
