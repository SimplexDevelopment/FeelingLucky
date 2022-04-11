package io.github.simplex.luck.listener;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.SpecialFootItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public record PlayerListener(FeelingLucky plugin) implements Listener {
    public PlayerListener(FeelingLucky plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void rabbitFoot(PlayerInteractEvent event) {
        Action action = event.getAction();
        ItemStack foot = new ItemStack(Material.RABBIT_FOOT);
        SpecialFootItem special = new SpecialFootItem();
        Player player = event.getPlayer();
        Luck luck = plugin.handler.getLuckContainer(player);
        if (action.isRightClick() && player.getInventory().getItemInMainHand().isSimilar(foot)) {
            if (foot.getItemMeta().equals(special.meta()) || foot.equals(special.get())) {
                luck.setMultiplier(luck.multiplier() + 1);
            }
            double rng = Luck.RNG().nextDouble(2.0, 5.0);
            player.getInventory().remove(player.getInventory().getItemInMainHand());
            luck.addTo(rng);
            plugin.handler.updatePlayer(player, luck);
            player.sendMessage(Component.empty().content("Your luck has been increased by " + rng + " points."));
        }
    }

    @EventHandler
    public void witchesBrew(EntityDamageByEntityEvent event) {
        Entity eTEMP = event.getDamager();
        Entity pTEMP = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (!(pTEMP instanceof Player player)) {
            return;
        }

        if (!(eTEMP instanceof Witch)) {
            return;
        }

        Luck luck = plugin.handler.getLuckContainer(player);
        if (cause.equals(EntityDamageEvent.DamageCause.MAGIC) || cause.equals(EntityDamageEvent.DamageCause.POISON)) {
            if (luck.quickRNG(33.0)) {
                luck.takeFrom(5.0);
                plugin.handler.updatePlayer(player, luck);
            }
        }
    }
}
