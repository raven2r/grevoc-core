package me.raven2r.grevoc.core;

public class TranslationBuilder {
    Translation translation;
    public TranslationBuilder() {
        translation = new Translation();
    }
    
    public TranslationBuilder source(String s) {
        translation.setSource(s);
        return this;
    }
    
    public TranslationBuilder target(String t) {
        translation.setTarget(t);
        return this;
    }
    
    public TranslationBuilder counter(int c) {
        translation.setCounter(c);
        return this;
    }
    
    public TranslationBuilder added(long c) {
        translation.setAdded(c);
        return this;
    }
    
    public Translation build() {
        return translation;
    }
}
