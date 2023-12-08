package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class HideCheck extends AbstractListener
{
    public HideCheck(FeelingLucky plugin)
    {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void checkForSneak(PlayerToggleSneakEvent event)
    {
        Player player = event.getPlayer();
        if (player.isSneaking())
            return;

        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getValue()) && doesQualify("hide_check", luck.getValue()))
        {
            player.getNearbyEntities(25, 25, 25)
                  .stream()
                  .filter(e -> e instanceof Monster)
                  .map(e -> (Monster) e)
                  .forEach(m ->
                           {
                               final LivingEntity target = m.getTarget();
                               if (target != null && target.getUniqueId().equals(player.getUniqueId()))
                               {
                                   m.setTarget(null);
                               }
                           });

            if (luck.isVerbose())
            {
                asAudience(player).sendMessage(MiniComponent.info("Your luck has hidden you from sight."));
            }
        }
    }
}
