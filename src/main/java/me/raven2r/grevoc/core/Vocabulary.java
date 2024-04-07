package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;
import me.raven2r.grevoc.core.translator.Deepl;
import me.raven2r.grevoc.core.translator.Translates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class Vocabulary {
    private final String TABLE_NAME = "translations";
    private final String SOURCE_FIELD_NAME = "source_language";
    private final String TARGET_FIELD_NAME = "target_language";
    private final String ADDED_FIELD_NAME = "added";
    private final String COUNTER_FIELD_NAME = "counter";

    private final UserConfig userConfig;
    private final Path candidatesFile;
    private final String databaseURL;
    private Translates translator;
    private final Connection connection;

    private final Map<String, Integer> candidates = new TreeMap<>();
    private final ArrayList<Translation> translations = new ArrayList<>();
    private final ArrayList<Translation> dbTranslations = new ArrayList<>();

    /**
     * Model for communication with SQLite database
     *
     * @param userConfig prepared user configuration
     */
    public Vocabulary(UserConfig userConfig) {
        this.userConfig = userConfig;
        candidatesFile = userConfig.getHomeDirPath()
                .resolve(GlobalConfig.USER_TRANSLATION_CANDIDATES_FILE_NAME);

        makeDirectoryConsistence();

        try {
            var dbPath = userConfig.getHomeDirPath().resolve(GlobalConfig.USER_DATABASE_FILE_NAME);
            databaseURL = "jdbc:sqlite:" + dbPath.toAbsolutePath();
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }

        makeTable();
        translator = Translates.byName(userConfig.getTranslatorName());
    }

    /**
     * Make directory consistence
     * tries to check and fix directory structure
     *
     * @return true if success
     */
    public static boolean makeDirectoryConsistence() {
        ArrayList<Path> paths = new ArrayList<Path>();
        paths.add(GlobalConfig.getRootDirectoryPath());
        paths.add(GlobalConfig.getDataDirectoryPath());
        paths.add(GlobalConfig.getUsersDirectoryPath());

        for (Path path : paths) {
            File directory = path.toFile();

            if ((!directory.exists() && !directory.mkdir())
                    || !directory.isDirectory())
                return false;
        }

        return true;
    }

    /**
     * Make database table if not exits
     *
     * @return true if success
     */
    public boolean makeTable() {
        var query = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "("
                //+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + SOURCE_FIELD_NAME + " TEXT NOT NULL,"
                + TARGET_FIELD_NAME + " TEXT NOT NULL,"
                + ADDED_FIELD_NAME + " INTEGER NOT NULL,"
                + COUNTER_FIELD_NAME + " INTEGER NOT NULL DEFAULT 0,"
                + "PRIMARY KEY (" + SOURCE_FIELD_NAME + ")"
                + ")";

        try(var statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Pull all database translations
     *
     * @return true in case of successful database interaction, false otherwise
     */
    public boolean pullAllDBTranslations() {
        String queryTrans;

        if(translations.isEmpty())
            queryTrans = "SELECT * FROM " + TABLE_NAME;
        else
            queryTrans = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + SOURCE_FIELD_NAME + " <> " + joinTranslationsInOrKeysForSQL();

        try(var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(queryTrans);

            while (resultSet.next()) {
                var translation = buildTranslationFromRow(resultSet);

                if (null == getDBTranslationBySource(translation.getSource()))
                    dbTranslations.add(translation);
            }

            Collections.sort(dbTranslations);
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    /**
     * Suspends Model activity
     * closes JDBC connection
     *
     * @return true if connection suspended correctly, false if case of an error
     */
    public boolean suspend() {
        try {
            connection.close();
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }


    public boolean loadCandidatesFromFile() {
        return loadCandidatesFromFile(userConfig.getHomeDirPath()
                .resolve(GlobalConfig.USER_TRANSLATION_CANDIDATES_FILE_NAME));
    }

    public boolean loadCandidatesFromFile(Path candidatesPath) {
        List<String> lines;
        try {
            lines = Files.readAllLines(candidatesPath);

            if (lines.isEmpty())
                return false;

            // add checks for number of words
            lines.forEach(this::addCandidate);
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public boolean deleteCandidatesFile() {
        try {
            Files.delete(candidatesFile);
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }


    /**
     * Add candidate to candidates list
     *
     * @param candidate
     * @return true if candidate is new in this session, not in db
     */
    public boolean addCandidate(String candidate) {
        if (!candidates.keySet().contains(candidate)) {
            candidates.put(candidate, 1);
        } else {
            candidates.put(candidate, candidates.get(candidate) + 1);
        }
        return true;
    }

    /**
     * Add translation to translations list
     *
     * @param translation
     * @return true on success
     */
    public boolean addTranslation(Translation translation) {
        return _addTranslation(translation, translations);
    }

    public boolean addDBTranslation(Translation translation) {
        return _addTranslation(translation, dbTranslations);
    }

    public boolean _addTranslation(Translation translation, ArrayList<Translation> to) {
        if (null == _getTranslationBySource(translation.getSource(), to)) {
            to.add(translation);
            return true;
        }

        return false;
    }

    public Translation getTranslationBySource(String source) {
        return _getTranslationBySource(source, translations);
    }

    public Translation getTranslationByTarget(String target) {
        return _getTranslationByTarget(target, translations);
    }

    public Translation getTranslationByAdded(Long added) {
        return _getTranslationByAdded(added, translations);
    }

    public Translation getDBTranslationBySource(String source) {
        return _getTranslationBySource(source, dbTranslations);
    }

    public Translation getDBTranslationByTarget(String target) {
        return _getTranslationByTarget(target, dbTranslations);
    }

    public Translation getDBTranslationByAdded(long added) {
        return _getTranslationByAdded(added, dbTranslations);
    }

    private Translation _getTranslationBySource(String source, ArrayList<Translation> from) {
        return from.stream()
                .filter(t -> t.getSource().equals(source)).findFirst().orElse(null);
    }

    private Translation _getTranslationByTarget(String target, ArrayList<Translation> from) {
        return from.stream()
                .filter(t -> t.getTarget().equals(target)).findFirst().orElse(null);
    }

    private Translation _getTranslationByAdded(long added, ArrayList<Translation> from) {
        return from.stream()
                .filter(t -> t.getAdded() == added).findFirst().orElse(null);
    }

    public Translation buildTranslationFromRow(ResultSet resultSet) {
        try {
            if (resultSet.isAfterLast())
                return null;

            var translationBuilder = new TranslationBuilder()
                    .source(resultSet.getString(SOURCE_FIELD_NAME))
                    .target(resultSet.getString(TARGET_FIELD_NAME))
                    .added(resultSet.getLong(ADDED_FIELD_NAME))
                    .counter(resultSet.getInt(COUNTER_FIELD_NAME));

            return translationBuilder.build();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public boolean hasCandidate(String candidate) {
        if (candidates.keySet().contains(candidate))
            return true;

        return false;
    }

    public boolean hasTranslation(String source) {
        return (null == getTranslationBySource(source));
    }

    /** Checks if Database of database internal cache has specified translation
     * by source
     * @param source source text
     * @return
     */
    public boolean dbHasTranslation(String source) {
        if (null == dbTranslations.stream().filter(t -> t.getSource().equals(source))
                .findAny().orElse(null))
            return true;

        try(var statement =  connection.createStatement()) {
            var query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + SOURCE_FIELD_NAME + " = '" + source + "'";
            int numberOfRows = statement.executeUpdate(query);

            if (numberOfRows > 0)
                return true;
            else
                return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public void translateCandidates() {
        if (candidates.isEmpty())
            return;

        String sourceLanguage = userConfig.getSourceLanguage();
        String targetLanguage = userConfig.getTargetLanguage();

        pullDBSimilarTranslationsAsCandidates();

        for (Iterator<String> i = candidates.keySet().iterator(); i.hasNext(); ) {
            String source = i.next();
            String target;
            Translation translation;
            Translation dbTranslation;

            // REWRITE
            pullAllDBTranslations();

            translation = getTranslationBySource(source);
            dbTranslation = getDBTranslationBySource(source);

            if (null != translation) {
                translation.appendCounter(candidates.get(source));
            } else if (null != dbTranslation) {
                translations.add(dbTranslation.clone().setCounter(candidates.get(source)));
            } else {
                target = translator.translate(userConfig.getSourceLanguage(),
                        userConfig.getTargetLanguage(), source);

                translations.add(Translation.newSimple(source, target));
            }

            i.remove();
        }
    }

    public boolean pullDBSimilarTranslationsAsCandidates() {
        var query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + SOURCE_FIELD_NAME + " = " + joinCandidatesInOrKeysForSQL();

        try(var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())
                dbTranslations.add(buildTranslationFromRow(resultSet));

            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean pushTranslations() {
        for (Iterator<Translation> entry = translations.iterator();
             entry.hasNext(); ) {
            var e = entry.next();
            pushTranslation(e);
            dbTranslations.add(e);
            //translations.remove(e);
        }

        return true;
    }

    public Translation pullTranslationBySource(String source) {
        try(var statement = connection.createStatement()) {
            var query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + SOURCE_FIELD_NAME + " = '" + source + "'";

            var resultSet = statement.executeQuery(query);
            return buildTranslationFromRow(resultSet);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public Translation pullTranslationByTarget(String target) {
        try(var statement = connection.createStatement()) {
            var query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + TARGET_FIELD_NAME + " = '" + target + "'";

            var resultSet = statement.executeQuery(query);
            return buildTranslationFromRow(resultSet);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public Translation pullTranslationByAdded(long added) {
        try(var statement = connection.createStatement()) {
            var query = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + ADDED_FIELD_NAME + " = " + added;

            var resultSet = statement.executeQuery(query);
            return buildTranslationFromRow(resultSet);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    private boolean pushTranslation(Translation translation) {
        var source = translation.getSource();
        var target = translation.getTarget();
        var added = translation.getAdded();
        var counter = translation.getCounter();

        pullAllDBTranslations();

        try(var statement = connection.createStatement()) {
            // insert if not exists
            if (null == getDBTranslationBySource(translation.getSource())) {
                var query = "INSERT INTO " + TABLE_NAME + "("
                        + SOURCE_FIELD_NAME + ","
                        + TARGET_FIELD_NAME + ","
                        + ADDED_FIELD_NAME + ","
                        + COUNTER_FIELD_NAME + ") "
                        + "VALUES ("
                        + "'" + source + "',"
                        + "'" + target + "',"
                        + added + ","
                        + counter
                        + ")";

                statement.executeUpdate(query);
            }
            // REWRITE
            // increase counter when already exists
            else {
                var counterOld = getDBTranslationBySource(translation.getSource()).getCounter();
                var queryUpdate = "UPDATE " + TABLE_NAME
                        + " SET " + COUNTER_FIELD_NAME + " = " + (counterOld + counter)
                        + " WHERE " + SOURCE_FIELD_NAME + " = '" + source + "'";

                statement.executeUpdate(queryUpdate);
            }
        } catch (SQLException sqle) {
            if (sqle.getMessage().contains("UNIQUE constraint failed")) {
                try(var substatement = connection.createStatement()) {
                    var query = "SELECT " + COUNTER_FIELD_NAME
                            + " FROM " + TABLE_NAME
                            + " WHERE " + SOURCE_FIELD_NAME + " = '" + source + "'";

                    var resultSet = substatement.executeQuery(query);

                    if (resultSet.next()) {
                        var counterOld = resultSet.getInt(COUNTER_FIELD_NAME);
                        var queryUpdate = "UPDATE " + TABLE_NAME
                                + " SET " + COUNTER_FIELD_NAME + " = " + counterOld + counter
                                + " WHERE " + SOURCE_FIELD_NAME + " = '" + source + "'";

                        substatement.executeUpdate(queryUpdate);
                    } else
                        return false;
                } catch (SQLException sqleInner) {
                    sqleInner.printStackTrace();
                    return false;
                }
            } else {
                sqle.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private String joinCandidatesInOrKeysForSQL() {
        ArrayList<String> qcand = new ArrayList<>();
        candidates.forEach((k, v) -> qcand.add("'" + k + "'"));
        return String.join(" OR ", qcand);
    }

    private String joinTranslationsInOrKeysForSQL() {
        ArrayList<String> orQuery = new ArrayList<>();
        translations.forEach(t -> orQuery.add("'" + t.getSource() + "'"));
        return String.join(" OR ", orQuery);
    }

    public ArrayList<Translation> getTranslations() {
        return (ArrayList<Translation>) translations.clone();
    }

    public ArrayList<Translation> getDBTranslations() {
        return (ArrayList<Translation>) dbTranslations.clone();
    }

    public Map<String, Integer> getCandidates() {
        return (Map<String, Integer>) new TreeMap<String, Integer>(candidates);
    }

    public ArrayList<Translation> pullRandomlyAllDBTranslations() {
        return pullRandomDBTranslations(0);
    }

    private ArrayList<Translation> pullRandomDBTranslations(int limit) {
        ArrayList<Translation> translations = new ArrayList<>();

        // if limit == 0 do not modify query (select all)
        var query = "SELECT * FROM " + TABLE_NAME + " ORDER BY RANDOM()";
        if (limit < 0)
            throw new IllegalArgumentException("limit must be greater or equal to 0");
        else if (limit > 0)
            query += " LIMIT " + limit;

        try(var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(query);

            while (resultSet.next())
                translations.add(buildTranslationFromRow(resultSet));

            return translations;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public void setTranslator(Translates translator) {
        this.translator = translator;
    }

    public void printCandidates() {
        if (candidates.isEmpty())
            System.out.println("No candidates");
        else
            candidates.forEach((k, v) -> System.out.println(k + "\t" + v));
    }

    public void printTranslations() {
        if (translations.isEmpty())
            System.out.println("No translations");
        else
            translations.forEach(t -> System.out.println(t.getSource() + "\t" + t.getTarget()));
    }

    /**
     * Prints all translations from database
     */
    public void printDBTranslations() {
        if (dbTranslations.isEmpty()) {
            System.out.println("No pulled translations");
        }

        dbTranslations.forEach(t -> System.out.println(t.getSource()
                        + "\t" + t.getTarget()
                        + "\t" + t.getCounter()
                        + "\t" + t.getAdded()
                )
        );
    }
}

