package unrefined.json.parse;

import java.util.List;
import java.util.Map;

/**
 * Container factory for creating containers for JSON object and JSON array.
 * 
 * @see JSONParser#parse(java.io.Reader, JSONContainerHandler)
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public interface JSONContainerHandler {

	/**
	 * @return A Map instance to store JSON object, or null if you want to use org.json.simple.JSONObject.
	 */
	Map<?, ?> createJSONObject();
	
	/**
	 * @return A List instance to store JSON array, or null if you want to use org.json.simple.JSONArray. 
	 */
	List<?> creatJSONArray();

}
