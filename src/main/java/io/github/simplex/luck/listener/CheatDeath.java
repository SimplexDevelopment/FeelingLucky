package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public record CheatDeath(FeelingLucky plugin) implements Listener {
    public CheatDeath(FeelingLucky plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void cheatDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Luck luck = plugin.handler.getLuckContainer(player);
        double absorption = Math.round(Luck.RNG().nextDouble(5.0, 10.0));
        if (luck.quickRNG(luck.getPercentage())) {
            event.setCancelled(true);
            player.setHealth(1.0);
            player.setAbsorptionAmount(absorption);
            player.sendMessage(MiniComponent.of("You got lucky and cheated death!").send());
        }

    }
}
