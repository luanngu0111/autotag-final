package upskills.autotag;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import excel.reader.service.ExcelReader;
import excel.util.ExcelUtils;
import ups.mongo.excelutil.ExcelUtilsIn;
import ups.mongo.fileprocess.MongoDataUtil;
import ups.mongo.model.AutoTagOutput;
import ups.mongo.model.ReconInputMx2;
import ups.mongo.service.AutoTagService;
import ups.mongo.service.ReconInputServiceMx2;
import upskills.autotag.resource.IConstants;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		/*ExcelUtils utl = ExcelUtils.getInstance();
		utl.set_sheet_id(0);
		utl.set_splitter(",");
		ExcelReader reader = new ExcelReader();
		List<String[]> data = reader.readData("import/Scene2_MX2.csv", utl);
		String header =String.join("!",data.get(0));
		List<String> content= data.subList(1, data.size()-1).stream().map(row->String.join("!", row)).collect(Collectors.toList());
		MongoDataUtil service = new MongoDataUtil(ReconInputServiceMx2.class);
		ReconInputMx2 ao = new ReconInputMx2("XXX", "xxxx", "2017 may 13", new Date(), header, content);
		
		service.saveToMongoDB(ao);*/
		
		System.out.println("issue 1st".contains("issue"));
		
		
	}
}
