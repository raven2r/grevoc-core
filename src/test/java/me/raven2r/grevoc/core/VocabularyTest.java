package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyTest {
    UserConfig userConfig;
    Vocabulary vocabulary;

    @Test
    void scenarioOne() {
        initVocabulary();
        vocabulary.addCandidate("Hallo");
        vocabulary.addCandidate("Schule");
        vocabulary.addCandidate("Hotel");
        vocabulary.addCandidate("Markt");
        vocabulary.translateCandidates();
        vocabulary.printTranslations();
        vocabulary.printDBTranslations();
        vocabulary.pushTranslations();
        vocabulary.addCandidate("Hotel");
        vocabulary.translateCandidates();
        vocabulary.printTranslations();
        vocabulary.pushTranslations();
        vocabulary.printDBTranslations();
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

        vocabulary.loadCandidatesFromFile();
        vocabulary.translateCandidates();
        vocabulary.pushTranslations();
        vocabulary.printTranslations();
    }

    @Test
    void testMultipleLoadingFromFile() {
        initVocabulary();
        vocabulary.loadCandidatesFromFile();
        vocabulary.loadCandidatesFromFile();
        vocabulary.loadCandidatesFromFile();
        vocabulary.translateCandidates();
        vocabulary.pushTranslations();
        vocabulary.printTranslations();
    }

    @Test
    void getTranslationsTest() {
        initVocabulary();
        vocabulary.loadCandidatesFromFile();
        vocabulary.translateCandidates();

        var trs = vocabulary.getTranslations();
        var tr = Translation.newSimple("source", "target");
        trs.add(tr);
        vocabulary.printTranslations();

        if(vocabulary.getTranslations().contains(tr))
            fail("Key added through clone");
    }

    @Test
    void loadAllDBTranslationsTest() {
        initVocabulary();
        vocabulary.printCandidates();
        vocabulary.printTranslations();
        vocabulary.printDBTranslations();
        vocabulary.loadCandidatesFromFile();
        vocabulary.printCandidates();
        vocabulary.translateCandidates();
        vocabulary.printTranslations();
        vocabulary.suspend();
    }

    @Test
    void pullAddDBTranslations() {
        initVocabulary();
        vocabulary.pullAllDBTranslations();
        vocabulary.printDBTranslations();
        vocabulary.suspend();
    }


    @Test
    public void testSingleTranslationPush() {
        initVocabulary();
        vocabulary.addCandidate("Unity");
        vocabulary.translateCandidates();
        vocabulary.pushTranslations();
        vocabulary.suspend();
    }

    @Test
    public void testMultipleTranslationsPush() {
        initVocabulary();
        vocabulary.addCandidate("Unity");
        vocabulary.addCandidate("Unity");
        vocabulary.addCandidate("Milch");
        vocabulary.addCandidate("Milch");
        vocabulary.addCandidate("Milch");
        vocabulary.translateCandidates();
        vocabulary.pushTranslations();
        vocabulary.suspend();
    }

    @Test
    public void multipleFilesCandidatesLoadTest() {
        initVocabulary();
        vocabulary.loadCandidatesFromFile();
        Map candidatesFirst = vocabulary.getCandidates();

        vocabulary.loadCandidatesFromFile();
        vocabulary.loadCandidatesFromFile();
        vocabulary.loadCandidatesFromFile();
        Map candidatesSecond = vocabulary.getCandidates();

        candidatesFirst.forEach( (k, v) -> System.out.println(k + ":" + v));
        System.out.println("Second:");
        candidatesSecond.forEach( (k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    void testRandomPulling() {
        initVocabulary();

        vocabulary.pullRandomlyAllDBTranslations().forEach(t -> {
            System.out.println(t.getSource());
        });

        vocabulary.suspend();
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

        vocabulary = new Vocabulary(userConfig);
    }
}