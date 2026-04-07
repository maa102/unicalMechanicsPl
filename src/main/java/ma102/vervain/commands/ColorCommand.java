package ma102.vervain.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import ma102.vervain.UnicaleMechanics;
import org.bukkit.entity.Player;

import java.util.List;

public class ColorCommand implements BasicCommand {

    private final UnicaleMechanics plugin;
    private final String commandName;

    public ColorCommand(UnicaleMechanics plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("§cЭта команда только для игроков!");
            return;
        }

        if (commandName.equalsIgnoreCase("color")) {
            plugin.getColorMenu().openMenu(player);
        } else if (commandName.equalsIgnoreCase("resetcolor")) {
            plugin.getNicknameManager().removePlayerColor(player);
            player.sendMessage("§a✓ Цвет никнейма сброшен!");
        }
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }
}
