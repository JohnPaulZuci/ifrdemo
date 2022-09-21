package com.demo.application.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


public class PropertyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyHandler.class);

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final Map<String, String> PROPS;

    static {
        //config.properties=/home/johnpaul.s@zucisystems.com/Desktop/Johny/azentio/config.properties
        final String path = System.getenv(CONFIG_PROPERTIES);
        try (var input = new FileInputStream(path)) {

            var prop = new Properties();
            //load a properties file from class path, inside static method
            prop.load(input);
            var maps = prop.entrySet().stream().collect(
                    Collectors.toMap(
                            e -> String.valueOf(e.getKey()),
                            e -> String.valueOf(e.getValue()),
                            (prev, next) -> next, HashMap::new
                    ));
            PROPS = Collections.unmodifiableMap(maps);
        } catch (IOException e) {
            throw new UncheckedIOException("Sorry, unable to load " + CONFIG_PROPERTIES, e);
        }
    }

    private PropertyHandler() {
    }

    public static String get(final String key) {
        return PROPS.get(key);
    }

}
