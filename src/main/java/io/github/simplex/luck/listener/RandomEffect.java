package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.ListBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class RandomEffect extends AbstractListener {
    public RandomEffect(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void giveRandomEffect(PlayerRespawnEvent respawn) {
        Player player = respawn.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);

        List<PotionEffect> effectList = ListBox.positiveEffects.stream().map(m -> m.createEffect(120, 5)).toList();
        int size = effectList.size();
        PotionEffect random = effectList.get(Luck.RNG().nextInt(size - 1));

        if (luck.quickRNG(luck.getValue()) && doesQualify("random_effect", luck.getValue())) {
            player.addPotionEffect(random);
            player.sendMessage(MiniComponent.info("Thanks to luck, a random positive potion effect has been applied to you."));
        }
    }

    @EventHandler
    public void giveRandomEffect(PlayerTeleportEvent tp) {
        Player player = tp.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);

        List<PotionEffect> effectList = ListBox.positiveEffects.stream().map(m -> m.createEffect(120, 5)).toList();
        int size = effectList.size();
        PotionEffect random = effectList.get(Luck.RNG().nextInt(size - 1));

        if (luck.quickRNG(luck.getValue()) && doesQualify("random_effect", luck.getValue())) {
            player.addPotionEffect(random);
            player.sendMessage(MiniComponent.info("Thanks to luck, a random positive potion effect has been applied to you."));
        }
    }
}
