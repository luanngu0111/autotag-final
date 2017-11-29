package upskills.autotag.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;

import excel.reader.service.ExcelReader;
import excel.util.ExcelFormat;
import excel.util.ExcelUtils;
import excel.writer.service.ExcelWriter;
import lle.crud.data.util.DataHibernateUtil;
import lle.crud.model.Issue;
import lle.crud.model.Trade;
import lle.crud.service.TradeIssueMapService;
import lle.crud.service.TradeService;
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

	public static void main(String[] args) {
		String[] headers = new String[] { "TRN_NB" };
		try {
			GetTagByKeyColumn(Arrays.asList(headers));
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

	/** put key column to get corresponding issue tag
	 * @param key_headers
	 * @throws Exception
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static void GetTagByKeyColumn(List<String> key_headers) throws Exception {
		List<String[]> mm_result = new ArrayList<>();
		List<TaggedObj> mm_table = new ArrayList<>();
		HashMap<String, String> key_val = null;
		List<String> mod_key_head = HeaderMap.MapHeaderToColumn(key_headers);
		
		ExcelUtils utl = ExcelUtils.getInstance();
		utl.set_splitter(IConstants.CSV_SPLIT);

		ExcelReader reader = new ExcelReader();
		mm_result = reader.readData(IConstants.FILE_PATH, utl);

		mm_table = extractMismatchColumn(mm_result);

		for (Iterator iterator = mm_table.iterator(); iterator.hasNext();) {
			TaggedObj taggedObj = (TaggedObj) iterator.next();
			List<Trade> trades = new ArrayList<>();
			List<Trade> org_trades=null;
			List<Trade> trade_issues = new ArrayList<>();

			key_val = (HashMap<String, String>) taggedObj.get_disp_column().entrySet().stream().filter(k->mod_key_head.contains(k.getKey())).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
			taggedObj.set_disp_column(key_val);
			trades = tradeService.getTradeByCriteria(key_val);
			trades  = trades.stream().filter(t -> t.getIssueList() != null || t.getIssueList().size() > 0)
					.collect(Collectors.toList()); // SELECT TRADE WHICH HAVE ISSUE
			org_trades = new ArrayList<Trade>(trades);
			
			if (trades != null && trades.size() > 0) {
				for (int i =0 ; i < trades.size(); i++) {
					Trade trade = new Trade(trades.get(i));
					trade.setIssueList(trade.getIssueList().stream()
							.filter(is -> is.getField().equalsIgnoreCase(taggedObj.get_field_name()))
							.collect(Collectors.toList()));
					if (trade.getIssueList().size() > 0)
						trade_issues.add(trade);
				}

				/*
				 * Key and Field name exists in DB
				 */
				if (trade_issues.size() > 0) {
					List<Issue> issues = trade_issues.stream().map(Trade::getIssueList).flatMap(x->x.stream()).collect(Collectors.toList());
					taggedObj.set_issues(
							issues.stream().map(is -> String.valueOf(is.getIssueId())).collect(Collectors.toList()));
					taggedObj.set_systematic(true);
				} else { // Key and Field name NOT exists in DB
					List<Issue> issues = org_trades.stream().map(Trade::getIssueList).flatMap(x->x.stream()).collect(Collectors.toList());
					taggedObj.set_issues(
							issues.stream().map(is -> String.valueOf(is.getIssueId())).collect(Collectors.toList()));
				}
			} else {
				// TODO implement for NOT existed Trade
			}
		}
		List<String[]> output  = new ArrayList<String[]>();
		ExcelWriter writer = new ExcelWriter();
		
		// Config excel file
		utl.set_sheet_name(IConstants.EXCEL_EXPORT_SHEET);
		
		// Prepare header
		exportHeader.addAll(1, key_headers);
		output.add(exportHeader.toArray(new String[0]));
		output.addAll(mm_table.parallelStream().map(m->m.toString().split(";")).collect(Collectors.toList()));
		
		// Format excel option
		ExcelFormat format = new ExcelFormat();
		format.setBorderCell(true, true, true, true, BorderStyle.MEDIUM);
		utl.set_format(format);
		writer.writeData(output, IConstants.EXPORT_EXCEL_FILE, utl);

	}
}
