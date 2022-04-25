package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.CooldownTimer;
import io.github.simplex.luck.util.SpecialFootItem;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class PlayerListener extends AbstractListener {
    private final CooldownTimer timer;

    public PlayerListener(FeelingLucky plugin) {
        super(plugin);
        this.timer = new CooldownTimer();
    }

    @EventHandler
    public void rabbitFoot(PlayerInteractEvent event) {
        Action action = event.getAction();
        ItemStack foot = new ItemStack(Material.RABBIT_FOOT);
        SpecialFootItem special = new SpecialFootItem();
        Player player = event.getPlayer();
        Luck luck = getHandler().getLuckContainer(player);

        if (timer.onCooldown(player)) {
            player.sendMessage(MiniComponent.err("That feature can only be used once every 30 seconds."));
            player.sendMessage(MiniComponent.info("You have " + timer.remaining(player) + " seconds remaining."));
            return;
        }

        if (action.isRightClick() && player.getInventory().getItemInMainHand().isSimilar(foot)) {
            if (foot.getItemMeta().equals(special.meta()) || foot.equals(special.get())) {
                luck.setMultiplier(luck.multiplier() + 1);
                player.sendMessage(MiniComponent.info("Your luck multiplier has increased by 1!"));
            }
            double rng = Luck.RNG().nextDouble(2.0, 5.0);
            player.getInventory().remove(player.getInventory().getItemInMainHand());
            luck.addTo(rng);
            plugin.getHandler().updatePlayer(player, luck);
            timer.setCooldown(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage(MiniComponent.info("Your luck has been increased by " + rng + " points."));
        }
    }

    @EventHandler
    public void luckDecrease(EntityDamageByEntityEvent event) {
        Entity eTEMP = event.getDamager();
        Entity pTEMP = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (!(pTEMP instanceof Player player)) {
            return;
        }

        if (!(eTEMP instanceof Witch) || !(eTEMP instanceof Guardian)) {
            return;
        }

        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (cause.equals(EntityDamageEvent.DamageCause.MAGIC) || cause.equals(EntityDamageEvent.DamageCause.POISON)) {
            if (luck.quickRNG(33.0)) {
                luck.takeFrom(5.0);
                plugin.getHandler().updatePlayer(player, luck);
                player.sendMessage(MiniComponent.warn("Your luck has been decreased by 5 points!"));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PlayerListener) obj;
        return Objects.equals(this.plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin);
    }

    @Override
    public String toString() {
        return "PlayerListener[" +
                "plugin=" + plugin + ']';
    }

}
