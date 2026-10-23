package com.hippo.ducttapecore.config;

import com.hippo.ducttapecore.DuctTapeCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        Configuration config = ModConfig.getConfig();
        List<IConfigElement> elements = new ArrayList<>();
        for (String name : config.getCategoryNames()) {
            // "tweaks.restricted blocks" 같은 하위 카테고리는 상위 카테고리 안에 표시된다.
            if (!name.contains(".")) {
                elements.add(new ConfigElement(config.getCategory(name)));
            }
        }
        return new GuiConfig(parentScreen, elements, DuctTapeCore.MODID, false, false,
                GuiConfig.getAbridgedConfigPath(config.toString()));
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
