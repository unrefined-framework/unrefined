package unrefined.json.parse;

/**
 * A simplified and stoppable SAX-like content handler for stream processing of JSON text. 
 *
 * @see JSONParser#parse(java.io.Reader, JSONContentHandler)
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public interface JSONContentHandler {

	/**
	 * Receive notification of the beginning of JSON processing.
	 * The parser will invoke this method only once.
     * 
	 * @throws JSONParseException
	 * 			JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 */
	void startJSON() throws JSONParseException;
	
	/**
	 * Receive notification of the end of JSON processing.
	 * 
	 * @throws JSONParseException
	 */
	void endJSON() throws JSONParseException;
	
	/**
	 * Receive notification of the beginning of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     *         JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * @see #endJSON
	 */
	boolean startObject() throws JSONParseException;
	
	/**
	 * Receive notification of the end of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     * 
     * @see #startObject
	 */
	boolean endObject() throws JSONParseException;
	
	/**
	 * Receive notification of the beginning of a JSON object entry.
	 * 
	 * @param key - Key of a JSON object entry. 
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     * 
     * @see #endObjectEntry
	 */
	boolean startObjectEntry(String key) throws JSONParseException;
	
	/**
	 * Receive notification of the end of the value of previous object entry.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     * 
     * @see #startObjectEntry
	 */
	boolean endObjectEntry() throws JSONParseException;
	
	/**
	 * Receive notification of the beginning of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     * 
     * @see #endArray
	 */
	boolean startArray() throws JSONParseException;
	
	/**
	 * Receive notification of the end of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
     * 
     * @see #startArray
	 */
	boolean endArray() throws JSONParseException;
	
	/**
	 * Receive notification of the JSON primitive values:
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean
	 * 	null
	 * 
	 * @param value - Instance of the following:
	 * 			java.lang.String,
	 * 			java.lang.Number,
	 * 			java.lang.Boolean
	 * 			null
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws JSONParseException
	 */
	boolean isPrimitive(Object value) throws JSONParseException;
		
}
