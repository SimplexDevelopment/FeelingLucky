package io.github.simplex.luck.listener;

import io.github.simplex.lib.PotionEffectBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.ListBox;
import io.github.simplex.luck.player.Luck;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public record TakeDamage(FeelingLucky plugin) implements Listener {
    public TakeDamage {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void takeDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Luck luck = plugin.handler.getLuckContainer(player);
        if (ListBox.acceptedCauses.contains(event.getCause())) {
            if (luck.notDefault()) {
                double percentage = luck.getPercentage();

                /*
                 * If a player's luck stat is a negative number, or they are "marked",
                 * this will trigger instead of the regular luck spin.
                 */
                if (percentage < 0 || luck.isMarked(player)) {
                    percentage = Math.abs(percentage);
                    if (luck.quickRNG(percentage)) {
                        event.setCancelled(true);
                        player.damage(event.getDamage() * 2);
                        player.sendMessage(Component.empty().content("You were unlucky and took double damage."));
                    }
                    return;
                }

                if (luck.quickRNG(percentage)) {
                    event.setCancelled(true);
                    player.damage(event.getDamage() / 2);
                    player.sendMessage(Component.empty().content("You got lucky and took less damage."));
                }
            }
        }

        if (ListBox.sideCauses.contains(event.getCause())) {
            if (luck.notDefault()) {
                double percentage = luck.getPercentage();

                /*
                 * If a player's luck stat is a negative number, or they are "marked",
                 * this will trigger instead of the regular luck spin.
                 */
                if (percentage < 0 || luck.isMarked(player)) {
                    percentage = Math.abs(percentage);
                    if (luck.quickRNG(percentage)) {
                        event.setCancelled(true);
                        player.addPotionEffect(PotionEffectBuilder.newEffect().type(ListBox.potionEffects.get(Luck.RNG().nextInt(ListBox.potionEffects.size() - 1))).amplifier(Luck.RNG().nextInt(1, 5)).duration(Luck.RNG().nextInt(1, 120)).create());
                    }
                    return;
                }

                if (luck.quickRNG(percentage)) {
                    event.setCancelled(true);
                    player.getActivePotionEffects().removeIf(p -> ListBox.potionEffects.contains(p.getType()));
                    player.setFireTicks(0);
                    player.sendMessage(Component.empty().content("You got lucky and your afflictions were cured."));
                }
            }
        }
    }
}
