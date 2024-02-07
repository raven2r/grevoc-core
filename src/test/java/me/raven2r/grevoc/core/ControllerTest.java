package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.UserConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    @Test
    void scenarioOne() {
        var userConfig = new UserConfig("user288");
        userConfig.setSourceLanguage("de");
        userConfig.setTargetLanguage("ru");

        var model = new Model(userConfig);
        model.addCandidate("Hallo");
        model.addCandidate("Schule");
        model.addCandidate("Hotel");
        model.addCandidate("Markt");
        model.translateCandidates();
        model.printTranslations();
    }

    @Test
    void test() {
        var controller = new Controller();
        Controller.main(new String[]{"", "user1"});

    }

}