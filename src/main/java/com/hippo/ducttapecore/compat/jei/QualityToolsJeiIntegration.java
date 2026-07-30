package com.hippo.ducttapecore.compat.jei;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import com.tmtravlr.qualitytools.config.ConfigLoader;
import com.tmtravlr.qualitytools.config.CustomMaterial;
import com.tmtravlr.qualitytools.config.QualityType;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IRecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class QualityToolsJeiIntegration {

    private static IJeiRuntime jeiRuntime;
    private static final List<QualityToolsReforgingRecipe> currentRecipes = new ArrayList<>();

    public static void setRuntime(IJeiRuntime runtime) {
        jeiRuntime = runtime;
        syncRecipes();
    }

    public static void syncRecipes() {
        if (jeiRuntime == null || !Loader.isModLoaded("qualitytools")) {
            return;
        }
        List<QualityToolsReforgingRecipe> recipes = ModConfig.qualityToolsJeiEnabled
                ? buildReforgingRecipes()
                : new ArrayList<>();
        Minecraft.getMinecraft().addScheduledTask(() -> applyRecipes(recipes));
    }

    private static void applyRecipes(List<QualityToolsReforgingRecipe> recipes) {
        IRecipeRegistry recipeRegistry = jeiRuntime.getRecipeRegistry();

        for (QualityToolsReforgingRecipe old : currentRecipes) {
            recipeRegistry.removeRecipe(old, QualityToolsReforgingCategory.UID);
        }
        currentRecipes.clear();

        for (QualityToolsReforgingRecipe recipe : recipes) {
            recipeRegistry.addRecipe(recipe, QualityToolsReforgingCategory.UID);
        }
        currentRecipes.addAll(recipes);

        DuctTapeCore.LOGGER.info("[DuctTapeCore] Quality Tools 리포징 JEI 레시피 {}개 등록", recipes.size());
    }

    private static List<QualityToolsReforgingRecipe> buildReforgingRecipes() {
        List<QualityToolsReforgingRecipe> recipes = new ArrayList<>();
        List<ItemStack> allItems = getAllRegisteredItems();
        List<ItemStack> eligibleTools = findQualityEligibleItems(allItems);

        if (ConfigLoader.universalReforgeItem != null) {
            List<ItemStack> materials = resolveMaterial(ConfigLoader.universalReforgeItem);
            if (!eligibleTools.isEmpty() && !materials.isEmpty()) {
                recipes.add(new QualityToolsReforgingRecipe(eligibleTools, materials));
            }
        }

        for (CustomMaterial toolType : ConfigLoader.customReforgeMaterials.keySet()) {
            List<ItemStack> tools = resolveMaterial(toolType);
            List<ItemStack> materials = new ArrayList<>();
            for (CustomMaterial materialType : ConfigLoader.customReforgeMaterials.get(toolType)) {
                materials.addAll(resolveMaterial(materialType));
            }
            if (!tools.isEmpty() && !materials.isEmpty()) {
                recipes.add(new QualityToolsReforgingRecipe(tools, materials));
            }
        }

        if (ConfigLoader.useRepairItem) {
            recipes.addAll(buildRepairItemRecipes(eligibleTools, allItems));
        }

        return recipes;
    }

    private static List<QualityToolsReforgingRecipe> buildRepairItemRecipes(List<ItemStack> eligibleTools, List<ItemStack> allItems) {
        List<QualityToolsReforgingRecipe> recipes = new ArrayList<>();

        for (ItemStack tool : eligibleTools) {
            List<ItemStack> materials = new ArrayList<>();
            for (ItemStack candidate : allItems) {
                if (tool.getItem().getIsRepairable(tool, candidate)) {
                    materials.add(candidate);
                }
            }
            if (!materials.isEmpty()) {
                recipes.add(new QualityToolsReforgingRecipe(java.util.Collections.singletonList(tool), materials));
            }
        }

        return recipes;
    }

    private static List<ItemStack> resolveMaterial(CustomMaterial material) {
        List<ItemStack> result = new ArrayList<>();
        if (material == null) {
            return result;
        }
        if (material.item != null) {
            int meta = material.meta == 32767 ? 0 : material.meta;
            result.add(new ItemStack(material.item, 1, meta));
        } else if (material.oreDict != null) {
            result.addAll(OreDictionary.getOres(material.oreDict));
        }
        return result;
    }

    private static List<ItemStack> getAllRegisteredItems() {
        List<ItemStack> result = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(item);
            if (!stack.isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }

    private static List<ItemStack> findQualityEligibleItems(List<ItemStack> allItems) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : allItems) {
            for (QualityType type : ConfigLoader.qualityTypes.values()) {
                if (type != null && type.itemMatches(stack)) {
                    result.add(stack);
                    break;
                }
            }
        }
        return result;
    }
}