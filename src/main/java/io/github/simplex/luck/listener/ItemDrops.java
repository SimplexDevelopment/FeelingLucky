package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDrops extends AbstractListener
{
    private final Map<UUID, Player> entityPlayerMap = new HashMap<>();
    private boolean canAffect = false;

    public ItemDrops(FeelingLucky plugin)
    {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void checkForPreItemDrop(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity entity))
        {
            return;
        }

        if (!(event.getDamager() instanceof Player player))
        {
            return;
        }

        if (entity.getHealth() > 0.0)
        {
            return;
        }

        entityPlayerMap.put(entity.getUniqueId(), player);
    }

    @EventHandler
    public void checkForDroppedItems(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            canAffect = false;
            return;
        }

        canAffect = event.getDrops().isEmpty();
    }

    @EventHandler
    public void itemDrops(EntityDropItemEvent event)
    {
        Entity entity = event.getEntity();

        if (entityPlayerMap.get(entity.getUniqueId()) == null)
            return;

        if (!canAffect)
            return;

        Player player = entityPlayerMap.get(entity.getUniqueId());
        Luck luck = getHandler().getLuckContainer(player);
        Item item = event.getItemDrop();
        ItemStack stack = item.getItemStack();
        int amount = stack.getAmount();
        if (luck.quickRNG(luck.getValue()) && doesQualify("item_drops", luck.getValue()))
        {
            int rng = Luck.RNG().nextInt(2, 5);
            amount += rng;
            stack.setAmount(amount);
            event.getItemDrop().setItemStack(stack);

            if (luck.isVerbose())
            {
                asAudience(player).sendMessage(MiniComponent.info("Your luck earned you some extra loot!"));
            }
        }
    }
}
