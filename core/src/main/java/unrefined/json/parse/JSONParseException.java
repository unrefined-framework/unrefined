package unrefined.json.parse;

/**
 * JSONParseException explains why and where the error occurs in source JSON text.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Karstian Lee
 */
public class JSONParseException extends Exception {

	private static final long serialVersionUID = -7880698968187728547L;
	
	public static final int ERROR_UNEXPECTED_CHAR = 0;
	public static final int ERROR_UNEXPECTED_TOKEN = 1;
	public static final int ERROR_UNEXPECTED_EXCEPTION = 2;

	private final int errorType;
	private final Object unexpectedObject;
	private final int position;
	
	public JSONParseException(int position, int errorType, Object unexpectedObject) {
		this.position = position;
		this.errorType = errorType;
		this.unexpectedObject = unexpectedObject;
	}
	
	public int errorType() {
		return errorType;
	}
	
	/**
	 * @see JSONParser#position()
	 * 
	 * @return The character position (starting with 0) of the input where the error occurs.
	 */
	public int position() {
		return position;
	}
	
	/**
	 * @see JSONParser.Token
	 * 
	 * @return One of the following base on the value of errorType:
	 * 		   	ERROR_UNEXPECTED_CHAR		java.lang.Character
	 * 			ERROR_UNEXPECTED_TOKEN		org.json.simple.parser.Token
	 * 			ERROR_UNEXPECTED_EXCEPTION	java.lang.Exception
	 */
	public Object getUnexpectedObject() {
		return unexpectedObject;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		
		switch (errorType) {
			case ERROR_UNEXPECTED_CHAR:
				builder.append("Unexpected character '").append(unexpectedObject).append("' at position ").append(position).append(".");
				break;
			case ERROR_UNEXPECTED_TOKEN:
				builder.append("Unexpected token ").append(unexpectedObject).append(" at position ").append(position).append(".");
				break;
			case ERROR_UNEXPECTED_EXCEPTION:
				builder.append("Unexpected exception at position ").append(position).append(": ").append(unexpectedObject);
				break;
			default:
				builder.append("Unknown error at position ").append(position).append(".");
				break;
		}
		return builder.toString();
	}

}
