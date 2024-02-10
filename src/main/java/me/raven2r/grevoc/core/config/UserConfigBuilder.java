package me.raven2r.grevoc.core.config;

import java.util.HashMap;
import java.util.Map;

public class UserConfigBuilder {
    private Map<String, Boolean> initedFields = new HashMap<>();
    private UserConfig userConfig;

    public UserConfigBuilder() {
        userConfig = new UserConfig();
        initedFields.put("userName", false);
        initedFields.put("sourceLanguage", false);
        initedFields.put("targetLanguage", false);
    }

    public void setUserName(String name) {
        userConfig.setUserName(name);
        initedFields.put("userName", true);
    }

    public void setSourceLanguage(String language) {
        userConfig.setSourceLanguage(language);
        initedFields.put("sourceLanguage", true);
    }

    public void setTargetLanguage(String language) {
        userConfig.setTargetLanguage(language);
        initedFields.put("targetLanguage", true);
    }

    public UserConfig build() {
        initedFields.forEach( (k, v) -> {
           if(v == false)
               throw new RuntimeException(k + " is not initialized");
        });

        return userConfig;
    }
}
