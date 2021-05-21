package de.driver1848.pos;

import de.driver1848.pos.commands.posCommand;
import de.driver1848.pos.utils.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Main extends JavaPlugin {
    public HashMap<String, Integer> X;
    public HashMap<String, Integer> Y;
    public HashMap<String, Integer> Z;
    public HashMap<String, String> world;

    private Config config;

    @Override
    public void onLoad() {
        config = new Config();
    }

    @Override
    public void onEnable() {
        getCommand("pos").setExecutor(new posCommand());
        getCommand("pos").setTabCompleter(new posCommand());

        loadParams();
    }

    @Override
    public void onDisable() {
        new posCommand().save();

        config.save();
    }

    private void loadParams(){
        if(getYmlConfig().contains("pos.world") && getYmlConfig().contains("pos.X") && getYmlConfig().contains("pos.Y") && getYmlConfig().contains("pos.Z")){
            YamlConfiguration configuration = getYmlConfig();

            HashMap<String, Object> loadWorld = (HashMap<String, Object>) configuration.getConfigurationSection("pos.world").getValues(false);
            world = (HashMap) loadWorld;

            HashMap<String, Object> loadX = (HashMap<String, Object>) configuration.getConfigurationSection("pos.X").getValues(false);
            X = (HashMap) loadX;

            HashMap<String, Object> loadY = (HashMap<String, Object>) configuration.getConfigurationSection("pos.Y").getValues(false);
            Y = (HashMap) loadY;

            HashMap<String, Object> loadZ = (HashMap<String, Object>) configuration.getConfigurationSection("pos.Z").getValues(false);
            Z = (HashMap) loadZ;
        }else{
            world = new HashMap<>();
            X = new HashMap<>();
            Y = new HashMap<>();
            Z = new HashMap<>();
        }
    }

    public YamlConfiguration getYmlConfig(){
        return config.getConfig();
    }

    public String getPrefix(){
        return "§6[§3§lPositions§r§6]§r ";
    }
}
