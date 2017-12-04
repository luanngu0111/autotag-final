package upskills.autotag.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * @author LuanNgu
 *
 */
public class TaggedObj {
	private boolean _selected;
	private HashMap<String,String> _disp_column; 
	
	private String _field_name;
	private boolean _systematic;
	private List<String> _issues;
	/**
	 * 
	 */
	public TaggedObj() {
		super();
		// TODO Auto-generated constructor stub
		_selected = false;
		_disp_column = new HashMap<>();
		_systematic = false;
		_field_name= "";
		_issues = new ArrayList<>();
	}
	
	public TaggedObj(TaggedObj obj) {
		super();
		_selected = obj.is_selected();
		_disp_column = new HashMap<String,String>(obj.get_disp_column());
		_systematic = obj.is_systematic();
		_field_name = new String(obj.get_field_name());
		_issues = new ArrayList<String>(obj.get_issues());
		
	}
	/**
	 * @return the _selected
	 */
	public boolean is_selected() {
		return _selected;
	}
	/**
	 * @param _selected the _selected to set
	 */
	public void set_selected(boolean _selected) {
		this._selected = _selected;
	}
	/**
	 * @return the _disp_column
	 */
	public HashMap<String, String> get_disp_column() {
		return _disp_column;
	}
	/**
	 * @param _disp_column the _disp_column to set
	 */
	public void set_disp_column(HashMap<String, String> _disp_column) {
		this._disp_column = _disp_column;
	}
	/**
	 * @return the _field_name
	 */
	public String get_field_name() {
		return _field_name;
	}
	/**
	 * @param _field_name the _field_name to set
	 */
	public void set_field_name(String _field_name) {
		this._field_name = _field_name;
	}
	/**
	 * @return the _systematic
	 */
	public boolean is_systematic() {
		return _systematic;
	}
	/**
	 * @param _systematic the _systematic to set
	 */
	public void set_systematic(boolean _systematic) {
		this._systematic = _systematic;
	}
	/**
	 * @return the _issues
	 */
	public List<String> get_issues() {
		return _issues;
	}
	/**
	 * @param _issues the _issues to set
	 */
	public void set_issues(List<String> _issues) {
		this._issues = _issues;
	}
	
	public HashMap<String, String> toMap()
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("00Selected", (_selected)?"Y":"N");
		map.putAll(_disp_column);
		map.put("20Fields", _field_name);
		map.put("21Systematic", _systematic?"Y":"N");
		map.put("22Issues", String.join(";", _issues));
		return map;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(_selected?"X":""); sb.append(";");
		
		Set<Entry<String,String>> set = _disp_column.entrySet();
		for (Entry<String,String> entry: set)
		{
			sb.append(entry.getValue());sb.append(";");
		}
		sb.append(_field_name);sb.append(";");
		sb.append(_systematic?"Y":"N");sb.append(";");
		sb.append(String.join(";", _issues));
		
		return sb.toString();
	}
	
	public <T> void setPropertybyName(String prop_name, T value)
	{
		if (prop_name.toLowerCase().equals("selected"))
			this.set_selected((boolean) value);
		else if (prop_name.toLowerCase().equals("field"))
			this.set_field_name((String) value);
		else if (prop_name.toLowerCase().equals("systematic"))
			this.set_systematic((boolean) value);
		else if (prop_name.toLowerCase().compareTo("issue")>=0)
			this._issues.add((String) value);
		else this._disp_column.put(prop_name, (String) value);
	}
	
	
	
	
	
}
