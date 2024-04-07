package me.raven2r.grevoc.core.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

public class UserConfig {
    private String userName = null;
    private String sourceLanguage = null;
    private String targetLanguage = null;
    private String translatorName = null;
    private Path homeDirPath = null;

    // for UserConfigBuilder
    UserConfig() {
    }

    public UserConfig(String name, String password) {
        if(validate(name, password))
            load(GlobalConfig.getUsersDirectoryPath().resolve(name).resolve(GlobalConfig.USER_CONFIG_FILE_NAME).toFile());
        else
            throw new IllegalArgumentException("Couldn't access user with such credentials");

        this.userName = name;
        this.homeDirPath = GlobalConfig.getUsersDirectoryPath().resolve(this.userName);
    }


    public static boolean register(String name, String password) {
        File userDirectory = GlobalConfig
                .getUsersDirectoryPath()
                .resolve(name)
                .toFile();

        if(userDirectory.isDirectory())
            throw new IllegalArgumentException("Directory for username " + name
                    + " already exists ("+ userDirectory +")");
        else if(userDirectory.isFile())
            throw new IllegalArgumentException("File with name " + userDirectory
                    + " is not a directory, please resolve this");

        userDirectory.mkdir();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] innerSHA256 = messageDigest.digest();
            Path passwordHashPath = userDirectory.toPath().resolve(GlobalConfig.USER_PASSWORD_HASH_FILE_NAME);

            System.out.println(password + " to hash: " + Arrays.toString(innerSHA256));
            Files.write(passwordHashPath, innerSHA256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("user registered");
        return true;
    }

    public static boolean validate(String name, String password) {
        try {
            byte[] fileSHA256 = Files.readAllBytes(GlobalConfig
                    .getUsersDirectoryPath()
                    .resolve(name)
                    .resolve(GlobalConfig.USER_PASSWORD_HASH_FILE_NAME));

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());

            byte[] innerSHA256 = messageDigest.digest();
            return MessageDigest.isEqual(fileSHA256, innerSHA256);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean load(File config) {
        if(!config.exists()) {
            setDefaults();
            return false;
        }

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
        Path userFile = getHomeDirPath().resolve("config.properties");

        return false;
    }

    public void setDefaults() {
        sourceLanguage = GlobalConfig.DEFAULT_SOURCE_LANGUAGE;
        targetLanguage = GlobalConfig.DEFAULT_TARGET_LANGUAGE;
        translatorName = GlobalConfig.DEFAULT_TRANSLATOR_NAME;
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

    public Path getHomeDirPath() {
        return homeDirPath;
    }

    public String getTranslatorName() {
        return translatorName;
    }

    public void setTranslatorName(String translatorName) {
        this.translatorName = translatorName;
    }

    public static UserConfigBuilder newBuilder() {
        return new UserConfigBuilder();
    }
}
