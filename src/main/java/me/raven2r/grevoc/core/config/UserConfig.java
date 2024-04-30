package me.raven2r.grevoc.core.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

public class UserConfig {
    private String userName = null;
    private String sourceLanguage = null;
    private String targetLanguage = null;
    private Path homeDirPath = null;

    private String mainTranslator = "";
    private String deeplAPIKey = "";
    private String amazonAccessKey = "";
    private String amazonSecretKey = "";
    private String openaiKey = "";

    private static List<String> supportedTranslatorNames = List.of("deepl", "amazon", "openai");


    // for UserConfigBuilder
    UserConfig() {
    }

    public UserConfig(String name, String password) {
        if(validate(name, password)) {
            setUserName(name);
            this.homeDirPath = GlobalConfig.getUsersDirectoryPath().resolve(this.userName);
            setDefaults();
            load();
        }
        else
            throw new IllegalArgumentException("Couldn't access user with such credentials");
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
            Files.write(passwordHashPath, innerSHA256);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

    public boolean load() {
        var userConfig = getHomeDirPath().resolve("config.properties").toFile();

        if(!userConfig.exists())
            return false;

        Properties properties = new Properties();

        try {
            properties.load(userConfig.toURL().openStream());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        setUserName(properties.getProperty("user.name"));
        setSourceLanguage(properties.getProperty("source.language"));
        setTargetLanguage(properties.getProperty("target.language"));
        setDeeplAPIKey(properties.getProperty("deepl.api.key"));
        setAmazonAccessKey(properties.getProperty("amazon.access.key"));
        setAmazonSecretKey(properties.getProperty("amazon.secret.key"));
        setOpenaiKey(properties.getProperty("openai.api.key"));
        setMainTranslator(properties.getProperty("main.translator"));

        if( null == getUserName()
                || null == getSourceLanguage()
                || null == getTargetLanguage() )
            return false;

        return true;
    }

    public boolean export() {
        return export(getHomeDirPath().resolve(GlobalConfig.USER_CONFIG_FILE_NAME));
    }

    public boolean export(final Path userFilePath) {
        var properties = new Properties();
        properties.setProperty("deepl.api.key", deeplAPIKey);
        properties.setProperty("amazon.access.key", amazonAccessKey);
        properties.setProperty("amazon.secret.key", amazonSecretKey);
        properties.setProperty("openai.api.key", openaiKey);
        properties.setProperty("main.translator", mainTranslator);
        properties.setProperty("source.language", sourceLanguage);
        properties.setProperty("target.language", targetLanguage);
        try {
            properties.store(new FileOutputStream(userFilePath.toFile()), "test comment");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setDefaults() {
        sourceLanguage = GlobalConfig.DEFAULT_SOURCE_LANGUAGE;
        targetLanguage = GlobalConfig.DEFAULT_TARGET_LANGUAGE;
        mainTranslator = GlobalConfig.DEFAULT_TRANSLATOR_NAME;
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

    public List getSupportedTranslatorNames() {
        return supportedTranslatorNames;
    }

    public String getMainTranslator() {
        return mainTranslator;
    }

    public void setMainTranslator(String mainTranslator) {
        if(!supportedTranslatorNames.contains(mainTranslator))
            throw new IllegalArgumentException("Translator is not supported '" + mainTranslator + "'");

        this.mainTranslator = mainTranslator;
    }

    public String getDeeplAPIKey() {
        return deeplAPIKey;
    }

    public void setDeeplAPIKey(String deeplAPIKey) {
        this.deeplAPIKey = deeplAPIKey;
    }

    public String getAmazonAccessKey() {
        return amazonAccessKey;
    }

    public void setAmazonAccessKey(String amazonAccessKey) {
        this.amazonAccessKey = amazonAccessKey;
    }

    public String getAmazonSecretKey() {
        return amazonSecretKey;
    }

    public void setAmazonSecretKey(String amazonSecretKey) {
        this.amazonSecretKey = amazonSecretKey;
    }

    public String getOpenaiAPIKey() {
        return openaiKey;
    }

    public void setOpenaiKey(String openaiKey) {
        this.openaiKey = openaiKey;
    }

    public static UserConfigBuilder newBuilder(String userName, String password) {
        return new UserConfigBuilder(userName, password);
    }
}
