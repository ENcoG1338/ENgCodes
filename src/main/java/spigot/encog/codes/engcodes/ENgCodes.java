package spigot.encog.codes.engcodes;

import org.bukkit.plugin.java.JavaPlugin;

public final class ENgCodes extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("§aвключён. §fby ENcoG");
        this.saveDefaultConfig();
        this.getCommand("code").setExecutor(new ENgCommands(this));
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        getLogger().info("§cотключён. §fby ENcoG");
        // Plugin shutdown logic
    }


}
