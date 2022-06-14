package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;

public class HideCheck extends AbstractListener {
    public Map<Player, List<Entity>> entityMapList = new HashMap<>();

    public HideCheck(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void initPlayerMaps(PlayerJoinEvent event) {
        entityMapList.put(event.getPlayer(), List.of());
    }

    @EventHandler
    public void checkTargeting(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (event.getEntity() instanceof LivingEntity entity) {
                List<Entity> buffer = entityMapList.get(player).isEmpty() ?
                        new ArrayList<>() : entityMapList.get(player);
                buffer.add(entity);
                entityMapList.replace(player, buffer);
            }
        }
    }

    @EventHandler
    public void checkForSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getValue()) && doesQualify("hide_check", luck.getValue())) {
            entityMapList.get(player).forEach(e -> {
                e.getTrackedPlayers().remove(player);
            });
            player.sendMessage(MiniComponent.info("Your luck has hidden you from sight."));
        }
    }

    @EventHandler
    public void removePlayerOnLeave(PlayerQuitEvent event) {
        entityMapList.remove(event.getPlayer());
    }
}
