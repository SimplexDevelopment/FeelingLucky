package io.github.simplex.luck.listener;

import io.github.simplex.lib.PotionEffectBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.ListBox;
import io.github.simplex.luck.SneakyWorker;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.player.PlayerHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PlayerListener(FeelingLucky plugin) implements Listener {
    private static final Map<UUID, Player> entityPlayerMap = new HashMap<>();

    public PlayerListener {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void takeDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Luck luck = PlayerHandler.getLuckContainer(player);
        if (ListBox.acceptedCauses.contains(event.getCause())) {
            if (luck.notDefault()) {
                double percentage = luck.getPercentage();

                /*
                 * If a player's luck stat is a negative number, or they are "marked",
                 * this will trigger instead of the regular luck spin.
                 */
                if (percentage < 0 || PlayerHandler.isMarked(player)) {
                    percentage = Math.abs(percentage);
                    if (luck.quickRNG(percentage)) {
                        event.setCancelled(true);
                        player.damage(event.getDamage() * 2);
                        player.sendMessage(Component.empty().content("You were unlucky and took double damage."));
                    }
                    return;
                }

                if (luck.quickRNG(percentage)) {
                    event.setCancelled(true);
                    player.damage(event.getDamage() / 2);
                    player.sendMessage(Component.empty().content("You got lucky and took less damage."));
                }
            }
        }

        if (ListBox.sideCauses.contains(event.getCause())) {
            if (luck.notDefault()) {
                double percentage = luck.getPercentage();

                /*
                 * If a player's luck stat is a negative number, or they are "marked",
                 * this will trigger instead of the regular luck spin.
                 */
                if (percentage < 0 || PlayerHandler.isMarked(player)) {
                    percentage = Math.abs(percentage);
                    if (luck.quickRNG(percentage)) {
                        event.setCancelled(true);
                        player.addPotionEffect(PotionEffectBuilder.newEffect().type(ListBox.potionEffects.get(Luck.RNG().nextInt(ListBox.potionEffects.size() - 1))).amplifier(Luck.RNG().nextInt(1, 5)).duration(Luck.RNG().nextInt(1, 120)).create());
                    }
                    return;
                }

                if (luck.quickRNG(percentage)) {
                    event.setCancelled(true);
                    player.getActivePotionEffects().removeIf(p -> ListBox.potionEffects.contains(p.getType()));
                    player.setFireTicks(0);
                    player.sendMessage(Component.empty().content("You got lucky and your afflictions were cured."));
                }
            }
        }
    }

    @EventHandler
    public void extraBlockDrops(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        Luck luck = PlayerHandler.getLuckContainer(player);
        List<Item> items = event.getItems();
        if (luck.quickRNG(luck.getPercentage())) {
            items.forEach(SneakyWorker::move);
        }
    }

    @EventHandler
    public void checkForPreItemDrop(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(entity.getHealth() <= 0.0)) {
            return;
        }

        if (entity instanceof Witch witch) {
            if (Luck.quickRNG2(33.0)) {
                Location location = witch.getLocation();
                World world = location.getWorld();
                Item item = world.dropItemNaturally(location, new ItemStack(Material.RABBIT_FOOT, 1));
                new EntityDropItemEvent(witch, item).callEvent();
            }
        }

        entityPlayerMap.put(entity.getUniqueId(), player);
    }

    @EventHandler
    public void itemDrops(EntityDropItemEvent event) {
        Entity entity = event.getEntity();

        if (entityPlayerMap.get(entity.getUniqueId()) == null) return;

        Player player = entityPlayerMap.get(entity.getUniqueId());
        Luck luck = PlayerHandler.getLuckContainer(player);
        Item item = event.getItemDrop();
        ItemStack stack = item.getItemStack();
        int amount = stack.getAmount();
        if (luck.quickRNG(luck.getPercentage())) {
            int rng = Luck.RNG().nextInt(2, 5);
            amount += rng;
            stack.setAmount(amount);
            event.getItemDrop().setItemStack(stack);
        }
    }

    @EventHandler
    public void restoreHunger(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Luck luck = PlayerHandler.getLuckContainer(event.getPlayer());
        PotionEffect effect = PotionEffectBuilder.newEffect().type(PotionEffectType.SATURATION).amplifier(2).duration(10).particles(false).create();
        if (luck.notDefault()) {
            double percentage = luck.getPercentage();
            ListBox.foods.forEach(food -> {
                if (item.isSimilar(food)) {
                    if (luck.quickRNG(percentage)) {
                        event.getPlayer().setExhaustion(event.getPlayer().getExhaustion() + 2);
                        event.getPlayer().addPotionEffect(effect);
                    }
                }
            });
        }
    }

    @EventHandler
    public void rabbitFoot(PlayerInteractEvent event) {
        Action action = event.getAction();
        ItemStack foot = new ItemStack(Material.RABBIT_FOOT);
        Player player = event.getPlayer();
        Luck luck = PlayerHandler.getLuckContainer(player);
        if (action.isRightClick() && player.getInventory().getItemInMainHand().isSimilar(foot)) {
            double rng = Luck.RNG().nextDouble(2.0, 5.0);
            player.getInventory().remove(player.getInventory().getItemInMainHand());
            luck.addTo(rng);

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

        Luck luck = PlayerHandler.getLuckContainer(player);
        if (cause.equals(EntityDamageEvent.DamageCause.MAGIC) || cause.equals(EntityDamageEvent.DamageCause.POISON)) {
            if (luck.quickRNG(33.0)) {
                luck.takeFrom(5.0);
                PlayerHandler.updatePlayer(player, luck);
            }
        }
    }
}
