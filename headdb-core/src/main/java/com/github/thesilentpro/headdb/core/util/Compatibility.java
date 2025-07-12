package com.github.thesilentpro.headdb.core.util;

import com.github.thesilentpro.headdb.core.factory.ItemFactoryRegistry;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;

@SuppressWarnings("deprecation")
public class Compatibility {

    public static final boolean IS_PAPER;

    static {
        boolean isPaper;
        try {
            Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
        IS_PAPER = isPaper;
    }

    public static Executor getMainThreadExecutor(JavaPlugin plugin) {
        if (plugin == null) {
            throw new RuntimeException("Plugin instance is null!");
        }
        if (IS_PAPER) {
            return plugin.getServer().getScheduler().getMainThreadExecutor(plugin);
        } else {
            return r -> plugin.getServer().getScheduler().runTask(plugin, r);
        }
    }

    public static String getPluginVersion(JavaPlugin plugin) {
        if (plugin == null) {
            throw new RuntimeException("Plugin instance is null!");
        }
        if (IS_PAPER) {
            return plugin.getPluginMeta().getVersion();
        } else {
            return plugin.getDescription().getVersion();
        }
    }

    public static void sendMessage(CommandSender sender, Component component) {
        if (sender == null) {
            return;
        }
        if (component == null) {
            return; // Silently fail on null components to avoid exceptions
        }
        if (IS_PAPER) {
            sender.sendMessage(component);
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(component)));
        }
    }

    public static void playSound(Player player, Sound sound) {
        if (sound == null) {
            return;
        }
        if (IS_PAPER) {
            player.playSound(sound);
        } else {
            player.playSound(player, sound.name().value(), sound.volume(), sound.pitch());
        }
    }

    public static void playSound(CommandSender sender, Sound sound) {
        if (!(sender instanceof Player)) {
            return;
        }
        playSound((Player) sender, sound);
    }

    // TODO: Refactor usages of the below methods to use item factory instead of delegating to it.

    public static ItemStack setItemDetails(ItemStack item, Component name, @NotNull Component @Nullable ... lore) {
        return ItemFactoryRegistry.get().setItemDetails(item, name, lore);
    }

    public static ItemStack setItemDetails(ItemStack item, Component name) {
        return setItemDetails(item, name, (Component[]) null);
    }

    public static ItemStack newItem(Material material) {
        return ItemFactoryRegistry.get().newItem(material);
    }

    public static ItemStack newItem(Material material, Component name, Component... lore) {
        return ItemFactoryRegistry.get().newItem(material, name, lore);
    }
    
}
