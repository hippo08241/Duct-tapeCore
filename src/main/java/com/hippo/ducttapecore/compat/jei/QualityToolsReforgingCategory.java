package com.hippo.ducttapecore.compat.jei;

import com.tmtravlr.qualitytools.reforging.BlockReforgingStation;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class QualityToolsReforgingCategory extends BlankRecipeCategory<QualityToolsReforgingRecipe> {

    public static final String UID = "ducttapecore.qualitytools.reforging";

    private static final int WIDTH = 54;
    private static final int HEIGHT = 26;

    private static final int SLOT_TOOL = 0;
    private static final int SLOT_MATERIAL = 1;
    private static final int SLOT_OUTPUT = 2;

    private final IDrawable background;
    private final IDrawable icon;

    public QualityToolsReforgingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlockReforgingStation.ITEM_INSTANCE));
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return "Quality Tools 리포징";
    }

    @Override
    public String getModName() {
        return "Quality Tools";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, QualityToolsReforgingRecipe recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(SLOT_TOOL, true, 0, 4);
        itemStacks.init(SLOT_MATERIAL, true, 36, 4);
        itemStacks.init(SLOT_OUTPUT, false, 0, 4); // tool과 같은 자리 - R 조회용, 시각적으로는 겹쳐서 하나로 보임

        itemStacks.set(ingredients);
    }
}