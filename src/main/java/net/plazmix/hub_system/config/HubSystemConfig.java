package net.plazmix.hub_system.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.plazmix.core.api.Core;
import net.plazmix.core.api.service.economy.EconomyService;
import net.plazmix.core.api.service.group.Group;
import net.plazmix.core.api.service.group.GroupService;
import net.plazmix.core.api.service.group.PermissionData;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.core.api.spigot.config.SpigotYamlConfig;
import net.plazmix.core.api.spigot.sidebar.Sidebar;
import net.plazmix.core.api.spigot.util.Colors;
import net.plazmix.core.api.spigot.util.Position;
import org.bukkit.Location;

import java.io.File;
import java.util.List;
import java.util.Map;

public class HubSystemConfig extends SpigotYamlConfig {

    private final Map<HubPermission, String> hubPermissionMap;
    private final Location spawnpoint;
    private final boolean editMode;
    private final Sidebar sidebar;

    public HubSystemConfig(File folder, String fileName) {
        super(folder, fileName);
        load();
        ImmutableMap.Builder<HubPermission, String> hubPermissionMapBuilder = ImmutableMap.<HubPermission, String>builder();
        for (String permissionName : getKeys("permissions"))
            hubPermissionMapBuilder.put(HubPermission.valueOf(permissionName), getString("permissions." + permissionName));
        this.hubPermissionMap = hubPermissionMapBuilder.build();
        this.spawnpoint = Position.fromString(getString("spawnpoint")).toLocation();
        this.editMode = getBoolean("edit-mode");
        this.sidebar = buildSidebar();
    }

    public HubSystemConfig(File file) {
        super(file);
        ImmutableMap.Builder<HubPermission, String> hubPermissionMapBuilder = ImmutableMap.<HubPermission, String>builder();
        for (String permissionName : getKeys("permissions"))
            hubPermissionMapBuilder.put(HubPermission.valueOf(permissionName), getString("permissions." + permissionName));
        this.hubPermissionMap = hubPermissionMapBuilder.build();
        this.spawnpoint = Position.fromString(getString("spawnpoint")).toLocation();
        this.editMode = getBoolean("edit-mode");
        this.sidebar = buildSidebar();
    }

    public String getHubPermission(HubPermission permission) {
        return hubPermissionMap.get(permission);
    }

    public Location getSpawnpoint() {
        return spawnpoint;
    }

    public boolean isEditMode() {
        return editMode;
    }

    private Sidebar buildSidebar() {
        final String title = Colors.colorize(getString("sidebar.title"));
        final List<String> rawLines = getStringList("sidebar.lines");
        final GroupService groupService = Core.getApi().getService(GroupService.class);
        final EconomyService economyService = Core.getApi().getService(EconomyService.class);
        SpigotCoreApi coreApi = (SpigotCoreApi) Core.getApi();
        return coreApi.newSidebar()
                .setTitleApplier(player -> title)
                .setLinesApplier(player -> {
                    List<String> result = Lists.newArrayList();
                    Group group = groupService.getCachedData(player.getUniqueId())
                            .orElse(PermissionData.of(player.getUniqueId(), Sets.newHashSet(), Group.PLAYER)).getGroup();
                    for (String line : rawLines)
                        result.add(Colors.colorize(line
                                .replace("%prefix%", group.getPrefix())
                                .replace("%groupcolor%", group.getDisplayName().substring(0, 2))
                                .replace("%suffix%", group.getSuffix())
                                .replace("%player%", player.getName())
                                .replace("%group%", group.getDisplayName())
                                .replace("%cookies%", economyService.getCachedData(player.getUniqueId()).get().getWallet(economyService.getCurrency("cookies").get()).getBalance() + "")
                                .replace("%plasma%", economyService.getCachedData(player.getUniqueId()).get().getWallet(economyService.getCurrency("plasma").get()).getBalance() + "")));
                    return result;
                })
                .build();
    }

    public Sidebar getSidebar() {
        return sidebar;
    }
}
