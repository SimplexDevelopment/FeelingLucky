package io.github.simplex.luck.listener;

import io.github.simplex.lib.PotionEffectBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.ListBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record RestoreHunger(FeelingLucky plugin) implements Listener {
    public RestoreHunger {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void restoreHunger(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Luck luck = plugin.getHandler().getLuckContainer(event.getPlayer());
        PotionEffect effect = PotionEffectBuilder.newEffect().type(PotionEffectType.SATURATION).amplifier(2).duration(10).particles(false).create();
        if (luck.notDefault()) {
            double percentage = luck.getPercentage();
            ListBox.foods.forEach(food -> {
                if (item.isSimilar(food)) {
                    if (luck.quickRNG(percentage)) {
                        event.getPlayer().setExhaustion(event.getPlayer().getExhaustion() + 2);
                        event.getPlayer().addPotionEffect(effect);
                    }
                }
            });
        }
    }
}
