package com.demo.application.Util;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

public class ViewUtil {

    public static final String SCHEMA_ID = "schemaId";
    public static final String ID = "id";
    public static final String SOR_ID = "sorId";

    private ViewUtil() {
    }

    public static boolean isPhone() {
        final WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isWindowsPhone() || webBrowser.isIPhone();
    }


}
