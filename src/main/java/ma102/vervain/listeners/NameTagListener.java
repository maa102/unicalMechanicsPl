package ma102.vervain.listeners;

import ma102.vervain.UnicaleMechanics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class NameTagListener implements Listener {

    private final UnicaleMechanics plugin;

    public NameTagListener(UnicaleMechanics plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getNicknameManager().onPlayerQuit(event.getPlayer());
    }
}
