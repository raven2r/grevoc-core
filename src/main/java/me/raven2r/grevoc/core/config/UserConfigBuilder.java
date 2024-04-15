package me.raven2r.grevoc.core.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserConfigBuilder {
    private UserConfig userConfig;

    public UserConfigBuilder(String username, String password) {
        userConfig = new UserConfig(username, password);
        userConfig.setUserName(username);
        userConfig.setDefaults();
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
        userConfig.setMainTranslator(translatorName);
        return this;
    }

    public UserConfigBuilder deeplAPIKey(String key) {
        userConfig.setDeeplAPIKey(key);
        return this;
    }

    public UserConfigBuilder amazonAccessKey(String key) {
        userConfig.setAmazonAccessKey(key);
        return this;
    }

    public UserConfigBuilder amazonSecretKey(String key) {
        userConfig.setAmazonSecretKey(key);
        return this;
    }

    public UserConfigBuilder openaiAPIKey(String key) {
        userConfig.setOpenaiKey(key);
        return this;
    }

    public UserConfigBuilder mainTranslator(String name) {
        userConfig.setMainTranslator(name);
        return this;
    }

    public UserConfig build() {
        if(null == userConfig.getUserName())
            throw new IllegalStateException("User name must be specified");

        return userConfig;
    }
}
