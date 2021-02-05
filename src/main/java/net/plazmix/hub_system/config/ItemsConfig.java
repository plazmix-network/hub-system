package net.plazmix.hub_system.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.plazmix.core.api.common.config.LocaleConfig;
import net.plazmix.core.api.spigot.config.SpigotYamlConfig;
import net.plazmix.core.api.spigot.util.Colors;
import net.plazmix.core.api.spigot.util.DatableMaterial;
import net.plazmix.core.api.spigot.util.ItemBuilder;
import net.plazmix.hub_system.menu.CommandItem;
import net.plazmix.hub_system.menu.NavigatorMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemsConfig extends SpigotYamlConfig implements Listener {

    private final Plugin plugin;
    private final Map<Integer, CommandItem> otherItems = Maps.newHashMap();
    private final ItemStack navigator;

    private final NavigatorMenu navigatorMenu;

    public ItemsConfig(File parent, Plugin plugin, HubSystemConfig hubSystemConfig, LocaleConfig locale) {
        super(parent, "items");
        this.plugin = plugin;
        this.load();

        this.navigatorMenu = new NavigatorMenu(plugin, hubSystemConfig, locale);

        loadExternalItems();

        this.navigator = new ItemBuilder(DatableMaterial.fromString(getString("internal.navigator.icon")))
                .setDisplayName(Colors.colorize(getString("internal.navigator.name")))
                .setLore(Colors.colorize(getStringList("internal.navigator.lore"))).build();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void loadExternalItems() {
        Collection<String> keys = getKeys("external");
        for (String key : keys) {
            List<String> commands = getStringList("external." + key + ".commands");
            if (commands == null)
                commands = Lists.newArrayList();

            CommandItem commandItem = new CommandItem(
                    new ItemBuilder(new DatableMaterial(getString("external." + key + ".icon")))
                            .setDisplayName(Colors.colorize(getString("external." + key + ".name")))
                            .setLore(Colors.colorize(getStringList("external." + key + ".lore"))).build(),
                    commands);
            otherItems.put(getInt("external." + key + ".slot") - 1, commandItem);
        }
    }

    public NavigatorMenu getNavigatorMenu() {
        return navigatorMenu;
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItem(getInt("internal.navigator.slot") - 1, navigator);

        player.getInventory().setHeldItemSlot(getInt("internal.navigator.slot") - 1);

        for (Map.Entry<Integer, CommandItem> entry : otherItems.entrySet())
            player.getInventory().setItem(entry.getKey(), entry.getValue().getItem());
    }

    @EventHandler
    private void onInternal(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !event.getAction().name().contains("RIGHT"))
            return;

        if (item.isSimilar(navigator)) {
            navigatorMenu.open(event.getPlayer());
        }
    }

    @EventHandler
    private void onExternal(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !event.getAction().name().contains("RIGHT"))
            return;

        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        if (!otherItems.containsKey(slot))
            return;
        for (String cmd : otherItems.get(slot).getCommands()) {
            String[] cmdData = cmd.split(":");
            if (cmdData.length <= 1)
                event.getPlayer().chat(cmd.replace("%player%", event.getPlayer().getName()));
            else if (cmdData[0].equalsIgnoreCase("console"))
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        cmdData[1].replace("%player%", event.getPlayer().getName()));
            else if (cmdData[0].equalsIgnoreCase("server")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("survival");
                event.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            }
        }
    }
}