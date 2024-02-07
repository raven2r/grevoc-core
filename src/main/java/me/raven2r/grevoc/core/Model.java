package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;
import me.raven2r.grevoc.core.translator.Deepl;
import me.raven2r.grevoc.core.translator.Translates;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Model {
        private final String TABLE_NAME = "translations";
        private final String SOURCE_FIELD_NAME = "source_language";
        private final String TARGET_FIELD_NAME = "target_language";
        private final String DATE_FIELD_NAME = "date";

        private final UserConfig userConfig;
        private final String databaseURL;
        private final Translates translator;
        private Connection connection = null;
        private Statement statement;

        private final ArrayList<String> candidates = new ArrayList<String>();
        private final HashMap<String, String> translations = new HashMap<>();

        public Model (UserConfig userConfig) {
            this.userConfig = userConfig;
            makeDirectoryConsistence();

            var dbPath = userConfig.getHomeDirPath().resolve(GlobalConfig.getUserDatabaseFileName());
            databaseURL = "jdbc:sqlite:" + dbPath.toAbsolutePath();

            try {
                connection = DriverManager.getConnection(databaseURL);
                statement = connection.createStatement();
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            makeTables();

            translator = new Deepl();
        }

        private boolean makeDirectoryConsistence() {
            ArrayList<Path> paths = new ArrayList<Path>();
            paths.add(GlobalConfig.getRootDirectoryPath());
            paths.add(GlobalConfig.getDataDirectoryPath());
            paths.add(GlobalConfig.getUsersDirectoryPath());
            paths.add(GlobalConfig.getUsersDirectoryPath().resolve(userConfig.getUserName()));

            for(Path path: paths) {
                File directory = path.toFile();

                if ( (!directory.exists() && !directory.mkdir())
                        || !directory.isDirectory() )
                    return false;
            }

            return true;
        }

        public boolean makeTables() {
            var query = "CREATE TABLE IF NOT EXISTS translations "
                    + TABLE_NAME + "("
                    //+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + SOURCE_FIELD_NAME + " TEXT NOT NULL,"
                    + TARGET_FIELD_NAME + " TEXT NOT NULL,"
                    + DATE_FIELD_NAME + " INTEGER NOT NULL)";

            try {
                statement.execute(query);
                statement.execute("SELECT * FROM " + TABLE_NAME);
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            return true;
        }

        // add checks for language in future
        public boolean addCandidate(String candidate) {
            candidates.add(candidate);
            return true;
        }

        public void translateCandidates() {
            var i = candidates.iterator();
            String sourceLanguage = userConfig.getSourceLanguage();
            String targetLanguage = userConfig.getTargetLanguage();
            String source;
            String translation;

            while(i.hasNext()) {
                source = i.next();
                translation = translator.translate(sourceLanguage, targetLanguage, source);
                translations.put(source, translation);
                i.remove();
            }
        }

        // add checks later
        public boolean uploadTranslations() {
            var query = "";

            translations.forEach( (k, v) -> {

            });

            return true;
        }

        public void printCandidates() {
            candidates.forEach(System.out::println);
        }

        public void printTranslations() {
            translations.forEach( (k,v) -> System.out.println(k + "\t" + v) );
        }
}

