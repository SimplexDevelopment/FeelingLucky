package io.github.simplex.luck.listener;

import io.github.simplex.lib.PotionEffectBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.player.PlayerHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public record PlayerListener(FeelingLucky plugin) implements Listener {

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
                        player.addPotionEffect(PotionEffectBuilder.newEffect()
                                .type(ListBox.potionEffects.get(Luck.RNG().nextInt(ListBox.potionEffects.size() - 1)))
                                .amplifier(Luck.RNG().nextInt(1, 5))
                                .duration(Luck.RNG().nextInt(1, 120))
                                .create());
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
    public void restoreHunger(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Luck luck = PlayerHandler.getLuckContainer(event.getPlayer());
        if (luck.notDefault()) {
            double percentage = luck.getPercentage();
            ListBox.foods.forEach(food -> {
                if (item.isSimilar(food)) {
                    if (luck.quickRNG(percentage)) {
                        event.getPlayer().setExhaustion(event.getPlayer().getExhaustion() + 2);
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
        DamageCause cause = event.getCause();

        if (!(pTEMP instanceof Player player)) {
            return;
        }

        if (!(eTEMP instanceof Witch)) {
            return;
        }

        Luck luck = PlayerHandler.getLuckContainer(player);
        if (cause.equals(DamageCause.MAGIC) || cause.equals(DamageCause.POISON)) {
            if (luck.quickRNG(33.0)) {
                luck.takeFrom(5.0);
                plugin.handler.updatePlayer(player, luck);
            }
        }
    }

    private static class ListBox {
        public static final List<DamageCause> acceptedCauses = new ArrayList<>() {{
            add(DamageCause.ENTITY_ATTACK);
            add(DamageCause.ENTITY_SWEEP_ATTACK);
            add(DamageCause.PROJECTILE);
            add(DamageCause.ENTITY_EXPLOSION);
            add(DamageCause.FLY_INTO_WALL);
            add(DamageCause.LIGHTNING);
            add(DamageCause.MAGIC);
        }};

        public static final List<DamageCause> sideCauses = new ArrayList<>() {{
            add(DamageCause.POISON);
            add(DamageCause.WITHER);
            add(DamageCause.FIRE_TICK);
        }};

        public static final List<PotionEffectType> potionEffects = new ArrayList<>() {{
            add(PotionEffectType.POISON);
            add(PotionEffectType.WITHER);
            add(PotionEffectType.BLINDNESS);
            add(PotionEffectType.SLOW);
            add(PotionEffectType.SLOW_DIGGING);
            add(PotionEffectType.BAD_OMEN);
            add(PotionEffectType.CONFUSION);
            add(PotionEffectType.WEAKNESS);
        }};

        public static final List<ItemStack> foods = new ArrayList<>() {{
            add(new ItemStack(Material.COOKED_BEEF));
            add(new ItemStack(Material.COOKED_CHICKEN));
            add(new ItemStack(Material.COOKED_PORKCHOP));
            add(new ItemStack(Material.COOKED_COD));
            add(new ItemStack(Material.COOKED_MUTTON));
            add(new ItemStack(Material.COOKED_RABBIT));
            add(new ItemStack(Material.COOKED_SALMON));
            add(new ItemStack(Material.BEETROOT_SOUP));
            add(new ItemStack(Material.POTATO));
            add(new ItemStack(Material.BAKED_POTATO));
            add(new ItemStack(Material.CARROT));
            add(new ItemStack(Material.GOLDEN_CARROT));
            add(new ItemStack(Material.APPLE));
            add(new ItemStack(Material.GOLDEN_APPLE));
            add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
            add(new ItemStack(Material.BEEF));
            add(new ItemStack(Material.PORKCHOP));
            add(new ItemStack(Material.CHICKEN));
            add(new ItemStack(Material.COD));
            add(new ItemStack(Material.SALMON));
            add(new ItemStack(Material.MUTTON));
            add(new ItemStack(Material.RABBIT));
            add(new ItemStack(Material.MUSHROOM_STEW));
            add(new ItemStack(Material.BREAD));
            add(new ItemStack(Material.CAKE));
            add(new ItemStack(Material.COOKIE));
        }};
    }
}
