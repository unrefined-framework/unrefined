package org.example.desktop.json;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.json.JSON;
import unrefined.json.parse.JSONParseException;
import unrefined.util.Arrays;
import unrefined.util.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONTest {

    public static void main(String[] args) throws JSONParseException {
        Lifecycle.onMain(args);

        Log log = Log.defaultInstance();

        Map<Object, Object> jsonMap = new HashMap<>();
        JSON.putJSONValue(jsonMap, "string", "text");
        JSON.putJSONValue(jsonMap, "boolean", true);
        JSON.putJSONValue(jsonMap, "integer", 0);
        JSON.putJSONValue(jsonMap, "decimal", 1.5);
        JSON.putJSONValue(jsonMap, "object", Objects.operate(new HashMap<>(), object -> JSON.putJSONValue(object, "null", null)));
        JSON.putJSONValue(jsonMap, "array", Objects.operate(new ArrayList<>(), array -> array.addAll(Arrays.asList(1, 2, 3, 4, 5))));

        String jsonString = JSON.toJSONString(jsonMap);
        log.info("Unrefined JSON", "original: \n" + jsonString);

        Map<Object, Object> decoded = (Map<Object, Object>) JSON.parse(jsonString);
        String decodedString = JSON.toJSONString(decoded);

        log.info("Unrefined JSON", "decoded: \n" + decodedString);

        log.info("Unrefined JSON", "original.equals(decoded): " + jsonString.equals(decodedString));
    }

}
