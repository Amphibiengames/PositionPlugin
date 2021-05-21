package de.driver1848.pos.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Config {

    private final File file;
    private final YamlConfiguration config;

    public Config() {
        File dir = new File("./plugins/Positions");

        if(!(dir.exists())){
            dir.mkdirs();
        }

        this.file = new File(dir, "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                Writer myWriter = new FileWriter(file);
                myWriter.write("#DO NOT CHANGE THESE VALUES!!! It might crash the plugin, if you mess up.");
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
