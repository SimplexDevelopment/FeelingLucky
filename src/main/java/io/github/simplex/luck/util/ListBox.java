package io.github.simplex.luck.util;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListBox {
    public static final List<EntityDamageEvent.DamageCause> acceptedCauses = new ArrayList<>() {{
        add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        add(EntityDamageEvent.DamageCause.PROJECTILE);
        add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
        add(EntityDamageEvent.DamageCause.FLY_INTO_WALL);
        add(EntityDamageEvent.DamageCause.LIGHTNING);
        add(EntityDamageEvent.DamageCause.MAGIC);
    }};

    public static final List<EntityDamageEvent.DamageCause> sideCauses = new ArrayList<>() {{
        add(EntityDamageEvent.DamageCause.POISON);
        add(EntityDamageEvent.DamageCause.WITHER);
        add(EntityDamageEvent.DamageCause.FIRE_TICK);
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

    public static final List<PotionEffectType> positiveEffects = new ArrayList<>() {{
        add(PotionEffectType.DAMAGE_RESISTANCE);
        add(PotionEffectType.DOLPHINS_GRACE);
        add(PotionEffectType.INCREASE_DAMAGE);
        add(PotionEffectType.ABSORPTION);
        add(PotionEffectType.SATURATION);
        add(PotionEffectType.FIRE_RESISTANCE);
        add(PotionEffectType.WATER_BREATHING);
        add(PotionEffectType.SPEED);
        add(PotionEffectType.SLOW_FALLING);
        add(PotionEffectType.REGENERATION);
        add(PotionEffectType.NIGHT_VISION);
        add(PotionEffectType.LUCK);
        add(PotionEffectType.JUMP);
        add(PotionEffectType.INVISIBILITY);
        add(PotionEffectType.HEALTH_BOOST);
        add(PotionEffectType.FAST_DIGGING);
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
