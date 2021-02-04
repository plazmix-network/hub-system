package net.plazmix.hub_system.listener;

import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.Core;
import net.plazmix.core.api.common.config.LocaleConfig;
import net.plazmix.core.api.service.group.Group;
import net.plazmix.core.api.service.group.GroupService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final LocaleConfig localeConfig;
    private final GroupService groupService = Core.getApi().getService(GroupService.class);

    @EventHandler
    private void on(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        Group group = groupService.getCachedData(event.getPlayer().getUniqueId()).get().getGroup();
        event.setFormat(localeConfig.getMessage("chat-format")
                .replace("%sender%", event.getPlayer().getName())
                .replace("%prefix%", group.getPrefix())
                .replace("%group%", group.getDisplayName())
                .replace("%suffix%", group.getSuffix())
                .replace("%message%", event.getMessage().replace("%", "")));
    }
}
