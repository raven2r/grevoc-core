package me.raven2r.grevoc.core.translator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmazonTest {
    @Test
    public void testTranslation() {
        var amazon = new Amazon();
        assertTrue(amazon.translate("de", "ru", "Hallo Welt!").equals("Привет, мир!"));
    }
}