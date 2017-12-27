package upskills.autotag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ups.mongo.fileprocess.MongoDataUtil;
import ups.mongo.service.AutoTagService;
import ups.mongo.service.ClusterOutputService;
import upskills.autotag.model.HeaderMap;
import upskills.autotag.process.ImportTagProcess;
import upskills.autotag.process.TagProcess;
import upskills.autotag.resource.IConstants;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		/*
		 * ExcelUtils utl = ExcelUtils.getInstance(); utl.set_sheet_id(0);
		 * utl.set_splitter(","); ExcelReader reader = new ExcelReader(); List<String[]>
		 * data = reader.readData("import/Scene2_MX2.csv", utl); String header
		 * =String.join("!",data.get(0)); List<String> content= data.subList(1,
		 * data.size()-1).stream().map(row->String.join("!",
		 * row)).collect(Collectors.toList()); MongoDataUtil service = new
		 * MongoDataUtil(ReconInputServiceMx2.class); ReconInputMx2 ao = new
		 * ReconInputMx2("XXX", "xxxx", "2017 may 13", new Date(), header, content);
		 * 
		 * service.saveToMongoDB(ao);
		 */

		// System.out.println("issue 1st".contains("issue"));

		AutoTagService ats = MongoDataUtil.getAutoTagService();
		List<String> rows = ats.getByReportId("R320").get(0).getRows();
		List<String> headers = ats.getByReportId("R320").get(0).getHeaders();
		
		List<String[]> outputAT = new ArrayList<String[]>(); 
		
		//Add header to output
		String[] aHeaders = new String[headers.size()];
		headers.toArray(aHeaders);		
		outputAT.add(aHeaders);
		
		//Add rows to output
		for (String string : rows) {
			System.out.println(string);
			String[] temp = string.split("!");
			outputAT.add(temp);
		}
		
		//print out
		for (String[] strings : outputAT) {
			System.out.println(String.join("!", strings));
		}
		
		//Write to excel
		TagProcess.printExcelFile(outputAT, "TestAutotag.xlsx");
		
		
		/*try {
			// ImportTagProcess.execute(IConstants.EXPORT_EXCEL_FILE);
			TagProcess.execute("R320", "2017 Sep 20", "Tue Dec 26 14:43:03 ICT 2017", new String[] { "Trade_number" });
			// Export to file

			List<String[]> outputData = TagProcess.getOutputData();
			TagProcess.printExcelFile(outputData, "TestAutotag.xlsx");

			System.out.println();
			System.out.println("-----------------------------");
			for (String[] strings : outputData) {
				System.out.println(String.join("!", strings));
			}
			// HeaderMap.addTradeHeader("");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}
