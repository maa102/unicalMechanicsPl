package ma102.vervain.menu;

import ma102.vervain.UnicaleMechanics;
import ma102.vervain.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ColorMenu implements Listener {

    private static final String TITLE = "ВЫБОР ЦВЕТА НИКНЕЙМА";
    private final UnicaleMechanics plugin;
    private final Map<UUID, Integer> animationTasks = new HashMap<>();

    public ColorMenu(UnicaleMechanics plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, Component.text("§8§l▖ §6§lВЫБОР ЦВЕТА НИКНЕЙМА §8§l▖"));
        fillBackground(menu);
        menu.setItem(49, createResetItem());

        int slot = 10;
        for (Map.Entry<String, ColorUtils.GradientColor> entry : ColorUtils.GRADIENTS.entrySet()) {
            if (slot > 43) break;
            if (slot % 9 == 8) slot += 2;
            if (slot % 9 == 0) slot += 1;
            menu.setItem(slot, createGradientItem(entry.getValue()));
            slot++;
        }

        player.openInventory(menu);
        startMenuAnimation(player, menu);
        player.sendMessage("§8┃ §7Выбери переливающийся цвет для своего никнейма!");
    }

    private void fillBackground(Inventory menu) {
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.displayName(Component.text(" "));
        bg.setItemMeta(bgMeta);
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, bg);
            }
        }
    }

    private ItemStack createGradientItem(ColorUtils.GradientColor gradient) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(gradient.getAnimatedComponent(gradient.getName(), 0).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§8§m--------------------------------"));
        lore.add(Component.text("§7✦ §fЭффект: §dПереливающийся"));
        lore.add(Component.text("§7✦ §fТип: §6Градиент"));
        lore.add(Component.text("§8§m--------------------------------"));
        lore.add(Component.text("§e⚑ §fНажмите, чтобы применить!"));
        lore.add(Component.text("§7   Ваш никнейм будет красиво переливаться"));
        lore.add(Component.text("§8§m--------------------------------"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createResetItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§c§lСБРОСИТЬ ЦВЕТ"));
        meta.lore(Arrays.asList(
                Component.text("§8§m--------------------------------"),
                Component.text("§7➤ §fВернуть стандартный цвет никнейма"),
                Component.text("§8§m--------------------------------"),
                Component.text("§c⚠ §7Никнейм станет обычным, без эффектов"),
                Component.text("§8§m--------------------------------")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private void startMenuAnimation(Player player, Inventory menu) {
        UUID playerId = player.getUniqueId();
        int taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.getOpenInventory().getTopInventory() != menu) {
                    cancelAnimation(playerId);
                    return;
                }

                for (Map.Entry<String, ColorUtils.GradientColor> entry : ColorUtils.GRADIENTS.entrySet()) {
                    for (int slot = 0; slot < menu.getSize(); slot++) {
                        ItemStack item = menu.getItem(slot);
                        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                            String itemName = item.getItemMeta().getDisplayName().toString();
                            if (itemName.contains(entry.getValue().getName())) {
                                ItemMeta meta = item.getItemMeta();
                                meta.displayName(entry.getValue().getAnimatedComponent(entry.getValue().getName(), tick));
                                item.setItemMeta(meta);
                            }
                        }
                    }
                }
                tick++;
            }
        }, 0L, 2L).getTaskId();

        animationTasks.put(playerId, taskId);
    }

    private void cancelAnimation(UUID playerId) {
        Integer taskId = animationTasks.remove(playerId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (title.contains(TITLE)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            if (clicked.getType() == Material.BARRIER) {
                plugin.getNicknameManager().removePlayerColor(player);
                player.closeInventory();
                player.sendMessage("§a✓ Цвет никнейма сброшен!");
                return;
            }

            String displayName = clicked.getItemMeta().getDisplayName().toString();
            for (String colorName : ColorUtils.GRADIENTS.keySet()) {
                if (displayName.contains(colorName)) {
                    plugin.getNicknameManager().setPlayerColor(player, colorName);
                    player.closeInventory();
                    player.sendMessage("§a✓ Цвет никнейма изменён на " + colorName + "§a!");
                    player.sendMessage("§7Теперь ваш никнейм красиво переливается!");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (title.contains(TITLE)) {
            cancelAnimation(event.getPlayer().getUniqueId());
        }
    }
}
