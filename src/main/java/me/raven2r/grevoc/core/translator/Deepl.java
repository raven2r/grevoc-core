package me.raven2r.grevoc.core.translator;

import com.deepl.api.Translator;

import java.io.IOException;
import java.util.Properties;

public class Deepl implements Translates {
    // make in external file
    private String authKey = null;
    private Translator translator = null;
    private String translatedText = "";

    public Deepl() {
        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("auth.properties"));
            translator = new Translator(properties.getProperty("deepl.api.key"));
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
}
