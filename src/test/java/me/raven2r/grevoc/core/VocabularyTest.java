package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyTest {
    UserConfig userConfig;
    Vocabulary model;

    @Test
    void scenarioOne() {
        initVocabulary();
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
        initVocabulary();

        List<String> fileLines = List.of("Hallo", "Sicherheit", "Schule", "Wahrheit");

        try {
            Path filePath = userConfig.getHomeDirPath().resolve(GlobalConfig.USER_TRANSLATION_CANDIDATES_FILE_NAME);

            if(!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, fileLines);
            }
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }

        model.loadCandidatesFromFile();
        model.translateCandidates();
        model.pushTranslations();
        model.printTranslations();
    }

    @Test
    void testMultipleLoadingFromFile() {
        initVocabulary();
        model.loadCandidatesFromFile();
        model.loadCandidatesFromFile();
        model.loadCandidatesFromFile();
        model.translateCandidates();
        model.pushTranslations();
        model.printTranslations();
    }

    @Test
    void getTranslationsTest() {
        initVocabulary();
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
        initVocabulary();
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
        initVocabulary();
        model.pullAllDBTranslations();
        model.printDBTranslations();
        model.suspend();
    }


    private void initVocabulary() {
        String username = "user228";
        String password = "password";

        Vocabulary.makeDirectoryConsistence();
        if(!UserConfig.validate(username, password))
            UserConfig.register(username, password);

        userConfig = new UserConfig(username, password);
        userConfig.setSourceLanguage("de");
        userConfig.setTargetLanguage("ru");

        model = new Vocabulary(userConfig);
    }

    @Test
    public void testSingleTranslationPush() {
        initVocabulary();
        model.addCandidate("Unity");
        model.translateCandidates();
        model.pushTranslations();
        model.suspend();
    }

    @Test
    public void testMultipleTranslationsPush() {
        initVocabulary();
        model.addCandidate("Unity");
        model.addCandidate("Unity");
        model.addCandidate("Milch");
        model.addCandidate("Milch");
        model.addCandidate("Milch");
        model.translateCandidates();
        model.pushTranslations();
        model.suspend();
    }

    @Test
    void testRandomPulling() {
        initVocabulary();

        model.pullRandomlyAllDBTranslations().forEach(t -> {
            System.out.println(t.getSource());
        });
    }
}