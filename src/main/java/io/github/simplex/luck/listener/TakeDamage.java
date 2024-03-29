package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.lib.PotionEffectBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.ListBox;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class TakeDamage extends AbstractListener {
    public TakeDamage(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void takeDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Luck luck = getHandler().getLuckContainer(player);
        if (ListBox.acceptedCauses.contains(event.getCause()) && (luck.notDefault())) {
                double percentage = luck.getValue();

                /*
                 * If a player's luck stat is a negative number, or they are "marked",
                 * this will trigger instead of the regular luck spin.
                 */
                if (percentage < 0 || luck.isMarked(player)) {
                    percentage = Math.abs(percentage);
                    if (luck.quickRNG(percentage)) {
                        event.setCancelled(true);
                        player.damage(event.getDamage() * 2);
                        asAudience(player).sendMessage(MiniComponent.warn("You were unlucky and took double damage!"));
                    }
                    return;
                }

                if (luck.quickRNG(percentage) && doesQualify("take_damage", percentage)) {
                    event.setCancelled(true);
                    player.damage(event.getDamage() / 2);
                }

        }

        if (ListBox.sideCauses.contains(event.getCause()) && (luck.notDefault())) {
                double percentage = luck.getValue();

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

                if (luck.quickRNG(percentage) && doesQualify("take_damage", percentage)) {
                    event.setCancelled(true);
                    player.getActivePotionEffects().removeIf(p -> ListBox.potionEffects.contains(p.getType()));
                    player.setFireTicks(0);
                    asAudience(player).sendMessage(MiniComponent.info("You got lucky and your afflictions were cured" +
                                                                      "."));
                }

        }
    }
}
