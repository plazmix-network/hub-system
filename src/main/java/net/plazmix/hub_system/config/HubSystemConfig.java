package net.plazmix.hub_system.config;

import com.google.common.collect.ImmutableMap;
import net.plazmix.core.api.spigot.config.SpigotYamlConfig;
import net.plazmix.core.api.spigot.util.Position;
import org.bukkit.Location;

import java.io.File;
import java.util.Map;

public class HubSystemConfig extends SpigotYamlConfig {

    private final Map<HubPermission, String> hubPermissionMap;
    private final Location spawnpoint;
    private final boolean editMode;

    public HubSystemConfig(File folder, String fileName) {
        super(folder, fileName);
        load();
        ImmutableMap.Builder<HubPermission, String> hubPermissionMapBuilder = ImmutableMap.<HubPermission, String> builder();
        for (String permissionName : getKeys("permissions"))
            hubPermissionMapBuilder.put(HubPermission.valueOf(permissionName), getString("permissions." + permissionName));
        this.hubPermissionMap = hubPermissionMapBuilder.build();
        this.spawnpoint = Position.fromString(getString("spawnpoint")).toLocation();
        this.editMode = getBoolean("edit-mode");
    }

    public HubSystemConfig(File file) {
        super(file);
        ImmutableMap.Builder<HubPermission, String> hubPermissionMapBuilder = ImmutableMap.<HubPermission, String> builder();
        for (String permissionName : getKeys("permissions"))
            hubPermissionMapBuilder.put(HubPermission.valueOf(permissionName), getString("permissions." + permissionName));
        this.hubPermissionMap = hubPermissionMapBuilder.build();
        this.spawnpoint = Position.fromString(getString("spawnpoint")).toLocation();
        this.editMode = getBoolean("edit-mode");
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
}
