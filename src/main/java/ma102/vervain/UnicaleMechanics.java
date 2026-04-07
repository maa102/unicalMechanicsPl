package ma102.vervain;

import ma102.vervain.commands.ColorCommand;
import ma102.vervain.listeners.FurnaceListener;
import ma102.vervain.listeners.NameTagListener;
import ma102.vervain.manager.NicknameManager;
import ma102.vervain.menu.ColorMenu;
import org.bukkit.plugin.java.JavaPlugin;

public final class UnicaleMechanics extends JavaPlugin {

    private static UnicaleMechanics instance;
    private NicknameManager nicknameManager;
    private ColorMenu colorMenu;

    @Override
    public void onEnable() {
        instance = this;

        this.nicknameManager = new NicknameManager(this);
        this.colorMenu = new ColorMenu(this);

        getServer().getPluginManager().registerEvents(new FurnaceListener(this), this);
        getServer().getPluginManager().registerEvents(new NameTagListener(this), this);

        registerCommand("color", new ColorCommand(this, "color"));
        registerCommand("resetcolor", new ColorCommand(this, "resetcolor"));

        getLogger().info("§a╔════════════════════════════════════╗");
        getLogger().info("§a║       UnicaleMechanics v1.0       ║");
        getLogger().info("§a║          Author: MA102            ║");
        getLogger().info("§a╚════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (nicknameManager != null) {
            nicknameManager.saveAll();
        }
        getLogger().info("§cUnicaleMechanics выключен!");
    }

    public static UnicaleMechanics getInstance() {
        return instance;
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }

    public ColorMenu getColorMenu() {
        return colorMenu;
    }
}
