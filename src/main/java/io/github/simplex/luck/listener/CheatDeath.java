package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class CheatDeath extends AbstractListener
{
    public CheatDeath(FeelingLucky plugin)
    {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void cheatDeath(PlayerDeathEvent event)
    {
        Player player = event.getPlayer();
        Luck luck = getHandler().getLuckContainer(player);
        double absorption = Math.round(Luck.RNG().nextDouble(5.0, 10.0));
        if (luck.quickRNG(luck.getValue()) && doesQualify("cheat_death", luck.getValue()))
        {
            event.setCancelled(true);
            player.setHealth(1.0);
            player.setAbsorptionAmount(absorption);

            if (luck.isVerbose())
            {
                asAudience(player).sendMessage(MiniComponent.info("You got lucky and cheated death!"));
            }
        }
    }
}
