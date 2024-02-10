package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    UserConfig userConfig;
    Model model;

    @Test
    void scenarioOne() {
        model.addCandidate("Hallo");
        model.addCandidate("Schule");
        model.addCandidate("Hotel");
        model.addCandidate("Markt");
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
        initModel();
        model.loadTranslationCandidates();
        model.translateCandidates();
        model.uploadTranslations();
        model.printTranslations();
    }

    @Test
    void getTranslationsTest() {
        initModel();
        model.loadTranslationCandidates();
        model.translateCandidates();

        var tr = model.getTranslations();
        tr.put("TEST", "TEST");
        model.printTranslations();

        if(model.getTranslations().containsKey("TEST"))
            fail("Key added through clone");
    }

    @Test
    void loadAllDBTranslationsTest() {
        initModel();
        model.loadAllDBTranslations();
        model.printTranslations();
        model.printCandidatesCounter();
        model.translateCandidates();
    }


    private void initModel() {
        userConfig = new UserConfig("user288", "de", "ru");
        model = new Model(userConfig);
    }

}