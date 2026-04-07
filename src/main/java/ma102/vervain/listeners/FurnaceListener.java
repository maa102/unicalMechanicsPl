package ma102.vervain.listeners;

import ma102.vervain.UnicaleMechanics;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceListener implements Listener {

    private final UnicaleMechanics plugin;

    public FurnaceListener(UnicaleMechanics plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        Material sourceType = source.getType();
        if (sourceType.name().endsWith("_CONCRETE_POWDER")) {
            String concreteName = sourceType.name().replace("_CONCRETE_POWDER", "_CONCRETE");
            Material concreteResult = Material.getMaterial(concreteName);
            if (concreteResult != null) {
                event.setResult(new ItemStack(concreteResult, source.getAmount()));
                plugin.getLogger().fine("Переплавлен " + sourceType.name() + " -> " + concreteResult.name());
            }
        }
    }
}
