package me.raven2r.grevoc.core.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Path;
import java.util.Properties;

public class UserConfig {
    private String userName;
    private String sourceLanguage;
    private String targetLanguage;
    private File homeDirectory;

    public UserConfig(String userName) {
        homeDirectory = GlobalConfig.getUsersDirectoryPath().resolve(userName).toFile();
    }

    public boolean load(File config) {
        Properties properties = new Properties();

        try {
            properties.load(config.toURL().openStream());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        setUserName(properties.getProperty("user.name"));
        setSourceLanguage(properties.getProperty("source.language"));
        setTargetLanguage(properties.getProperty("target.language"));

        if( null == getUserName()
                || null == getSourceLanguage()
                || null == getTargetLanguage() )
            return false;

        return true;
    }

    public boolean export(File config) {
        return false;
    }

    public void setDefault() {
        sourceLanguage = GlobalConfig.getDefaultSourceLanguage();
        targetLanguage = GlobalConfig.getDefaultTargetLanguage();
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
