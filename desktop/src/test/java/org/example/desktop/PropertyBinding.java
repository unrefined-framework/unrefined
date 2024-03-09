package org.example.desktop;

import unrefined.app.Logger;
import unrefined.beans.BooleanProperty;
import unrefined.runtime.DesktopRuntime;

import java.util.Properties;

public class PropertyBinding {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Properties properties = new Properties();
        properties.setProperty("test.boolean", "true");
        Logger.defaultInstance().info("Property", properties.getProperty("test.boolean"));

        BooleanProperty property = BooleanProperty.bind(properties, "test.boolean");
        property.set(false);
        Logger.defaultInstance().info("Property", properties.getProperty("test.boolean"));
    }

}
