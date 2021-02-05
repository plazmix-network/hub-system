package net.plazmix.hub_system.menu;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.plazmix.core.api.Core;
import net.plazmix.core.api.common.config.LocaleConfig;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import net.plazmix.core.api.spigot.inventory.view.GlobalViewInventory;
import net.plazmix.core.api.spigot.util.ItemBuilder;
import net.plazmix.hub_system.config.HubSystemConfig;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

public class NavigatorMenu {

    private final GlobalViewInventory navigatorMenu;

    public NavigatorMenu(Plugin plugin, HubSystemConfig hubSystemConfig, LocaleConfig locale) {
        SpigotCoreApi coreApi = (SpigotCoreApi) Core.getApi();
        this.navigatorMenu = coreApi.newGlobalViewInventory()
                .setChestRows(6)
                .setTitle(locale.getMessage("navigator-title"))
                .withIcon(49, Icon.of(new ItemBuilder(Material.COMPASS)
                        .setDisplayName(locale.getMessage("navigator-spawn-name"))
                        .setLore(locale.getMessages("navigator-spawn-lore")).build(), click -> {
                    click.getIssuer().closeInventory();
                    click.getIssuer().teleport(hubSystemConfig.getSpawnpoint());
                    click.getIssuer().playSound(click.getIssuer().getLocation(),
                            Sound.ENDERMAN_TELEPORT, 1F, 1F);
                }))
                .withIcon(10, Icon.of(new ItemBuilder(Material.IRON_PICKAXE)
                        .setDisplayName(locale.getMessage("navigator-survival-name"))
                        .setLore(locale.getMessages("navigator-survival-lore"))
                        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), click -> {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("survival");
                    click.getIssuer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }))
                .build();
    }

    public void open(Player player) {
        navigatorMenu.open(player);
    }
}