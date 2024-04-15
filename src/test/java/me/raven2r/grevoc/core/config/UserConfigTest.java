package me.raven2r.grevoc.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserConfigTest {
    @Test
    public void loadExportText() {
        var config = makeBlobUserConfig();
        String deeplAPIKey = config.getDeeplAPIKey();
        String username = config.getUserName();
        String amazonAccessKey = config.getAmazonAccessKey();
        String amazonSecretKey = config.getAmazonSecretKey();
        String openaiAPIKey = config.getOpenaiAPIKey();

        config.export();
        config.load();

        assertEquals(deeplAPIKey, config.getDeeplAPIKey());
    }

    private UserConfig makeBlobUserConfig() {
        String username = "test_user";
        String password = "test_password";

        if(!UserConfig.validate(username, password))
            UserConfig.register(username, password);

        var config = UserConfig.newBuilder(username, password)
                .source("de")
                .target("ru")
                .deeplAPIKey("tesetetset920930")
                .translator("deepl")
                .build();

        return config;
    }
}