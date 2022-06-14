package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IllOmen extends AbstractListener {
    public IllOmen(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void reconnectCheck(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PotionEffectType type = PotionEffectType.BAD_OMEN;
        Luck luck = getHandler().getLuckContainer(player);

        if (player.hasPotionEffect(type)) {
            luck.cache();
            double maths = luck.getValue() - (luck.getValue() * 0.25);
            luck.setValue(maths);
            player.sendMessage(MiniComponent.info("A -25% debuff has been applied to your luck from the Bad Omen status effect."));
        } else if (luck.cached(player) && !player.hasPotionEffect(type)) {
            luck.restore();
            player.sendMessage("The -25% debuff to your luck has been removed.");
        }
    }

    @EventHandler
    public void effectApplyCheck(EntityPotionEffectEvent event) {
        EntityPotionEffectEvent.Cause cause = EntityPotionEffectEvent.Cause.PATROL_CAPTAIN;
        EntityPotionEffectEvent.Action added = EntityPotionEffectEvent.Action.ADDED;
        EntityPotionEffectEvent.Action changed = EntityPotionEffectEvent.Action.CHANGED;

        if (event.getCause().equals(cause) && (event.getAction().equals(added) || event.getAction().equals(changed))) {
            if (event.getEntity() instanceof Player player) {
                Luck luck = plugin.getHandler().getLuckContainer(player);
                luck.cache();
                double maths = luck.getValue() - (luck.getValue() * 0.25);
                luck.setValue(maths);
                player.sendMessage(MiniComponent.warn("A -25% debuff has been applied to your luck from the Bad Omen status effect."));
            }
        }
    }

    @EventHandler
    public void effectRemoveCheck(EntityPotionEffectEvent event) {
        PotionEffect old = event.getOldEffect();
        EntityPotionEffectEvent.Action cleared = EntityPotionEffectEvent.Action.CLEARED;
        EntityPotionEffectEvent.Action removed = EntityPotionEffectEvent.Action.REMOVED;

        if (old == null) return;

        if (old.getType().equals(PotionEffectType.BAD_OMEN) && (event.getAction().equals(cleared) || event.getAction().equals(removed))) {
            if ((event.getEntity() instanceof Player player)) {
                Luck luck = plugin.getHandler().getLuckContainer(player);
                if (luck.cached(player)) {
                    luck.restore();
                    player.sendMessage("The -25% debuff to your luck has been removed.");
                }
            }
        }
    }

    @EventHandler
    public void disconnectCheck(PlayerQuitEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.BAD_OMEN)) {
            Luck luck = plugin.getHandler().getLuckContainer(event.getPlayer());
            if (luck.cached(event.getPlayer())) {
                luck.restore();
            }
        }
    }
}
