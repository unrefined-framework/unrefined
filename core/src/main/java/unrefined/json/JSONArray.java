/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package unrefined.json;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A JSON array. JSONObject supports java.util.List interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public class JSONArray extends ArrayList<Object> {

	private final long serialVersionUID = 3957988303675231981L;

	public JSONArray(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty JSONArray.
	 */
	public JSONArray() {
		super();
	}

	/**
	 * Constructs a JSONArray containing the elements of the specified
	 * collection, in the order they are returned by the collection's iterator.
	 * 
	 * @param c the collection whose elements are to be placed into this JSONArray
	 */
	public JSONArray(Collection<?> c) {
		super(c);
	}

	public void writeJSONString(Writer out) throws IOException {
		JSON.writeJSONString(out, this);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	/**
	 * Returns a string representation of this array. This is equivalent to
	 * calling {@link JSONArray#toJSONString()}.
	 */
	@Override
	public String toString() {
		return toJSONString();
	}

	public String getJSONString(int index, String defaultValue) {
		return JSON.getJSONString(this, index, defaultValue);
	}

	public boolean getJSONBoolean(int index, boolean defaultValue) {
		return JSON.getJSONBoolean(this, index, defaultValue);
	}

	public long getJSONInteger(int index, long defaultValue) {
		return JSON.getJSONInteger(this, index, defaultValue);
	}

	public double getJSONDecimal(int index, double defaultValue) {
		return JSON.getJSONDecimal(this, index, defaultValue);
	}

	public Number getJSONNumber(int index, Number defaultValue) {
		return JSON.getJSONNumber(this, index, defaultValue);
	}

	public Map<?, ?> getJSONObject(int index, Map<?, ?> defaultValue) {
		return JSON.getJSONObject(this, index, defaultValue);
	}

	public List<?> getJSONArray(int index, List<?> defaultValue) {
		return JSON.getJSONArray(this, index, defaultValue);
	}

	public Object getJSONValue(int index, Object defaultValue) {
		return JSON.getJSONValue(this, index, defaultValue);
	}

	public void addJSONValue(Object value) {
		JSON.addJSONValue(this, value);
	}

	public void addJSONValue(int index, Object value) {
		JSON.addJSONValue(this, index, value);
	}

}
