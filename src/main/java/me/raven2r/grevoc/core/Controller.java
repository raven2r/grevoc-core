package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Controller {
    public static void main(String[] args) {
        var userConfig = new UserConfig(args[1]);

        var paths = new ArrayList<Path>();
        paths.add(GlobalConfig.getRootDirectoryPath());
        paths.add(GlobalConfig.getDataDirectoryPath());
        paths.add(GlobalConfig.getUsersDirectoryPath());
        paths.add(GlobalConfig.getUsersDirectoryPath().resolve(args[1]));

        makeDirectoryConsistence(paths, userConfig);
    }

    private static boolean makeDirectoryConsistence(ArrayList<Path> p, UserConfig uc) {
        for(Path path: p) {
            File directory = path.toFile();

            if ( !directory.exists() ) {
                directory.mkdir();
                continue;
            }

            if( !directory.isDirectory() )
                return false;
        }

        return true;
    }
}
