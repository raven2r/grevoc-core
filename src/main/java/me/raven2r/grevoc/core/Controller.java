package me.raven2r.grevoc.core;

import me.raven2r.grevoc.core.config.GlobalConfig;
import me.raven2r.grevoc.core.config.UserConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Controller {
    public static void main(String[] args) {
        var userConfig = new UserConfig(args[1]);
    }
}
