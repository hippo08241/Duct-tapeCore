package com.hippo.ducttapecore.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class QualityToolsReforgingRecipe implements IRecipeWrapper {

    private final List<ItemStack> tools;
    private final List<ItemStack> materials;

    public QualityToolsReforgingRecipe(List<ItemStack> tools, List<ItemStack> materials) {
        this.tools = tools;
        this.materials = materials;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, Arrays.asList(tools, materials));
        ingredients.setOutputs(ItemStack.class, tools);
    }
}
