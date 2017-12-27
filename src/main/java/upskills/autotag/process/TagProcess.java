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
	static String reportId, reportingDate, reconTime;
	static final int MAX_THREAD = 4;

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
		int i = 1; //ignore header row
		String[] header = mm_result.get(0); // extract header
		
		int length = header.length;
		int size = mm_result.size();
		List<TaggedObj> mm_table = new ArrayList<>();
		for (; i < size - 1; i = i + 2) { 
			String[] data1 = (mm_result.get(i));
			String[] data2 = (mm_result.get(i + 1)); 
			length = (length > data1.length)?data1.length:length;
			TaggedObj result = new TaggedObj();
			for (int j = 3; j < length; j++) {

				result.set_selected(false);
				result.set_systematic(false);
				if (data1[j].compareToIgnoreCase(data2[j]) == 0) {  // if data is matched
					result.get_disp_column().put(HeaderMap.MapHeaderToColumn(header[j]), data1[j]); // store key column header and its data
				} else { // if not matched
					result.set_field_name(header[j]); //store mismatch column header
					mm_table.add(new TaggedObj(result)); 
				}

			}
		}
		return mm_table;
	}

	private static List<String[]> readFromDb(String reportId, String reportingDate, String reconTime) {
		
		List<String[]> mm_result = new ArrayList<String[]>();
		ReconOutputService service = MongoDataUtil.getReconOutputService();
		
		//Get list of output by ReportID and ReportDates
		List<ReconOutput> list = new ArrayList<ReconOutput>(service.getByReportIdAndReportingDate(reportId, reportingDate));
		
		//Extract expected output by Recontime.
		ReconOutput ro = list.stream().filter(t->t.getReconTime().toString().equalsIgnoreCase(reconTime)).collect(Collectors.toList()).get(0);
		mm_result = ro.getRows().stream().map(o -> o.split("!")).collect(Collectors.toList());
		int pos_missing = mm_result.size() - 1;
		String[] s = mm_result.get(pos_missing);

		while (s[0].equalsIgnoreCase("mx2") || s[0].equalsIgnoreCase("mx3")){ //ignore the missing data rows
			pos_missing--;
			s = mm_result.get(pos_missing);
		}
		return new ArrayList<String[]>(mm_result.subList(0, pos_missing+1));

	}

	/**
	 * @author LuanNgu
	 * @param key_headers
	 * @throws Exception
	 *             Description: to tag issue for trade
	 */
	public static void execute(String rep_id, String rep_date, String recon_time, List<String> key_headers) throws Exception {
		getTagByKeyColumn(rep_id,rep_date,recon_time, key_headers);
	}
	
	/** @author LuanNgu
	 * @param rep_id report id/code
	 * @param rep_date reporting date
	 * @param recon_time time when executed report
	 * @param key_headers String[] list of column name which are comparing key
	 * @throws Exception
	 * 
	 */
	public static void execute(String rep_id, String rep_date, String recon_time, String[] key_headers) throws Exception {
		List<String> list_headers = new ArrayList<String>(Arrays.asList(key_headers));
		getTagByKeyColumn(rep_id,rep_date,recon_time, list_headers);
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
		List<String> mod_key_head = HeaderMap.MapHeaderToColumn(key_headers); //Normalize header name

		ExcelUtils utl = ExcelUtils.getInstance();
		utl.set_splitter(IConstants.CSV_SPLIT);
		utl.set_sheet_id(2);

		/* ExcelReader reader = new ExcelReader();
		 mm_result = reader.readData(IConstants.FILE_PATH, utl);*/
		
		
		mm_result = readFromDb(reportId, reportingDate, reconTime); //read data from DB and store in list of String array
		mm_table = extractMismatchColumn(mm_result); // transform mismatch data to Tagged Object

		ThreadTag[] threads = new ThreadTag[MAX_THREAD];

		int size = mm_table.size();
		if (size < MAX_THREAD)
		{
			ThreadTag t = new ThreadTag(mm_table, mod_key_head);
			t.start();
			threads[0] = t;
		}
		for (int i = 1; i <= MAX_THREAD; i++) {
			int pos_start = size / MAX_THREAD * (i - 1);
			int pos_end = (i == MAX_THREAD) ? size : size / MAX_THREAD * i;
			List<TaggedObj> sub_list = new ArrayList<TaggedObj>(mm_table.subList(pos_start, pos_end));
			ThreadTag t = new ThreadTag(sub_list, mod_key_head);
			t.start();
			threads[i - 1] = t;

		}

		for (ThreadTag t : threads) {
			table_result.addAll(t.getDataList());
			t.join();
		}

		List<String[]> output = new ArrayList<String[]>();
		

//		Config excel file
		 ExcelWriter writer = new ExcelWriter();
		 utl.set_sheet_name(IConstants.EXCEL_EXPORT_SHEET);

		// Prepare header
		exportHeader.addAll(1, key_headers);
		output.add(exportHeader.toArray(new String[0]));
		output.addAll(table_result.parallelStream().map(m -> m.toString().split("!")).collect(Collectors.toList()));
		
		/*int pivot_start = exportHeader.indexOf(IConstants.EXPORT_HEADER_NEUTRAL[3]);
		int pivot_end = exportHeader.indexOf(IConstants.EXPORT_HEADER_NEUTRAL[12]);
				Format excel option
		 ExcelFormat format = new ExcelFormat();
		 format.setBorderCell(true, true, true, true, BorderStyle.MEDIUM);
		 format.mergeCell(0, 0, 0, pivot_start - 1, "MisMatch");
		 format.mergeCell(0, 0, pivot_start, pivot_end, "Auto-Tagging");
		 format.mergeCell(0, 0, pivot_end + 1, pivot_end + 2, "User Input");

			 Export to file
		 utl.set_format(format);
		 writer.writeData(output, IConstants.EXPORT_EXCEL_FILE, utl);*/

		
		
		// Save to MongoDB
		AutoTagService service = MongoDataUtil.getAutoTagService();
		AutoTagOutput ao = new AutoTagOutput();
		ao.setGeneratedDate(new Date());
		ao.setReportId(reportId);
		ao.setReportName(reportId);
		ao.setHeaders(exportHeader);
		ao.setRows(output.subList(1, output.size()).stream().map(o -> String.join("!", o))
				.collect(Collectors.toList()));
		service.saveToMongoDB(ao);

		
		//Export to file
		printExcelFile(output);
		
		System.out.print((new Date()).toString());
	}

	/** @author LuanNgu
	 * @param rep_id
	 * @param rep_date
	 * @param recon_time
	 * @param key_headers
	 * @throws Exception
	 */
	public static void getTagByKeyColumn(String rep_id, String rep_date, String recon_time, List<String> key_headers)
			throws Exception {

		reportId = rep_id;
		reportingDate = rep_date;
		reconTime = recon_time;

		getTagByKeyColumn(key_headers);

	}
	
	private static void printExcelFile(List<String[]> output) throws Exception
	{
		// Config excel file
		ExcelWriter writer = new ExcelWriter();
		ExcelUtils utl = ExcelUtils.getInstance();
		utl.set_sheet_name(IConstants.EXCEL_EXPORT_SHEET);

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
	}
}
