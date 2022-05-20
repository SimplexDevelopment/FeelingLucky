package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GiveDamage extends AbstractListener {
    public GiveDamage(FeelingLucky plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent e) {
        if ((e.getDamager() instanceof Player player)
                && (e.getEntity() instanceof LivingEntity)) {
            double nextDmg = e.getDamage() + Luck.RNG().nextDouble(1.0, 5.0);
            Luck luck = plugin.getHandler().getLuckContainer(player);
            if (luck.quickRNG(luck.getPercentage())) {
                e.setDamage(nextDmg);
                player.sendMessage(MiniComponent.info("Your luck has increased your damage output!"));
            }
        }
    }
}
