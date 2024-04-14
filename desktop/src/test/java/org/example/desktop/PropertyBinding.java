package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.beans.BooleanProperty;

import java.util.Properties;

public class PropertyBinding {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Properties properties = new Properties();
        properties.setProperty("test.boolean", "true");
        Log.defaultInstance().info("Property", properties.getProperty("test.boolean"));

        BooleanProperty property = BooleanProperty.bind(properties, "test.boolean");
        property.set(false);
        Log.defaultInstance().info("Property", properties.getProperty("test.boolean"));
    }

}
