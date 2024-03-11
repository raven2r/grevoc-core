package me.raven2r.grevoc.core.translator;

public interface Translates {
    public String translate(String source, String target, String text);
    public static Translates byName(String name) {
        switch(name) {
            case "amazon":
                return new Amazon();
            case "deepl":
                return new Deepl();
            default:
                throw new IllegalArgumentException("No such translator '" + name + "'");
        }
    }
}
