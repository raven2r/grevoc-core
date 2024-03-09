package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyTest {
    UserConfig userConfig;
    Vocabulary model;

    @Test
    void scenarioOne() {
        initModel();
        model.addCandidate("Hallo");
        model.addCandidate("Schule");
        model.addCandidate("Hotel");
        model.addCandidate("Markt");
        model.translateCandidates();
        model.printTranslations();
        model.printDBTranslations();
        model.pushTranslations();
        model.addCandidate("Hotel");
        model.translateCandidates();
        model.printTranslations();
        model.pushTranslations();
        model.printDBTranslations();
    }

    @Test
    void testLoadingFromFile() {
        initModel();
        model.loadCandidatesFromFile();
        model.translateCandidates();
        model.pushTranslations();
        model.printTranslations();
    }

    @Test
    void testMultipleLoadingFromFile() {
        initModel();
        model.loadCandidatesFromFile();
        model.loadCandidatesFromFile();
        model.loadCandidatesFromFile();
        model.translateCandidates();
        model.pushTranslations();
        model.printTranslations();
    }

    @Test
    void getTranslationsTest() {
        initModel();
        model.loadCandidatesFromFile();
        model.translateCandidates();

        var trs = model.getTranslations();
        var tr = Translation.newSimple("source", "target");
        trs.add(tr);
        model.printTranslations();

        if(model.getTranslations().contains(tr))
            fail("Key added through clone");
    }

    @Test
    void loadAllDBTranslationsTest() {
        initModel();
        model.printCandidates();
        model.printTranslations();
        model.printDBTranslations();
        model.loadCandidatesFromFile();
        model.printCandidates();
        model.translateCandidates();
        model.printTranslations();
    }

    @Test
    void pullAddDBTranslations() {
        initModel();
        model.pullAllDBTranslations();
        model.printDBTranslations();
        model.suspend();
    }


    private void initModel() {
        userConfig = new UserConfig("user288", "de", "ru");
        model = new Vocabulary(userConfig);
    }

    @Test
    public void testSingleTranslationPush() {
        initModel();
        model.addCandidate("Unity");
        model.translateCandidates();
        model.pushTranslations();
        model.suspend();
    }

    @Test
    public void testTwoTranslationPush() {
        initModel();
        model.addCandidate("Unity");
        model.addCandidate("Unity");
        model.addCandidate("Milch");
        model.translateCandidates();
        model.pushTranslations();
        model.suspend();
    }

    @Test
    void testRandomPulling() {
        initModel();

        model.pullRandomlyAllDBTranslations().forEach(t -> {
            System.out.println(t.getSource());
        });
    }
}