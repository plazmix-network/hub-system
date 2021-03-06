package net.plazmix.hub_system;

import net.plazmix.core.api.common.config.LocaleConfig;
import net.plazmix.core.api.spigot.config.SpigotYamlConfig;
import net.plazmix.hub_system.config.HubSystemConfig;
import net.plazmix.hub_system.listener.ChatListener;
import net.plazmix.hub_system.listener.ConnectionListener;
import net.plazmix.hub_system.listener.PlayerActivityListener;
import net.plazmix.hub_system.listener.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;

public class HubSystemPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        HubSystemConfig hubSystemConfig = new HubSystemConfig(this.getDataFolder(), "config");
        this.saveResource("messages.yml", false);
        LocaleConfig localeConfig = new LocaleConfig(new SpigotYamlConfig(this.getDataFolder(), "messages"));

        this.getServer().getPluginManager().registerEvents(new ConnectionListener(hubSystemConfig, localeConfig), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(localeConfig), this);
        this.getServer().getPluginManager().registerEvents(new PlayerActivityListener(hubSystemConfig), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }
}
