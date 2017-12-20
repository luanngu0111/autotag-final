package upskills.autotag.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;

import excel.reader.service.ExcelReader;
import excel.util.ExcelFormat;
import excel.util.ExcelUtils;
import excel.writer.service.ExcelWriter;
import lle.crud.data.util.DataHibernateUtil;
import lle.crud.service.TradeIssueMapService;
import lle.crud.service.TradeService;
import ups.mongo.fileprocess.MongoDataUtil;
import ups.mongo.model.AutoTagOutput;
import ups.mongo.model.ReconOutput;
import ups.mongo.service.AutoTagService;
import ups.mongo.service.ReconOutputService;
import upskills.autotag.model.HeaderMap;
import upskills.autotag.model.TaggedObj;
import upskills.autotag.resource.IConstants;

/**
 * @author LuanNgu
 *
 */
public class TagProcess {

	static TradeService tradeService = DataHibernateUtil.getTradeService();
	static TradeIssueMapService tradeIssueService = DataHibernateUtil.getTradeIssueMapService();
	static List<String> exportHeader = new ArrayList<String>(Arrays.asList(IConstants.EXPORT_HEADER_NEUTRAL));
	static String reportId, reportingDate;
	static final int MAX_THREAD = 32;

	public static void main(String[] args) {
		String[] headers = new String[] { "TRN_NB" };
		try {
			getTagByKeyColumn(Arrays.asList(headers));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<TaggedObj> extractMismatchColumn(List<String[]> mm_result) throws CloneNotSupportedException {
		int i = 1;
		String[] header = mm_result.get(0);
		int size = mm_result.size();
		List<TaggedObj> mm_table = new ArrayList<>();
		for (; i < size - 1; i = i + 2) {
			String[] data1 = (mm_result.get(i));
			String[] data2 = (mm_result.get(i + 1));
			int length = data1.length;
			TaggedObj result = new TaggedObj();
			for (int j = 3; j < length; j++) {

				result.set_selected(false);
				result.set_systematic(false);
				if (data1[j].compareToIgnoreCase(data2[j]) == 0) {
					result.get_disp_column().put(HeaderMap.MapHeaderToColumn(header[j]), data1[j]);
				} else {
					result.set_field_name(header[j]);
					mm_table.add(new TaggedObj(result));
				}

			}
		}
		return mm_table;
	}

	private static List<String[]> readFromDb(String reportId, String reportingDate) {
		List<String[]> mm_result = new ArrayList<String[]>();
		ReconOutputService service = MongoDataUtil.getReconOutputService();
		ReconOutput ro = (ReconOutput) service.getByReportIdAndReportingDate(reportId, reportingDate).get(0);
		mm_result = ro.getRows().stream().map(o -> o.split("!")).collect(Collectors.toList());
		return mm_result;

	}

	/**
	 * @author LuanNgu
	 * @param key_headers
	 * @throws Exception
	 *             Description: to tag issue for trade
	 */
	public static void execute(List<String> key_headers) throws Exception {
		getTagByKeyColumn(key_headers);
	}

	/**
	 * put key column to get corresponding issue tag
	 * 
	 * @param key_headers
	 * @throws Exception
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static void getTagByKeyColumn(List<String> key_headers) throws Exception {
		List<String[]> mm_result = new ArrayList<>();
		List<TaggedObj> mm_table, table_result = new ArrayList<>();
		HashMap<String, String> key_val = null;
		List<String> mod_key_head = HeaderMap.MapHeaderToColumn(key_headers);

		ExcelUtils utl = ExcelUtils.getInstance();
		utl.set_splitter(IConstants.CSV_SPLIT);
		utl.set_sheet_id(2);

		ExcelReader reader = new ExcelReader();
		 mm_result = reader.readData(IConstants.FILE_PATH, utl);
//		mm_result = readFromDb(reportId, reportingDate);
		mm_table = extractMismatchColumn(mm_result);

		ThreadTag[] threads = new ThreadTag[MAX_THREAD];

		int size = mm_result.size();
		if (size < MAX_THREAD)
		{
			ThreadTag t = new ThreadTag(mm_table, mod_key_head);
			t.start();
			threads[0] = t;
		}
		for (int i = 1; i <= MAX_THREAD; i++) {
			int pos_start = size / MAX_THREAD * (i - 1);
			int pos_end = (i == MAX_THREAD) ? size - 1 : size / MAX_THREAD * i - 1;
			List<TaggedObj> sub_list = mm_table.subList(pos_start, pos_end);
			ThreadTag t = new ThreadTag(sub_list, mod_key_head);
			t.start();
			threads[i - 1] = t;

		}

		for (ThreadTag t : threads) {
			table_result.addAll(t.getDataList());
			t.join();
		}

		List<String[]> output = new ArrayList<String[]>();
		ExcelWriter writer = new ExcelWriter();

		// Config excel file
		utl.set_sheet_name(IConstants.EXCEL_EXPORT_SHEET);

		// Prepare header
		exportHeader.addAll(1, key_headers);
		output.add(exportHeader.toArray(new String[0]));
		output.addAll(table_result.parallelStream().map(m -> m.toString().split(";")).collect(Collectors.toList()));
		int pivot_start = exportHeader.indexOf(IConstants.EXPORT_HEADER_NEUTRAL[3]);
		int pivot_end = exportHeader.indexOf(IConstants.EXPORT_HEADER_NEUTRAL[12]);
		// Format excel option
		ExcelFormat format = new ExcelFormat();
		format.setBorderCell(true, true, true, true, BorderStyle.MEDIUM);
		format.mergeCell(0, 0, 0, pivot_start - 1, "MisMatch");
		format.mergeCell(0, 0, pivot_start, pivot_end, "Auto-Tagging");
		format.mergeCell(0, 0, pivot_end + 1, pivot_end + 2, "User Input");

		// Export to file
		utl.set_format(format);
		writer.writeData(output, IConstants.EXPORT_EXCEL_FILE, utl);

		// Save to MongoDB

		AutoTagService service = MongoDataUtil.getAutoTagService();
		AutoTagOutput ao = new AutoTagOutput();
		ao.setGeneratedDate(new Date());
		ao.setReportId(IConstants.FILE_PATH.substring(IConstants.FILE_PATH.indexOf("\\") + 1,
				IConstants.FILE_PATH.lastIndexOf(".")));
		ao.setReportName(IConstants.FILE_PATH.substring(IConstants.FILE_PATH.indexOf("\\") + 1,
				IConstants.FILE_PATH.lastIndexOf(".")));
		ao.setHeaders(exportHeader);
		ao.setRows(output.subList(1, output.size() - 1).stream().map(o -> String.join("!", o))
				.collect(Collectors.toList()));
		service.saveToMongoDB(ao);

		System.out.print((new Date()).toString());
	}

}
