package me.breach;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MyPlugin extends JavaPlugin implements Listener {

private NamespacedKey coreKey;
private NamespacedKey rollKey; 

    @Override
    public void onEnable() {
        this.coreKey = new NamespacedKey(this, "breach_core_type");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Breach-MC Online.");
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            // Pick a random number between 0 and 4 (for the 5 types)
            int typeIndex = ThreadLocalRandom.current().nextInt(0, 5);
            String[] types = {"Void", "Solar", "Glitch", "Pulse", "Primal"};
            String selectedType = types[typeIndex];

            ItemStack core = createBreachCore(selectedType);

            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(core);
            if (!leftovers.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), core);
            }
            player.sendMessage("§b§l[!] §7Welcome! You have been granted a §f Breach Core§7." + SelectedType);
        }
    }

public ItemStack createBreachCore(String type) {
    ItemStack item = new ItemStack(Material.HEAVY_CORE);
    ItemMeta meta = item.getItemMeta();
    
    if (meta != null) {
        String color;
        String description;

        // Customizing color AND description based on the type
        switch (type) {
            case "Void" -> {
                color = "§8";
                description = "§7It feels cold to the touch and seems to swallow light.";
            }
            case "Solar" -> {
                color = "§e";
                description = "§7A faint warmth radiates from deep within the stone.";
            }
            case "Glitch" -> {
                color = "§d";
                description = "§7The surface shimmers and jitters when you aren't looking.";
            }
            case "Pulse" -> {
                color = "§b";
                description = "§7A rhythmic vibration echoes through your hand.";
            }
            case "Primal" -> {
                color = "§2";
                description = "§7Etched with moss that never seems to dry or die.";
            }
            default -> {
                color = "§f";
                description = "§7A mysterious stone of unknown origin.";
            }
        meta.getPersistentDataContainer().set(coreKey, PersistentDataType.STRING, type);
        
        item.setItemMeta(meta);
        }
        return item;

        meta.setDisplayName(color + "§l" + type + " Breach Core");
        
        // Setting the customized Lore
        meta.setLore(Arrays.asList(
            description,
            " ", // Adds a blank line for better spacing
            "§8Type: " + type,
            "§8This item is physically locked."
        ));
        
        // Hidden tag and Glow effect
        meta.getPersistentDataContainer().set(coreKey, PersistentDataType.STRING, type);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); 
        
        item.setItemMeta(meta);
    }
    return item;
}           
            // Store the type string in the hidden tag
            meta.getPersistentDataContainer().set(coreKey, PersistentDataType.STRING, type);
            
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); 
            
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("getcore")) {
            if (!(sender instanceof Player player)) return true;
            
            // Default to Void if no argument is given
            String type = (args.length > 0) ? args[0] : "Void";
            player.getInventory().addItem(createBreachCore(type));
            return true;
        }
        return false;
        // Handle /resetrerolls
if (cmd.getName().equalsIgnoreCase("resetrerolls")) {
    if (sender instanceof Player player) {
        // Set the hidden player data back to 3
        player.getPersistentDataContainer().set(rollKey, PersistentDataType.INTEGER, 3);
        player.sendMessage("§a§l[!] §7Your Breach Rerolls have been reset to §f3§7.");
    }
         return true;
}
    }

if (cmd.getName().equalsIgnoreCase("breach")) {
        if (sender instanceof Player player) {
            openBreachGUI(player);
        }
        return true;
    }

    return false;
}

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isAnyBreachCore(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cThe " + getCoreType(event.getItemInHand()) + " Core cannot be placed!");
        }
    }

@EventHandler
public void onCraft(PrepareItemCraftEvent event) {
    // Check every item currently in the crafting grid
    for (ItemStack item : event.getInventory().getMatrix()) {
        if (isAnyBreachCore(item)) {
            // set the result to null (empty) so they can't take the Mace.
            event.getInventory().setResult(null);
            return; 
        }
    }
}

    private boolean isAnyBreachCore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(coreKey, PersistentDataType.STRING);
    }

    private String getCoreType(ItemStack item) {
        if (!isAnyBreachCore(item)) return "Unknown";
        return item.getItemMeta().getPersistentDataContainer().get(coreKey, PersistentDataType.STRING);
    }





    // ---------GUI-------------------------------------- 
private void openBreachGUI(Player player) {
    Inventory gui = Bukkit.createInventory(null, 54, "§0§lBreach Network");

    // Logic to find which core they are holding
    String currentType = "None";
    for (ItemStack item : player.getInventory().getContents()) {
        if (isAnyBreachCore(item)) {
            currentType = getCoreType(item); // Reads the hidden tag
            break;
        }
    }

    // Status Item
    gui.setItem(13, createGuiItem(Material.COMPASS, "§bCurrent Core: §f" + currentType, "§7Detected in your inventory."));

    // Reroll Button (Checks player data for remaining charges)
    int rolls = player.getPersistentDataContainer().getOrDefault(rollKey, PersistentDataType.INTEGER, 3);
    gui.setItem(31, createGuiItem(Material.ENDER_EYE, "§a§lReroll Core", "§7Cost: §f1 Charge", "§7Charges Left: §e" + rolls));

    // Close Button
    gui.setItem(49, createGuiItem(Material.BARRIER, "§c§lClose Menu", "§7Click to exit."));

    player.openInventory(gui);
}
    // This command opens the window for the player
    player.openInventory(gui);
    player.sendMessage("§b§l[!] §7Accessing the Breach...");
}
@EventHandler
public void onGuiClick(InventoryClickEvent event) {
    // Only run this if the player is clicking inside the Breach GUI
    if (!event.getView().getTitle().equals("§0§lBreach Network")) return;
    
    event.setCancelled(true); // Prevents players from stealing the GUI items
    Player player = (Player) event.getWhoClicked();
    ItemStack clicked = event.getCurrentItem();

    if (clicked == null || clicked.getType() == Material.AIR) return;

    // Button: Close
    if (clicked.getType() == Material.BARRIER) {
        player.closeInventory();
    }

    // Button: Reroll
    if (clicked.getType() == Material.ENDER_EYE) {
        int rolls = player.getPersistentDataContainer().getOrDefault(rollKey, PersistentDataType.INTEGER, 3);
        
        if (rolls <= 0) {
            player.sendMessage("§cNo rerolls remaining!");
            return;
        }

        // Search for the core to swap it
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isAnyBreachCore(item)) {
                String[] types = {"Void", "Solar", "Glitch", "Pulse", "Primal"};
                String newType = types[ThreadLocalRandom.current().nextInt(0, 5)];
                
                player.getInventory().setItem(i, createBreachCore(newType));
                player.getPersistentDataContainer().set(rollKey, PersistentDataType.INTEGER, rolls - 1);
                
                player.sendMessage("§b§l[!] §7Core rerolled to: §f" + newType);
                openBreachGUI(player); // Refresh the GUI to update the numbers
                return;
            }
        }
    }
}
