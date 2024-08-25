package magmaout.mappet.api.ui.utils;

import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.api.ui.components.UIComponent;
import magmaout.mappet.api.ui.components.UIParentComponent;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UIRootComponent extends UIParentComponent {
    @Override
    @SideOnly(Side.CLIENT)
    public GuiElement create(Minecraft mc, UIContext context) {
        GuiElement element = new GuiElement(mc);

        for (UIComponent component : this.getChildComponents()) {
            GuiElement created = component.create(mc, context);

            created.flex().relative(element);
            element.add(created);
        }

        return this.applyKeybinds(element, context);
    }
}