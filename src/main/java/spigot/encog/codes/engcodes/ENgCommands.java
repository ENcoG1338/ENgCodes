package spigot.encog.codes.engcodes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ENgCommands implements CommandExecutor {
    private ENgCodes plugin;
    private FileConfiguration config;
    private YamlConfiguration usedCodesConfig;
    private List<String> usedNick;
    private List<String> usedIP;

    public ENgCommands(ENgCodes plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.usedCodesConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "usedCodes.yml"));
        this.usedNick = usedCodesConfig.getStringList("usedNick");
        this.usedIP = usedCodesConfig.getStringList("usedIP");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                String codeName = args[0];
                if (config.contains("codes." + codeName)) {
                    String playerCode = player.getUniqueId().toString() + codeName;
                    String ipCode = player.getAddress().getAddress().getHostAddress() + codeName;

                    boolean checkNick = config.getBoolean("codes." + codeName + ".checkNick", true);
                    boolean checkIP = config.getBoolean("codes." + codeName + ".checkIP", true);

                    int maxUsesPerAccount = config.getInt("codes." + codeName + ".maxUsesPerAccount", 1);
                    int maxUsesPerIP = config.getInt("codes." + codeName + ".maxUsesPerIP", 1);

                    int usesPerAccount = Collections.frequency(usedNick, playerCode);
                    int usesPerIP = Collections.frequency(usedIP, ipCode);

                    if ((!checkNick || usesPerAccount < maxUsesPerAccount) && (!checkIP || usesPerIP < maxUsesPerIP)) {
                        List<String> rewards = config.getStringList("codes." + codeName + ".rewards");
                        for (String reward : rewards) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), reward.replace("<player>", player.getName()));
                        }
                        usedNick.add(playerCode);
                        usedIP.add(ipCode);
                        usedCodesConfig.set("usedCodes", usedNick);
                        usedCodesConfig.set("usedIPs", usedIP);
                        try {
                            usedCodesConfig.save(new File(plugin.getDataFolder(), "usedCodes.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String message = config.getString("codes." + codeName + ".message");
                        message = ChatColor.translateAlternateColorCodes('&', message);
                        message = message.replace("\\n", "\n");
                        player.sendMessage(message);

                        String title = config.getString("codes." + codeName + ".title");
                        title = ChatColor.translateAlternateColorCodes('&', title);
                        String subtitle = config.getString("codes." + codeName + ".subtitle");
                        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                        player.sendTitle(title, subtitle, 10, 70, 20);
                    } else {
                        player.sendMessage(config.getString("messages.alreadyUsed", "§fВы уже использовали этот код!"));
                    }
                } else {
                    player.sendMessage(config.getString("messages.codeNotExist", "§fТакого кода не существует!"));
                }
            } else {
                player.sendMessage(config.getString("messages.enterCode", "§fПожалуйста, введите код!"));
            }
        } else {
            sender.sendMessage(config.getString("messages.playersOnly", "§fТолько игроки могут использовать эту команду!"));
        }
        return true;
    }


}