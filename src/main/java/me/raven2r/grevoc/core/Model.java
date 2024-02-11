package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;
import me.raven2r.grevoc.core.translator.Deepl;
import me.raven2r.grevoc.core.translator.Translates;
import org.languagetool.JLanguageTool;
import org.languagetool.Languages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class Model {
        private final String TABLE_TRANS = "translations";
        private final String SOURCE_FIELD_NAME = "source_language";
        private final String TARGET_FIELD_NAME = "target_language";
        private final String DATE_FIELD_NAME = "date";

        // source field is same as in main table
        private final String TABLE_STATS = "statistic";
        private final String COUNTER_FIELD_NAME = "counter";


        private final UserConfig userConfig;
        private final Path candidatesFile;
        private JLanguageTool languageTool;
        private final String databaseURL;
        private final Translates translator;
        private Connection connection = null;
        private Statement statement;

        private final ArrayList<String> candidates = new ArrayList<String>();
        private final TreeMap<String, Integer> candidatesCounter = new TreeMap<>();
        private final TreeMap<String, Integer> candidatesDBCounter = new TreeMap<>();
        private final TreeMap<String, String> translations = new TreeMap<>();
        private final HashSet<String> translated = new HashSet<>();
        private ResultSet selectorResult;

        public Model (UserConfig userConfig) {
            this.userConfig = userConfig;
            candidatesFile = userConfig.getHomeDirPath()
                                .resolve(GlobalConfig.getUserTranslationCandidatesFileName());
            languageTool = new JLanguageTool(Languages.getLanguageForShortCode(userConfig.getSourceLanguage()));

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

            if(null == connection)
                throw new RuntimeException("Couldn't connect to database");

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
            // create translations table
            var query = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_TRANS + "("
                    //+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + SOURCE_FIELD_NAME + " TEXT NOT NULL,"
                    + TARGET_FIELD_NAME + " TEXT NOT NULL,"
                    + DATE_FIELD_NAME + " INTEGER NOT NULL)";

            try {
                statement.execute(query);
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            // create counter table
            query = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_STATS + "("
                    //+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + SOURCE_FIELD_NAME + " TEXT NOT NULL,"
                    + COUNTER_FIELD_NAME + " INTEGER NOT NULL DEFAULT 0,"
                    + "PRIMARY KEY (" + SOURCE_FIELD_NAME + "),"
                    + "FOREIGN KEY (" + SOURCE_FIELD_NAME + ")"
                    + "REFERENCES " + TABLE_TRANS + " (" + SOURCE_FIELD_NAME + ")"
                    + "ON DELETE CASCADE"
                    + ")";

            try {
                statement.execute(query);
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            return true;
        }


        public boolean loadTranslationCandidatesFile() {
            var candidatesFile = userConfig.getHomeDirPath()
                    .resolve(GlobalConfig.getUserTranslationCandidatesFileName());

            List<String> lines = null;
            try {
                lines = Files.readAllLines(candidatesFile);

                if(lines.isEmpty())
                    return false;

                // add checks for number of words
                lines.forEach(this::addCandidate);
                return true;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        }

        public boolean deleteCandidatesFile() {
            try {
                Files.delete(candidatesFile);
                return true;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        }


    /**
     * Add candidate
     *
     * @param candidate
     * @return true if candidate is new in this session, not in db
     */
        public boolean addCandidate(String candidate) {
            try {
                if(!languageTool.check(candidate).isEmpty())
                    return false;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // new
            if (!candidatesCounter.containsKey(candidate)) {
                candidatesCounter.put(candidate, 1);
                return false;
            }
            else {
                candidatesCounter.put(candidate, candidatesCounter.get(candidate) + 1);
                return true;
            }
        }

        public void translateCandidates() {
            String sourceLanguage = userConfig.getSourceLanguage();
            String targetLanguage = userConfig.getTargetLanguage();

            loadSimilarTranslationsFromDB();

            candidatesCounter.forEach( (k,v) -> {
                String source = k;
                String translation;

                if(!translated.contains(source)) {
                    translation = translator.translate(userConfig.getSourceLanguage(),
                            userConfig.getTargetLanguage(), source);
                    translations.put(source, translation);
                }
            });
        }

        public boolean uploadTranslations() {
            for(Iterator<Map.Entry<String, Integer>> entry = candidatesCounter.entrySet().iterator();
                entry.hasNext();) {
                var e = entry.next();
                var k = e.getKey();
                var v = e.getValue();

                String query;
                if (!translated.contains(k)) {
                    query = "INSERT INTO " + TABLE_TRANS + "("
                            + SOURCE_FIELD_NAME + ","
                            + TARGET_FIELD_NAME + ","
                            + DATE_FIELD_NAME + ") "
                            + "VALUES ("
                            + "'" + k + "','" + translations.get(k) + "'," + System.currentTimeMillis()
                            + ")";
                    try {
                        System.out.println(query);
                        statement.execute(query);
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                    }
                }

                // upload counters
                query = "SELECT * FROM " + TABLE_STATS
                        + " WHERE " + SOURCE_FIELD_NAME + " = '" + k + "'";

                try {
                    if (statement.execute(query)) {
                        query = "INSERT INTO " + TABLE_STATS + "("
                                + SOURCE_FIELD_NAME + ","
                                + COUNTER_FIELD_NAME
                                + ") "
                                + "VALUES (" + "'" + k + "'," + v + ") "
                                + "ON CONFLICT(" + SOURCE_FIELD_NAME + ") "
                                + "DO UPDATE SET "
                                + COUNTER_FIELD_NAME + " = " + TABLE_STATS + "." + COUNTER_FIELD_NAME + " + " + v;

                        try {
                            statement.execute(query);
                        } catch (SQLException sqle) {
                            sqle.printStackTrace();
                        }

                        entry.remove();
                    }
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }

            return true;
        }

        private String joinCandidatesKeysForSQL() {
            ArrayList<String> qcand = new ArrayList<>();
            var i = 0;
            candidatesCounter.keySet().forEach( k -> qcand.add(SOURCE_FIELD_NAME + " = '" + k + "'"));
            return String.join(" OR ", qcand);
        }

        public Map<String, String> getTranslations() {
            return (Map)translations.clone();
        }

        public boolean loadSimilarTranslationsFromDB() {
            // Get words which already translated from database
            try {
                String query = "SELECT " + SOURCE_FIELD_NAME + "," + TARGET_FIELD_NAME
                        + " FROM " + TABLE_TRANS
                        + " WHERE " + joinCandidatesKeysForSQL();

                selectorResult = statement.executeQuery(query);

                while(selectorResult.next())
                {
                    translations.put(selectorResult.getString(SOURCE_FIELD_NAME),
                            selectorResult.getString(TARGET_FIELD_NAME)
                    );
                    translated.add(selectorResult.getString(SOURCE_FIELD_NAME));
                }
            }
            catch(SQLException sqle) {
                sqle.printStackTrace();
            }

            return true;
        }

        public boolean loadAllDBTranslations() {
            try {
                var query = "SELECT * FROM " + TABLE_TRANS;
                selectorResult = statement.executeQuery(query);

                while(selectorResult.next()) {
                    translations.put(
                            selectorResult.getString(SOURCE_FIELD_NAME),
                            selectorResult.getString(TARGET_FIELD_NAME)
                    );

                    candidatesCounter.putIfAbsent(selectorResult.getString(SOURCE_FIELD_NAME), 0);
                    translated.add(selectorResult.getString(SOURCE_FIELD_NAME));
                }

                return true;
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
                return false;
            }
        }

        public TreeMap<String, String> pullAllDBTranslations() {
            try {
                TreeMap<String, String> translations = new TreeMap<>();
                var query = "SELECT * FROM " + TABLE_TRANS;
                selectorResult = statement.executeQuery(query);

                while(selectorResult.next()) {
                    translations.put(selectorResult.getString(SOURCE_FIELD_NAME),
                            selectorResult.getString(TARGET_FIELD_NAME));
                }

                return translations;
            }
            catch(SQLException sqle) {
                sqle.printStackTrace();
                return null;
            }
        }

        public TreeMap<String, String> pullRandomDBTranslations() {
            return pullRandomDBTranslations(0);
        }

        private TreeMap<String, String> pullRandomDBTranslations(int limit) {
            TreeMap<String, String> translations = new TreeMap<>();
            var query = "SELECT * FROM " + TABLE_TRANS + "ORDER BY RANDOM()";

            // if limit == 0 do nothing
            if(limit < 0)
                throw new RuntimeException("limit must be greater or equal to 0");
            else if(limit > 0)
                query += " LIMIT " + limit;

            try {
                selectorResult = statement.executeQuery(query);

                while(selectorResult.next()) {
                    translations.put(selectorResult.getString(SOURCE_FIELD_NAME),
                            selectorResult.getString(TARGET_FIELD_NAME));
                }

                return translations;
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
                return null;
            }
        }

        public TreeMap<String, Integer> pullAllDBCounters() {
            TreeMap<String, Integer> counters = new TreeMap<>();
            var query = "SELECT * FROM " + TABLE_STATS;

            try {
                selectorResult = statement.executeQuery(query);

                while(selectorResult.next()) {
                    counters.put(selectorResult.getString(SOURCE_FIELD_NAME),
                            selectorResult.getInt(COUNTER_FIELD_NAME));
                }

                return counters;
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
                return null;
            }
        }

        public void printCandidatesCounter() {
            candidatesCounter.forEach( (k,v) -> System.out.println(k + "\t" + v));
        }

        public void printTranslations() {
            translations.forEach( (k,v) -> System.out.println(k + "\t" + v) );
        }
}

