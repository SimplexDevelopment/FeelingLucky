package io.github.simplex.luck.listener;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemDrops implements Listener {
    private final Map<UUID, Player> entityPlayerMap = new HashMap<>();
    private final FeelingLucky plugin;

    public ItemDrops(FeelingLucky plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
            if (Luck.quickRNGnoMultiplier(33.0)) {
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
        Luck luck = plugin.getHandler().getLuckContainer(player);
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
}
