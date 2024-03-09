module me.raven2r.grevoc {
    requires deepl.java;
    requires java.sql;
    requires aws.java.sdk.translate;
    requires aws.java.sdk.core;

    exports me.raven2r.grevoc.core;
    exports me.raven2r.grevoc.core.config;
    exports me.raven2r.grevoc.core.translator;
}