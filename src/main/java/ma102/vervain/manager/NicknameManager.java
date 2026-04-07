package ma102.vervain.manager;

import ma102.vervain.UnicaleMechanics;
import ma102.vervain.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameManager {

    private final UnicaleMechanics plugin;
    private final Map<UUID, String> playerColors = new HashMap<>();
    private final Map<UUID, Integer> animationTicks = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public NicknameManager(UnicaleMechanics plugin) {
        this.plugin = plugin;
        loadData();
        startAnimationTask();
    }

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "nicknames.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Не удалось создать nicknames.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String colorName = dataConfig.getString(key);
                if (colorName != null) {
                    playerColors.put(uuid, colorName);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, String> entry : playerColors.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить данные никнеймов: " + e.getMessage());
        }
    }

    public void setPlayerColor(Player player, String colorName) {
        playerColors.put(player.getUniqueId(), colorName);
        dataConfig.set(player.getUniqueId().toString(), colorName);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить цвет никнейма: " + e.getMessage());
        }
        updatePlayerName(player);
    }

    public void removePlayerColor(Player player) {
        playerColors.remove(player.getUniqueId());
        dataConfig.set(player.getUniqueId().toString(), null);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось удалить цвет никнейма: " + e.getMessage());
        }
        updatePlayerName(player);
    }

    public String getPlayerColor(Player player) {
        return playerColors.get(player.getUniqueId());
    }

    public boolean hasColor(Player player) {
        return playerColors.containsKey(player.getUniqueId());
    }

    private void updatePlayerName(Player player) {
        String colorName = playerColors.get(player.getUniqueId());
        if (colorName != null && ColorUtils.GRADIENTS.containsKey(colorName)) {
            plugin.getServer().getOnlinePlayers().forEach(online -> {
                online.hidePlayer(plugin, player);
                online.showPlayer(plugin, player);
            });
        } else {
            Component name = Component.text(player.getName());
            player.playerListName(name);
            player.displayName(name);
        }
    }

    private void startAnimationTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, String> entry : playerColors.entrySet()) {
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    ColorUtils.GradientColor gradient = ColorUtils.GRADIENTS.get(entry.getValue());
                    if (gradient != null) {
                        int tick = animationTicks.getOrDefault(player.getUniqueId(), 0);
                        Component animatedName = gradient.getAnimatedComponent(player.getName(), tick);
                        player.playerListName(animatedName);
                        player.displayName(animatedName);
                        animationTicks.put(player.getUniqueId(), tick + 1);
                    }
                }
            }
        }, 0L, 1L);
    }

    public void onPlayerQuit(Player player) {
        animationTicks.remove(player.getUniqueId());
    }
}
