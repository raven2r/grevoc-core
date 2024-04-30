package me.raven2r.grevoc.core.config;

import java.nio.file.Path;

public class GlobalConfig {
    private static final Path rootDirectory = Path.of(System.getProperty("user.home"), ".grevoc");
    private static final Path usersDirectory = getRootDirectoryPath().resolve("users");
    private static final Path dataDirectory = getRootDirectoryPath().resolve("data");
    public static final String DEFAULT_SOURCE_LANGUAGE = "de";
    public static final String DEFAULT_TARGET_LANGUAGE = "ru";
    public static final String DEFAULT_TRANSLATOR_NAME = "deepl";
    public static final String USER_DATABASE_FILE_NAME = "translations.db";
    public static final String USER_TRANSLATION_CANDIDATES_FILE_NAME = "candidates.txt";
    public static final String USER_PASSWORD_HASH_FILE_NAME = "password.sha256sum";
    public static final String USER_CONFIG_FILE_NAME = "config.properties";
    public static final String USER_CANDIDATES_MD5_FILE_NAME = "candidates.md5sums";

    public static Path getDataDirectoryPath() {
        return dataDirectory;
    }

    public static Path getUsersDirectoryPath() {
        return usersDirectory;
    }

    public static Path getRootDirectoryPath() {
        return rootDirectory;
    }

    // Prevent initialization -- only constants are defined
    private GlobalConfig() {
    }


}
