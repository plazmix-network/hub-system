package net.plazmix.hub_system.menu;

import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandItem {

    private final ItemStack item;
    private final List<String> commands;

    public CommandItem(ItemStack item, List<String> commands) {
        this.item = item;
        this.commands = Lists.newArrayList(commands);
    }

    public ItemStack getItem() {
        return item;
    }

    public List<String> getCommands() {
        return commands;
    }
}