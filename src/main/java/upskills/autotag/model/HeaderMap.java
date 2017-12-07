package upskills.autotag.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import upskills.autotag.resource.IConstants;


public class HeaderMap {

	public HeaderMap() {
		// TODO Auto-generated constructor stub
	}

	private static String[] trade_number_head = new String[] { "NB", "TRN_NB", "M_NB", "TRADE NUMBER" };
	private static String[] family_head = new String[] { "FAM", "FAMILY", "TRN_FMLY", "M_TRN_FMLY" };
	private static String[] group_head = new String[] { "GROUP", "TRN_GRP", "M_TRN_GRP" };
	private static String[] type_head = new String[] { "TYPE", "TYP", "TRN_TYPE", "M_TRN_TYPE" };
	private static String[] portfolio_head = new String[] { "PORT", "PORTFOLIO", "TP_PFOLIO", "M_TP_PFOLIO" };
	private static String[] instrument_head = new String[] { "INSTRUMENT", "M_INSTRUMENT" };
	private static String[] currency_head = new String[] { "CURRENCY", "TP_FXBASE", "M_TP_FXBASE" };

	public static void addHeader(String[] array, String head) {
		if (head.trim().equals(""))
			return;
		int size = array.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(array, 0, temp, 1, size);
		array = temp.clone();
	}

	public static void addTradeHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = trade_number_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(trade_number_head, 0, temp, 1, size);
		trade_number_head = temp.clone();
	}

	public static void addFmlyHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = family_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(family_head, 0, temp, 1, size);
		family_head = temp.clone();
	}

	public static void addGrpHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = group_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(group_head, 0, temp, 1, size);
		group_head = temp.clone();
	}

	public static void addTypeHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = type_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(type_head, 0, temp, 1, size);
		type_head = temp.clone();
	}

	public static void addPortHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = portfolio_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(portfolio_head, 0, temp, 1, size);
		portfolio_head = temp.clone();
	}

	public static void addInstrumentHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = instrument_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(instrument_head, 0, temp, 1, size);
		instrument_head = temp.clone();
	}

	public static void addCurrHeader(String head) {
		if (head.trim().equals(""))
			return;
		int size = currency_head.length;
		String[] temp = new String[size + 1];
		temp[0] = head;
		System.arraycopy(currency_head, 0, temp, 1, size);
		currency_head = temp.clone();
	}

	public static void exportPreferences(String filename) throws IOException {
		FileWriter writer = new FileWriter(filename);
		writer.append("trade_number_head:" + String.join(",", trade_number_head) + "\n");
		writer.append("family_head:" + String.join(",", family_head) + "\n");
		writer.append("group_head:" + String.join(",", group_head) + "\n");
		writer.append("type_head:" + String.join(",", type_head) + "\n");
		writer.append("portfolio_head:" + String.join(",", portfolio_head) + "\n");
		writer.append("instrument_head:" + String.join(",", instrument_head) + "\n");
		writer.append("currency_head:" + String.join(",", currency_head) + "\n");

		writer.flush();
		writer.close();

	}

	public static void importPreferences(String filename) {
		BufferedReader br = null;
		FileReader fr = null;
		try {

			// br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				String title = sCurrentLine.substring(0, sCurrentLine.indexOf(":"));
				if (title.equals("trade_number_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					trade_number_head = temp;
				}
				if (title.equals("family_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					family_head = temp;
				}
				if (title.equals("group_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					group_head = temp;
				}
				if (title.equals("type_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					type_head = temp;
				}
				if (title.equals("portfolio_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					portfolio_head = temp;
				}
				if (title.equals("instrument_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					instrument_head = temp;
				}
				if (title.equals("currency_head")) {
					String header = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
					String[] temp = header.split(",\\s*");
					currency_head = temp;
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

	public static String MapHeaderToColumn(String header) {
		if (Arrays.asList(trade_number_head).contains(header.toUpperCase()))
			return IConstants.HEADER_TRADE;
		if (Arrays.asList(family_head).contains(header.toUpperCase()))
			return IConstants.HEADER_FAMILY;
		if (Arrays.asList(group_head).contains(header.toUpperCase()))
			return IConstants.HEADER_GROUP;
		if (Arrays.asList(type_head).contains(header.toUpperCase()))
			return IConstants.HEADER_TYPE;
		if (Arrays.asList(portfolio_head).contains(header.toUpperCase()))
			return IConstants.HEADER_PORT;
		if (Arrays.asList(instrument_head).contains(header.toUpperCase()))
			return IConstants.HEADER_INS;
		if (Arrays.asList(currency_head).contains(header.toUpperCase()))
			return IConstants.HEADER_CURR;
		return header;
	}

	public static List<String> MapHeaderToColumn(List<String> headers) {
		List<String> result = new ArrayList<String>();
		for (String header : headers) {
			result.add(MapHeaderToColumn(header));
		}
		return result;
	}

	public static String[] MapHeaderToColumn(String[] headers) {
		String[] result = new String[headers.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = MapHeaderToColumn(headers[i]);
		}

		return result;
	}
}
