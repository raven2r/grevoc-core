package me.raven2r.grevoc.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserConfigTest {
    @Test
    public void loadExportText() {
        var config = makeBlobUserConfig();

        config.setMainTranslator("deepl");
        config.setDeeplAPIKey("test-deepl-api-key");
        config.setAmazonAccessKey("test-amazon-access-key");
        config.setAmazonSecretKey("test-amazon-secret-key");
        config.setOpenaiKey("test-openai-secret-key");
        
        String deeplAPIKey = config.getDeeplAPIKey();
        String username = config.getUserName();
        String amazonAccessKey = config.getAmazonAccessKey();
        String amazonSecretKey = config.getAmazonSecretKey();
        String openaiAPIKey = config.getOpenaiAPIKey();

        config.export();


        String blobValue = "blob";

        config.setMainTranslator("openai");
        config.setDeeplAPIKey(blobValue);
        config.setAmazonAccessKey(blobValue);
        config.setAmazonSecretKey(blobValue);
        config.setOpenaiKey(blobValue);
        
        config.load();


        assertNotEquals("openai", config.getMainTranslator());
        assertNotEquals(blobValue, config.getDeeplAPIKey());
        assertNotEquals(blobValue, config.getAmazonAccessKey());
        assertNotEquals(blobValue, config.getAmazonSecretKey());
        assertNotEquals(blobValue, config.getOpenaiAPIKey());

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