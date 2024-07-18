/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package unrefined.json;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public class JSONObject extends HashMap<Object, Object> {
	
	private static final long serialVersionUID = -503443796854799292L;
	
	public JSONObject() {
		super();
	}

	/**
	 * Allows creation of a JSONObject from a Map. After that, both the
	 * generated JSONObject and the Map can be modified independently.
	 *
	 * @param m
	 */
	public JSONObject(Map<?, ?> m) {
		super(m);
	}

	public JSONObject(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public JSONObject(int initialCapacity) {
		super(initialCapacity);
	}

	public void writeJSONString(Writer out) throws IOException {
		JSON.writeJSONString(out, this);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public String getJSONString(Object key, String defaultValue) {
		return JSON.getJSONString(this, key, defaultValue);
	}

	public boolean getJSONBoolean(Object key, boolean defaultValue) {
		return JSON.getJSONBoolean(this, key, defaultValue);
	}

	public long getJSONInteger(Object key, long defaultValue) {
		return JSON.getJSONInteger(this, key, defaultValue);
	}

	public double getJSONDecimal(Object key, double defaultValue) {
		return JSON.getJSONDecimal(this, key, defaultValue);
	}

	public Number getJSONNumber(Object key, Number defaultValue) {
		return JSON.getJSONNumber(this, key, defaultValue);
	}

	public Map<?, ?> getJSONObject(Object key, Map<?, ?> defaultValue) {
		return JSON.getJSONObject(this, key, defaultValue);
	}

	public List<?> getJSONArray(Object key, List<?> defaultValue) {
		return JSON.getJSONArray(this, key, defaultValue);
	}

	public Object getJSONValue(Object key, Object defaultValue) {
		return JSON.getJSONValue(this, key, defaultValue);
	}

	public Object putJSONValue(Object key, Object value) {
		return JSON.putJSONValue(this, key, value);
	}

}
