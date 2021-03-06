package io.github.simplex.luck.listener;

import io.github.simplex.lib.ItemBuilder;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.SpecialFootItem;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VillagerInventory extends AbstractListener {
    private final MerchantRecipe recipe;

    public VillagerInventory(FeelingLucky plugin) {
        super(plugin);

        SpecialFootItem foot = plugin.getFoot();
        this.recipe = new MerchantRecipe(foot.get(), 0, 2, true);

        recipe.setIngredients(Arrays.asList(
                ItemBuilder.of(Material.EMERALD).build(),
                ItemBuilder.of(Material.RABBIT_HIDE).build()
        ));
        recipe.setDemand(8);
        recipe.setPriceMultiplier(1.25F);
        recipe.setVillagerExperience(25);
        recipe.setSpecialPrice(4);

        register(this);
    }

    @EventHandler
    public void addRabbitFootToVillagerInventory(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Villager vil) {
            if (!vil.getProfession().equals(Villager.Profession.BUTCHER)) return;

            List<MerchantRecipe> recipeList = new ArrayList<>(vil.getRecipes());
            if (recipeList.contains(recipe)) return;

            Luck luck = plugin.getHandler().getLuckContainer(event.getPlayer());
            if (luck == null) return;

            if (luck.quickRNG(luck.getValue())) {
                recipeList.add(recipe);
                vil.setRecipes(recipeList);
            }
        }
    }
}
