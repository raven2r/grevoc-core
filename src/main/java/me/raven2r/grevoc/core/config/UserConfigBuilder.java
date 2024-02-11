package me.raven2r.grevoc.core.config;

import java.util.HashMap;
import java.util.Map;

public class UserConfigBuilder {
    private Map<String, Boolean> mandatoryFields = new HashMap<>();
    private UserConfig userConfig;

    public UserConfigBuilder() {
        userConfig = new UserConfig();
        mandatoryFields.put("userName", false);
        mandatoryFields.put("sourceLanguage", false);
        mandatoryFields.put("targetLanguage", false);
    }

    public void setUserName(String name) {
        userConfig.setUserName(name);
        mandatoryFields.put("userName", true);
    }

    public void setSourceLanguage(String language) {
        userConfig.setSourceLanguage(language);
        mandatoryFields.put("sourceLanguage", true);
    }

    public void setTargetLanguage(String language) {
        userConfig.setTargetLanguage(language);
        mandatoryFields.put("targetLanguage", true);
    }

    public UserConfig build() {
        mandatoryFields.forEach( (k, v) -> {
           if(!v)
               throw new RuntimeException(k + " is not initialized");
        });

        return userConfig;
    }
}
