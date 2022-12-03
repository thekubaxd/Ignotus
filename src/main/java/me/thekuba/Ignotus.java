package me.thekuba;

import me.thekuba.commands.Otchlan;
import me.thekuba.commands.Self;
import me.thekuba.commands.Set;
import me.thekuba.commands.Setadmin;
import me.thekuba.handlers.*;
import me.thekuba.inventories.AbyssInventory;
import me.thekuba.items.ItemPersi;
import me.thekuba.placeholders.PersivalExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import me.thekuba.files.PlayersManager;
import me.thekuba.files.GroupsManager;
import net.milkbowl.vault.permission.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Ignotus extends JavaPlugin implements Listener {

    public ClearHandler clear = null;

    public PlayersManager playersFile;

    public GroupsManager groupsFile;

    public DamageHandler pvp;

    public List<Inventory> abyssInv = new ArrayList<>();

    public static Permission perms = null;

    private static final String prefixInfo = "[Persival] ";

    private static final String prefixWarn = "[Persival]   ";
    @Override
    public void onEnable() {
        this.playersFile = new PlayersManager(this);
        this.groupsFile = new GroupsManager(this);
        saveDefaultConfig();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            toConsoleWarn("You don't have PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new PersivalExpansion()).register();
        if (Bukkit.getPluginManager().getPlugin("NBTAPI") == null) {
            toConsoleWarn("You don't have NBTAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            toConsoleWarn("You don't have Vault! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
        }
        setupPermissions();
        getCommand("set").setExecutor(new Set());
        getCommand("setadmin").setExecutor(new Setadmin());
        getCommand("otchlan").setExecutor(new Otchlan());
        getCommand("self").setExecutor(new Self());
        new PlayerClickHandler(this);
        new ClickInvHandler(this);
        new CloseInvHandler(this);
        new PlayerJoinHandler(this);
        this.pvp = new DamageHandler(this);
        if (getConfig().getBoolean("abyss.enable")) {
            this.clear = new ClearHandler((Plugin)this);
            List<ItemStack> items = this.clear.getItems();
            applyAbyss(items);
        }
        toConsoleInfo("The Persival plugin has been successfully loaded.");
    }

    @Override
    public void onDisable() {

    }

    public static void toConsoleInfo(String msg) {
        Bukkit.getLogger().info("[Persival] " + msg);
    }

    public static void toConsoleWarn(String msg) {
        Bukkit.getLogger().warning("[Persival]   " + msg);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = (Permission)rsp.getProvider();
        return (perms != null);
    }

    public void applyAbyss(List<ItemStack> items) {
        int itemsSize = items.size();
        this.abyssInv = new ArrayList<>();
        List<ItemStack> items2 = new ArrayList<>();
        for (ItemStack item : items)
            items2.add(item);
        if (itemsSize > 36) {
            for (int i = 0; i < itemsSize / 36 + 1; i++) {
                List<ItemStack> itemsOne = new ArrayList<>();
                for (int i2 = 0; i2 < 36; i2++) {
                    if (i2 == items2.size())
                        break;
                    itemsOne.add(items2.get(i2));
                    items.remove(0);
                }
                items2.clear();
                for (ItemStack item : items)
                    items2.add(item);
                if (i == 0) {
                    AbyssInventory invTemp = new AbyssInventory(itemsOne, 0, i);
                    ItemPersi itemTemp = new ItemPersi(invTemp.inv.getItem(0));
                    itemTemp.setIntNBT("abyssValue", i);
                    this.abyssInv.add(invTemp.inv);
                } else if (i == itemsSize / 36 && i != 0) {
                    AbyssInventory invTemp = new AbyssInventory(itemsOne, 1, i);
                    ItemPersi itemTemp = new ItemPersi(invTemp.inv.getItem(0));
                    itemTemp.setIntNBT("abyssValue", i);
                    this.abyssInv.add(invTemp.inv);
                } else {
                    AbyssInventory invTemp = new AbyssInventory(itemsOne, 2, i);
                    ItemPersi itemTemp = new ItemPersi(invTemp.inv.getItem(0));
                    itemTemp.setIntNBT("abyssValue", i);
                    this.abyssInv.add(invTemp.inv);
                }
            }
        } else {
            AbyssInventory invTemp = new AbyssInventory(items, 3, 0);
            ItemPersi itemTemp = new ItemPersi(invTemp.inv.getItem(0));
            itemTemp.setIntNBT("abyssValue", 0);
            this.abyssInv.add(invTemp.inv);
        }
    }
}