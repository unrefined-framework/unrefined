/*
 * $Id: JSON.java,v 1.1 2006/04/15 14:37:04 platform Exp $
 * Created on 2006-4-15
 */
package unrefined.json;

import unrefined.json.parse.JSONContainerHandler;
import unrefined.json.parse.JSONContentHandler;
import unrefined.json.parse.JSONParseException;
import unrefined.json.parse.JSONParser;
import unrefined.math.FastMath;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public final class JSON {

	private JSON() {
		throw new NotInstantiableError(JSON.class);
	}
	
	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @see JSONParser
	 * 
	 * @param in
	 * @return Instance of the following:
	 * 	org.json.simple.JSONObject,
	 * 	org.json.simple.JSONArray,
	 * 	java.lang.String,
	 * 	java.lang.Long,
	 * 	java.lang.Double,
	 * 	java.lang.Boolean,
	 * 	null
	 * 
	 * @throws IOException
	 * @throws JSONParseException
	 */
	public static Object parse(Reader in) throws IOException, JSONParseException {
		JSONParser parser = new JSONParser();
		return parser.parse(in);
	}
	
	public static Object parse(String s) throws JSONParseException {
		JSONParser parser = new JSONParser();
		return parser.parse(s);
	}

	public static void parse(Reader in, JSONContentHandler contentHandler) throws IOException, JSONParseException {
		JSONParser parser = new JSONParser();
		parser.parse(in, contentHandler);
	}

	public static void parse(String s, JSONContentHandler contentHandler) throws JSONParseException {
		JSONParser parser = new JSONParser();
		parser.parse(s, contentHandler);
	}

	public static Object parse(Reader in, JSONContainerHandler containerHandler) throws IOException, JSONParseException {
		JSONParser parser = new JSONParser();
		return parser.parse(in, containerHandler);
	}

	public static Object parse(String s, JSONContainerHandler containerHandler) throws JSONParseException {
		JSONParser parser = new JSONParser();
		return parser.parse(s, containerHandler);
	}

	public static String getJSONString(Map<?, ?> object, Object key, String defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		return value instanceof Character ? Character.toString((Character) value) : (value instanceof String ? (String) value : defaultValue);
	}

	public static boolean getJSONBoolean(Map<?, ?> object, Object key, boolean defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		return value instanceof Boolean ? (Boolean) value : defaultValue;
	}

	public static long getJSONInteger(Map<?, ?> object, Object key, long defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (value instanceof Number && FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).longValue();
		else return defaultValue;
	}

	public static double getJSONDecimal(Map<?, ?> object, Object key, double defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (value instanceof Number && !FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).doubleValue();
		else return defaultValue;
	}

	public static Number getJSONNumber(Map<?, ?> object, Object key, Number defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (value instanceof Number) {
			if (FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).longValue();
			else return ((Number) value).doubleValue();
		}
		else return defaultValue;
	}

	public static Map<?, ?> getJSONObject(Map<?, ?> object, Object key, Map<?, ?> defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (value instanceof Map) return (Map<?, ?>) value;
		else return defaultValue;
	}

	public static List<?> getJSONArray(Map<?, ?> object, Object key, List<?> defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (value instanceof List) return (List<?>) value;
		else return defaultValue;
	}

	public static Object getJSONValue(Map<?, ?> object, Object key, Object defaultValue) {
		if (!isJSONKeyCompatible(key)) return defaultValue;
		Object value = object.get(key);
		if (isJSONValueCompatible(value)) return value;
		else return checkJSONValueCompatible(defaultValue);
	}

	@SuppressWarnings("unchecked")
	public static Object putJSONValue(Map<?, ?> object, Object key, Object value) {
		return ((Map<Object, Object>) object).put(checkJSONKeyCompatible(key), checkJSONValueCompatible(value));
	}

	public static String getJSONString(List<?> array, int index, String defaultValue) {
		Object value = array.get(index);
		return value instanceof Character ? Character.toString((Character) value) : (value instanceof String ? (String) value : defaultValue);
	}

	public static boolean getJSONBoolean(List<?> array, int index, boolean defaultValue) {
		Object value = array.get(index);
		return value instanceof Boolean ? (Boolean) value : defaultValue;
	}

	public static long getJSONInteger(List<?> array, int index, long defaultValue) {
		Object value = array.get(index);
		if (value instanceof Number && FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).longValue();
		else return defaultValue;
	}

	public static double getJSONDecimal(List<?> array, int index, double defaultValue) {
		Object value = array.get(index);
		if (value instanceof Number && !FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).doubleValue();
		else return defaultValue;
	}

	public static Number getJSONNumber(List<?> array, int index, Number defaultValue) {
		Object value = array.get(index);
		if (value instanceof Number) {
			if (FastMath.isInteger(((Number) value).doubleValue())) return ((Number) value).longValue();
			else return ((Number) value).doubleValue();
		}
		else return defaultValue;
	}

	public static Map<?, ?> getJSONObject(List<?> array, int index, Map<?, ?> defaultValue) {
		Object value = array.get(index);
		if (value instanceof Map) return (Map<?, ?>) value;
		else return defaultValue;
	}

	public static List<?> getJSONArray(List<?> array, int index, List<?> defaultValue) {
		Object value = array.get(index);
		if (value instanceof List) return (List<?>) value;
		else return defaultValue;
	}

	public static Object getJSONValue(List<?> array, int index, Object defaultValue) {
		Object value = array.get(index);
		if (isJSONValueCompatible(value)) return value;
		else return checkJSONValueCompatible(defaultValue);
	}

	@SuppressWarnings("unchecked")
	public static void addJSONValue(List<?> array, Object value) {
		((List<Object>) array).add(checkJSONValueCompatible(value));
	}

	@SuppressWarnings("unchecked")
	public static void addJSONValue(List<?> array, int index, Object value) {
		((List<Object>) array).add(index, checkJSONValueCompatible(value));
	}

	public static boolean isJSONKeyCompatible(Object key) {
		return key instanceof String || key instanceof Character;
	}

	public static Object checkJSONKeyCompatible(Object key) {
		if (isJSONKeyCompatible(key)) return key;
		else throw new IllegalArgumentException("object '" + key + "' is not json key compatible.");
	}

	public static boolean isJSONValueCompatible(Object value) {
        return value == null || value instanceof Number || value instanceof String || value instanceof Character || value instanceof Boolean
                || value instanceof List || value instanceof Map;
	}

	public static Object checkJSONValueCompatible(Object value) {
		if (isJSONValueCompatible(value)) return value;
		else throw new IllegalArgumentException("object '" + value + "' is not json value compatible.");
	}

	private static Number checkJSONNumber(Number number) {
		if (number instanceof Double || number instanceof Long) return number;
		else throw new IllegalArgumentException("object '" + number + "' is not json number compatible.");
	}

	/**
	 * Encode an object into JSON text and write it to out.
	 *
	 * @param out
	 * @param value
	 */
	public static void writeJSONString(Writer out, Object value) throws IOException {
		if (value == null) out.write("null");
		else if (value instanceof Number) {
			double number = ((Number) value).doubleValue();
			if (Double.isInfinite(number) || Double.isNaN(number)) out.write("null");
			else if (FastMath.isInteger(number)) out.write(String.valueOf(((Number) value).longValue()));
			else out.write(String.valueOf(number));
		}
		else if (value instanceof Boolean) out.write(String.valueOf(value));
		else if (value instanceof String || value instanceof Character) {
			out.write('\"');
			out.write(escape(String.valueOf(value)));
			out.write('\"');
		}
		else if (value instanceof List) {
			boolean first = true;
			out.write('[');
			Iterator<?> iter = ((List<?>) value).iterator();
			while (iter.hasNext()) {
				if (first) first = false;
				else out.write(',');
				writeJSONString(out, iter.next());
			}
			out.write(']');
		}
		else if (value instanceof Map) {
			boolean first = true;
			out.write('{');
			Iterator<? extends Map.Entry<?, ?>> iter = ((Map<?, ?>) value).entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<?, ?> entry = iter.next();
				Object key = entry.getKey();
				Object jsonVal = entry.getValue();
				if (isJSONKeyCompatible(key) && isJSONValueCompatible(jsonVal)) {
					if (first) first = false;
					else out.write(',');
					out.write('\"');
					out.write(escape(String.valueOf(key)));
					out.write('\"');
					out.write(':');
					writeJSONString(out, jsonVal);
				}
			}
			out.write('}');
		}
		else throw new IllegalArgumentException("object '" + value + "' is not json value compatible.");
	}

	/**
	 * Convert an object to JSON String.
	 *
	 * @param value
	 * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
	 */
	public static String toJSONString(Object value) {
		try (StringWriter out = new StringWriter()) {
			writeJSONString(out, value);
			out.flush();
			return out.toString();
		} catch (IOException e) {
            throw new UnexpectedError(e);
        }
	}

	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * @param s
	 * @return
	 */
	static String escape(String s) {
		if (s == null) return null;
		StringBuilder builder = new StringBuilder();
		escape(s, builder);
		return builder.toString();
	}

	/**
	 * @param s - Must not be null.
	 * @param builder
	 */
	private static void escape(String s, StringBuilder builder) {
		final int len = s.length();
		for (int i = 0; i < len; i ++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '"':
					builder.append("\\\"");
					break;
				case '\\':
					builder.append("\\\\");
					break;
				case '\b':
					builder.append("\\b");
					break;
				case '\f':
					builder.append("\\f");
					break;
				case '\n':
					builder.append("\\n");
					break;
				case '\r':
					builder.append("\\r");
					break;
				case '\t':
					builder.append("\\t");
					break;
				case '/':
					builder.append("\\/");
					break;
				default:
					//Reference: http://www.unicode.org/versions/Unicode5.1.0/
					if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
						String ss=Integer.toHexString(ch);
						builder.append("\\u");
						for (int k = 0; k < 4 - ss.length(); k ++) builder.append('0');
						builder.append(ss.toUpperCase());
					}
					else builder.append(ch);
			}
		}
	}

}
