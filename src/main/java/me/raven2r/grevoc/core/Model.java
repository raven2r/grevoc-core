package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;

public class Model {
        private UserConfig userConfig;
        private Connection connection;
        private String databaseURL;


        public Model (UserConfig userConfig) {
            this.userConfig = userConfig;
            makeDirectoryConsistence();
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
}

