package me.raven2r.grevoc.core.translator;

import com.deepl.api.Translator;

import java.io.IOException;
import java.util.Properties;

public class Deepl implements Translates {
    private String apiKey = null;
    private Translator translator = null;
    private String translatedText = "";
    private Properties parameters = new Properties();


    public Deepl() {
        loadDefaultApiKey();
        translator = new Translator(apiKey);
    }

    @Override
    public String translate(String source, String target, String text) {
        try {
            translatedText = translator.translateText(text, source, target).getText();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return translatedText;
    }

    public boolean setAPIKey(String key) {
        apiKey = key;
        parameters.setProperty("deepl.api.key", key);
        return true;
    }

    boolean loadDefaultApiKey() {
        try {
            parameters.load(getClass().getClassLoader().getResourceAsStream("auth.properties"));
            apiKey = parameters.getProperty("deepl.api.key");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return true;
    }
}
