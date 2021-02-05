package net.plazmix.hub_system.listener;

import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.Core;
import net.plazmix.core.api.common.config.LocaleConfig;
import net.plazmix.core.api.service.economy.EconomyService;
import net.plazmix.core.api.service.group.Group;
import net.plazmix.core.api.service.group.GroupService;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.core.api.spigot.nametag.Nametag;
import net.plazmix.core.api.spigot.nametag.NametagManager;
import net.plazmix.hub_system.config.HubPermission;
import net.plazmix.hub_system.config.HubSystemConfig;
import net.plazmix.hub_system.config.ItemsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final HubSystemConfig hubSystemConfig;
    private final LocaleConfig localeConfig;
    private final ItemsConfig itemsConfig;
    private final GroupService groupService = Core.getApi().getService(GroupService.class);
    private final NametagManager nametagManager = ((SpigotCoreApi) Core.getApi()).getNametagManager();

    @EventHandler
    private void on(AsyncPlayerPreLoginEvent event) {
        EconomyService economyService = Core.getApi().getService(EconomyService.class);
        SpigotCoreApi coreApi = (SpigotCoreApi) Core.getApi();
        coreApi.getPlugin().getServer().getScheduler().runTaskAsynchronously(coreApi.getPlugin(), () -> {
            economyService.keep(event.getUniqueId(), economyService.getAccount(event.getUniqueId()), account -> false);
        });
    }

    @EventHandler
    private void on(PlayerLoginEvent event) {
        if (Bukkit.getOnlinePlayers().size() < Bukkit.getMaxPlayers())
            return;

        String permission = hubSystemConfig.getHubPermission(HubPermission.HUB_FULL);
        if (!event.getPlayer().hasPermission(permission)) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, localeConfig.getMessage("full"));
            return;
        }

        Player unlucky = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission))
                continue;
            unlucky = player;
            break;
        }

        if (unlucky != null)
            unlucky.kickPlayer(localeConfig.getMessage("kicked-full"));
    }

    @EventHandler
    private void on(PlayerJoinEvent event) {
        if (hubSystemConfig.getSpawnpoint() != null)
            event.getPlayer().teleport(hubSystemConfig.getSpawnpoint());
        String message = null;
        Group playerGroup = groupService.getCachedData(event.getPlayer().getUniqueId()).get().getGroup();
        if (event.getPlayer().hasPermission(hubSystemConfig.getHubPermission(HubPermission.HUB_JOIN_MESSAGE)))
            message = localeConfig.getMessage("join")
                    .replace("%group%", playerGroup.getDisplayName())
                    .replace("%player%", event.getPlayer().getName());
        event.setJoinMessage(message);
        if (event.getPlayer().hasPermission(hubSystemConfig.getHubPermission(HubPermission.HUB_FLY))) {
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
        }
        Nametag nametag = nametagManager.getNametag(event.getPlayer());
        nametag.updateAll(playerGroup.getPrefix(),
                event.getPlayer().getName(),
                playerGroup.getSuffix(),
                1);
        hubSystemConfig.getSidebar().addPlayer(event.getPlayer());
        itemsConfig.giveLobbyItems(event.getPlayer());
    }

    @EventHandler
    private void clear(PlayerQuitEvent event) {
        hubSystemConfig.getSidebar().removePlayer(event.getPlayer());
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void invalidate(PlayerQuitEvent event) {
        Core.getApi().getService(EconomyService.class).invalidate(event.getPlayer().getUniqueId());
    }
}
