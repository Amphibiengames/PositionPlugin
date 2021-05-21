package de.driver1848.pos.commands;

import de.driver1848.pos.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class posCommand implements CommandExecutor, TabExecutor {

    Main main = Main.getPlugin(Main.class);
    String prefix = main.getPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            switch (args.length){
                case 1 -> {
                    if (args[0].equalsIgnoreCase("list")){
                        listPos(sender);
                        return true;
                    } else {
                        sendUsage(sender);
                        return false;
                    }
                }
                case 2 -> {
                    switch (args[0].toLowerCase()){
                        case "get" -> getPos(sender, args[1]);
                        case "set" -> setPos(sender, args[1]);
                        case "delete" -> deletePos(sender, args[1]);
                        case "teleport" -> teleportPos(sender, args[1]);
                        default -> sendUsage(sender);
                    }
                    return true;
                }
                default -> {
                    sendUsage(sender);
                    return false;
                }
            }
        } else {
            sender.sendMessage(prefix+"§cDu musst ein Spieler sein!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        switch (args.length){
            case 1 -> {
                commands.add("list");
                commands.add("set");
                commands.add("get");
                commands.add("delete");
                commands.add("teleport");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
            case 2 -> {
                if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("teleport")){
                    commands.addAll(main.X.keySet());
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        return completions;
    }


    private void listPos(CommandSender sender) {
        sender.sendMessage(prefix+"§6Positionen, die in der Datenbank gefunden wurden:");

        Set<String> keys = main.X.keySet();
        List<String> keysList = keys.stream().collect(Collectors.toList());
        Collections.sort(keysList);
        int i = 1;
        for (String key : keysList) {
            sender.sendMessage("§6"+i+". "+"§3"+key);
            i++;
        }
    }
    private void getPos(CommandSender sender, String name) {
        if (main.X.containsKey(name)) {
            sender.sendMessage(prefix+"§6Die Position von §3"+name+" §r§6ist: §3"+getString(name));
        } else {
            posNotFound(sender);
        }
    }
    private void setPos(CommandSender sender, String name) {
        if (main.X.containsKey(name)) {
            sender.sendMessage(prefix+"§cDiese Position existiert bereits! Wenn du sie neu setzen möchtest musst du sie erst löschen!");
        } else {
            Player player = (Player) sender;
            Block location = player.getLocation().getBlock();

            main.X.put(name, location.getX());
            main.Y.put(name, location.getY());
            main.Z.put(name, location.getZ());
            main.world.put(name, location.getWorld().getName());

            sender.sendMessage(prefix+"§aDie Position §3"+name+"§r§a wurde erfolgreich auf §3"+getString(name)+"§r§a gesetzt!");
            Bukkit.getOnlinePlayers().forEach(player1 -> {
                if (!(player1.getName().equalsIgnoreCase(sender.getName()))){
                    player1.sendMessage(prefix+"§6Die Position §3"+name+" §r§6wurde von §3"+player.getName()+" §r§6auf §3"+getString(name)+" §r§6gesetzt!");
                }
            });
        }
    }
    private void deletePos(CommandSender sender, String name) {
        if(!(main.X.containsKey(name))){
            posNotFound(sender);
        } else {
            sender.sendMessage(prefix+"§cDie Position §3"+name+" §r§cwurde erfolgreich gelöscht!");
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(!(player.getName().equalsIgnoreCase(sender.getName()))){
                    player.sendMessage(prefix+"§6Die Position §3"+name+" §r§6wurde von §3"+sender.getName()+" §r§6gelöscht! ("+getString(name)+"§r§6)");
                }
            });

            main.X.remove(name);
            main.Y.remove(name);
            main.Z.remove(name);
            main.world.remove(name);
        }
    }
    private void teleportPos(CommandSender sender, String name) {
        if(main.X.containsKey(name)){
            Player player = (Player) sender;
            Location loc = new Location(Bukkit.getWorld(main.world.get(name)), main.X.get(name), main.Y.get(name), main.Z.get(name));
            player.teleport(loc);
            player.sendMessage(prefix+"§aDu wurdest erfolgreich zu der Position §3"+name+" §r§ateleportiert! §6(Es wurde eine Warnung an die Konsole geschickt!)");
            Bukkit.getLogger().warning("[Positions] Der Spieler "+player.getName()+" hat sich zu der Position "+name+" ("+getString(name)+") teleportiert! [WARNING]");
        }else{
            posNotFound(sender);
        }
    }

    private String getString(String name){
        return main.X.get(name)+", "+main.Y.get(name)+", "+main.Z.get(name)+" §6[§b"+main.world.get(name)+"§6]§r";
    }

    private void sendUsage(CommandSender sender){
        sender.sendMessage(prefix+"§cUsage: /pos <set|get|delete|list|teleport> [name: String]");
    }
    private void posNotFound(CommandSender sender){
        sender.sendMessage(prefix + "§cDie Position wurde nicht gefunden!");
    }

    public void save(){
        YamlConfiguration config = main.getYmlConfig();
        config.createSection("pos.world", main.world);
        config.createSection("pos.X", main.X);
        config.createSection("pos.Y", main.Y);
        config.createSection("pos.Z", main.Z);
    }
}
