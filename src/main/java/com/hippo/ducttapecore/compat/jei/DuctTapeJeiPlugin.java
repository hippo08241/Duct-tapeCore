package com.hippo.ducttapecore.compat.jei;

import com.tmtravlr.qualitytools.reforging.BlockReforgingStation;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

@JEIPlugin
public class DuctTapeJeiPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    @Override
    public void register(IModRegistry registry) {
        if (!Loader.isModLoaded("qualitytools")) {
            return;
        }

        registry.addRecipeCategories(new QualityToolsReforgingCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCatalyst(new ItemStack(BlockReforgingStation.ITEM_INSTANCE), QualityToolsReforgingCategory.UID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        if (!Loader.isModLoaded("qualitytools")) {
            return;
        }
        QualityToolsJeiIntegration.setRuntime(jeiRuntime);
    }
}