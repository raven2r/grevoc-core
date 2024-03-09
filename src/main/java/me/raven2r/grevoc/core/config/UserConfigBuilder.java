package me.raven2r.grevoc.core.config;

import java.util.HashMap;
import java.util.Map;

public class UserConfigBuilder {
    private UserConfig userConfig;

    public UserConfigBuilder() {
        userConfig = new UserConfig();
    }

    public UserConfigBuilder userName(String name) {
        userConfig.setUserName(name);
        return this;
    }

    public UserConfigBuilder source(String language) {
        userConfig.setSourceLanguage(language);
        return this;
    }

    public void target(String language) {
        userConfig.setTargetLanguage(language);
    }

    public UserConfig build() {
        return userConfig;
    }
}
