package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    UserConfig userConfig;

    @Test
    void scenarioOne() {


        var model = new Model(userConfig);
        model.addCandidate("Hallo");
        model.addCandidate("Schule");
        model.addCandidate("Hotel");
        //model.addCandidate("Markt");
        model.translateCandidates();
        model.printTranslations();
        model.uploadTranslations();
        model.printTranslations();
        model.addCandidate("Hotel");
        model.translateCandidates();
        model.printTranslations();
        model.uploadTranslations();
        model.printTranslations();
    }

    @Test
    void scenarioTwo() {
        initConfig();
        var model = new Model(userConfig);
        model.loadTranslationCandidates();
        model.translateCandidates();
        model.uploadTranslations();
        model.printTranslations();
    }

    @Test
    void translationUniq() {
        initConfig();
        var model = new Model(userConfig);
        model.loadTranslationCandidates();
        model.translateCandidates();
        model.uploadTranslations();

        var tr = model.getTranslations();
        tr.put("TEST", "TEST");
        model.printTranslations();

        if(model.getTranslations().containsKey("TEST"))
            fail("Key added through clone");
    }


    private void initConfig() {
        userConfig = new UserConfig("user288");
        userConfig.setSourceLanguage("de");
        userConfig.setTargetLanguage("ru");
    }

}