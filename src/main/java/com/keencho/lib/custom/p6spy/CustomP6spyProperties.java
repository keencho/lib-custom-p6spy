package com.keencho.lib.custom.p6spy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keencho.p6spy")
public class CustomP6spyProperties {
    protected static String startPackage = "";
    protected static int limitStackTrace = 10;
    protected static String[] excludePackages = null;

    public void setStartPackage(String startPackage) {
        CustomP6spyProperties.startPackage = startPackage;
    }

    public void setLimitStackTrace(int limitStackTrace) {
        CustomP6spyProperties.limitStackTrace = limitStackTrace;
    }

    public void setExcludePackages(String[] excludePackages) {
        CustomP6spyProperties.excludePackages = excludePackages;
    }
}
