package me.raven2r.grevoc.core.translator;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeeplTest {
    @Test
    void test() {
        var deepl = new Deepl();
        var text = deepl.translate("en", "de", "Hello");
        System.out.println(text);
    }
}