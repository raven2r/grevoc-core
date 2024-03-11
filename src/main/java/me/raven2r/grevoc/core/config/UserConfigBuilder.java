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

    public UserConfigBuilder target(String language) {
        userConfig.setTargetLanguage(language);
        return this;
    }

    public UserConfigBuilder translator(String translatorName) {
        userConfig.setTranslatorName(translatorName);
        return this;
    }

    public UserConfig build() {
        if(null == userConfig.getUserName())
            throw new IllegalStateException("User name must be specified");

        return userConfig;
    }
}
