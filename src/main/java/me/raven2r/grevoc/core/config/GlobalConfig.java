package me.raven2r.grevoc.core.config;

import java.nio.file.Path;

public class GlobalConfig {
    private static final Path rootDirectory = Path.of(System.getProperty("user.home"), ".grevoc");
    private static final Path usersDirectory = getRootDirectoryPath().resolve("users");
    private static final Path dataDirectory = getRootDirectoryPath().resolve("data");
    private static final String defaultSourceLanguage = "de";
    private static final String defaultTargetLanguage = "ru";
    private static final String userConfigFileName = "config.properties";
    private static final String userDatabaseFileName = "translations.db";

    public static Path getDataDirectoryPath() {
        return dataDirectory;
    }

    public static Path getUsersDirectoryPath() {
        return usersDirectory;
    }

    public static Path getRootDirectoryPath() {
        return rootDirectory;
    }

    public static String getDefaultSourceLanguage() {
        return defaultSourceLanguage;
    }

    public static String getDefaultTargetLanguage() {
        return defaultTargetLanguage;
    }

    public static String getUserConfigFileName() {
        return userConfigFileName;
    }

    public static String getUserDatabaseFileName() {
        return userDatabaseFileName;
    }
}
