package io.github.simplex.lib;

import io.github.simplex.luck.player.Luck;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PotionEffectBuilder {

    @Contract("-> new")
    public static Worker newEffect() {
        return new Worker();
    }

    @Contract("-> new")
    public static PotionEffectType randomType() {
        List<NamespacedKey> namesList = new ArrayList<>();
        PotionEffectType[] types = PotionEffectType.values();
        Arrays.stream(types).forEach(type -> namesList.add(type.getKey()));
        int size = namesList.size();
        int next = Luck.RNG().nextInt(size - 1);
        NamespacedKey name = namesList.get(next);
        return PotionEffectType.getByKey(name);
    }

    public static class Worker {
        public PotionEffect potionEffect;

        public Worker() {
            potionEffect = new PotionEffect(PotionEffectType.ABSORPTION, 0, 0);
        }

        public Worker duration(int duration) {
            potionEffect = potionEffect.withDuration(duration);
            return this;
        }

        public Worker amplifier(int amplifier) {
            potionEffect = potionEffect.withAmplifier(amplifier);
            return this;
        }

        public Worker particles(boolean show) {
            potionEffect = potionEffect.withParticles(show);
            return this;
        }

        public Worker ambience(boolean ambient) {
            potionEffect = potionEffect.withAmbient(ambient);
            return this;
        }

        public Worker type(PotionEffectType type) {
            potionEffect = potionEffect.withType(type);
            return this;
        }

        public PotionEffect create() {
            return potionEffect;
        }
    }

}
