package me.raven2r.grevoc.core.translator;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.amazonaws.services.translate.AmazonTranslateClient;

import java.io.IOException;
import java.util.Properties;

public class Amazon implements  Translates {
    private static AWSCredentialsProvider credentialsProvider;
    private static AmazonTranslate client;

    public Amazon() {
        loadDefaultCredentials();

        // Region is required, replace suitable for your credentials
        client = AmazonTranslateClient.builder()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

    @Override
    public String translate(String source, String target, String text) {
        TranslateTextRequest request = new TranslateTextRequest()
                .withText(text)
                .withSourceLanguageCode(source)
                .withTargetLanguageCode(target);

        return client.translateText(request).getTranslatedText();
    }

    public boolean loadDefaultCredentials() {
        try {
            var parameters = new Properties();
            parameters.load(getClass().getClassLoader().getResourceAsStream("auth.properties"));
            var accessKey = parameters.getProperty("aws.access.key");
            var secretKey = parameters.getProperty("aws.secret.key");
            var basicCreds = new BasicAWSCredentials(accessKey, secretKey);

            credentialsProvider = new AWSStaticCredentialsProvider(basicCreds);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return true;
    }

    public void loadDefaultCredentialsByChain() {
        credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance();
    }
}

