package io.github.simplex.luck.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class JumpBoost extends AbstractListener {
    public JumpBoost(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void detectJumping(PlayerJumpEvent event) {
        Player player = event.getPlayer(); // Player is never null; they're in game and jumping.
        Luck luck = plugin.getHandler().getLuckContainer(player);
        Vector velocity = player.getVelocity().clone();

        if (luck.quickRNG(luck.getValue()) && doesQualify("jump_boost", luck.getValue())) {
            player.setVelocity(velocity.multiply(2.5));

            if (luck.isVerbose()) {
                asAudience(player).sendMessage(MiniComponent.info("Your luck gave you an extra boost to your jump!"));
            }
        }
    }
}
