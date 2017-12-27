package upskills.autotag.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;

/**
 * @author LuanNgu
 *
 */
public class TaggedObj {
	private boolean _selected;
	private HashMap<String, String> _disp_column;

	private String _field_name;
	private boolean _systematic;
	private List<String> _issues;
	static final String _sep="!";

	/**
	 * 
	 */
	public TaggedObj() {
		super();
		// TODO Auto-generated constructor stub
		_selected = false;
		_disp_column = new HashMap<>();
		_systematic = false;
		_field_name = "";
		_issues = new ArrayList<>();
	}

	public TaggedObj(TaggedObj obj) {
		super();
		_selected = obj.is_selected();
		_disp_column = new HashMap<String, String>(obj.get_disp_column());
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
	 * @param _selected
	 *            the _selected to set
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
	 * @param _disp_column
	 *            the _disp_column to set
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
	 * @param _field_name
	 *            the _field_name to set
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
	 * @param _systematic
	 *            the _systematic to set
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
	 * @param _issues
	 *            the _issues to set
	 */
	public void set_issues(List<String> _issues) {
		this._issues = _issues;
	}

	public HashMap<String, String> toMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("00Selected", (_selected) ? "Y" : "N");
		map.putAll(_disp_column);
		map.put("20Fields", _field_name);
		map.put("21Systematic", _systematic ? "Y" : "N");
		map.put("22Issues", String.join(";", _issues));
		return map;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(_selected ? "X" : "");
		sb.append(_sep);

		Set<Entry<String, String>> set = _disp_column.entrySet();
		for (Entry<String, String> entry : set) {
			sb.append(entry.getValue());
			sb.append(_sep);
		}
		sb.append(_field_name);
		sb.append(_sep);
		sb.append(_systematic ? "Y" : "N");
		sb.append(_sep);
		sb.append(String.join(_sep, _issues));

		return sb.toString();
	}

	public <T> void setPropertybyName(String prop_name, T value) {
		if (prop_name.toLowerCase().equals("selected")) {
			String v = (String) value;
			boolean isSelected = false;
			if (v.equalsIgnoreCase("X") || v.equalsIgnoreCase("Y"))
				isSelected = true;
			this.set_selected(isSelected);
		} else if (prop_name.toLowerCase().equals("field"))
			this.set_field_name((String) value);
		else if (prop_name.toLowerCase().equals("systematic")) {
			String v = (String) value;
			boolean isSystem = false;
			if (v.equalsIgnoreCase("Y"))
				isSystem = true;
			this.set_systematic(isSystem);
		} else if (prop_name.toLowerCase().contains("issue"))
			this._issues.add((String) value);
		else {
			this._disp_column.put(HeaderMap.MapHeaderToColumn(prop_name), (String) value);
		}
	}
	
	public Object getPropertyByName(String prop_name)
	{
		if (prop_name.toLowerCase().equals("selected")) {
			return this.is_selected();
		} else if (prop_name.toLowerCase().equals("field"))
			return this.get_field_name();
		else if (prop_name.toLowerCase().equals("systematic")) {
			return this.is_systematic();
		} else if (prop_name.toLowerCase().contains("issue"))
			return this.get_issues();
		else {
			return this._disp_column.get(prop_name);
		}
	}
	public boolean match(String prop_name, Object value)
	{
		Object cur_val = getPropertyByName(prop_name);
		return cur_val.equals(value);
	}
	
	public boolean compareTo(TaggedObj t)
	{
		Set<Entry<String,String>> set = t.get_disp_column().entrySet();
		boolean res = true;
		for (Entry<String, String> entry : set) {
			res = res && match(entry.getKey(), entry.getValue());
			
		}
		return res;
	}
}
