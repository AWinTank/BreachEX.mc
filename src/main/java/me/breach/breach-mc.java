// ... (Your imports are correct)

public class breach-mc extends JavaPlugin implements Listener {

    private NamespacedKey coreKey;
    private NamespacedKey rollKey;

    @Override
    public void onEnable() {
        this.coreKey = new NamespacedKey(this, "breach_core_type");
        this.rollKey = new NamespacedKey(this, "breach_rerolls"); // Key was missing in your last snippet
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BreachMC Plugin Online!");
    }

    // [All your other methods: onDrop, onDeath, onFirstJoin, createBreachCore are good!]

    // --------- GUI Logic ---------
    private void openBreachGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§0§lBreach Network");

        String currentType = "None";
        for (ItemStack item : player.getInventory().getContents()) {
            if (isAnyBreachCore(item)) {
                currentType = getCoreType(item);
                break;
            }
        }

        // Status Item
        gui.setItem(13, createGuiItem(Material.COMPASS, "§bCurrent Core: §f" + currentType, "§7Detected in your inventory."));

        // Reroll Button
        int rolls = player.getPersistentDataContainer().getOrDefault(rollKey, PersistentDataType.INTEGER, 3);
        gui.setItem(31, createGuiItem(Material.ENDER_EYE, "§a§lReroll Core", "§7Cost: §f1 Charge", "§7Charges Left: §e" + rolls));

        // Close Button
        gui.setItem(49, createGuiItem(Material.BARRIER, "§c§lClose Menu", "§7Click to exit."));

        player.openInventory(gui);
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        // Use stripColor or exact match. If using 1.20.4+, view.getTitle() is sometimes weird.
        if (!event.getView().getTitle().equals("§0§lBreach Network")) return;
        
        event.setCancelled(true); 
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }

        if (clicked.getType() == Material.ENDER_EYE) {
            int rolls = player.getPersistentDataContainer().getOrDefault(rollKey, PersistentDataType.INTEGER, 3);
            
            if (rolls <= 0) {
                player.sendMessage("§cNo rerolls remaining!");
                player.closeInventory();
                return;
            }

            boolean found = false;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (isAnyBreachCore(item)) {
                    String[] types = {"Void", "Solar", "Glitch", "Pulse", "Primal"};
                    String newType = types[ThreadLocalRandom.current().nextInt(types.length)];
                    
                    player.getInventory().setItem(i, createBreachCore(newType));
                    player.getPersistentDataContainer().set(rollKey, PersistentDataType.INTEGER, rolls - 1);
                    
                    player.sendMessage("§b§l[!] §7Core rerolled to: §f" + newType);
                    openBreachGUI(player); // Refresh
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                player.sendMessage("§cYou need a Breach Core in your inventory to reroll!");
            }
        }
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            // This ensures the lore is a properly formatted list
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    // [Remaining helper methods: isAnyBreachCore, getCoreType, etc.]
}