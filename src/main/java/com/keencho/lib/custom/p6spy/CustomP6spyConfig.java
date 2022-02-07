package com.keencho.lib.custom.p6spy;

import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class CustomP6spyConfig {

    @PostConstruct
    public void setLogFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomP6spyPrettySqlFormatter.class.getName());
    }
}
