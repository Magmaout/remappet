package magmaout.mappet.client.gui.huds;

import magmaout.mappet.api.huds.HUDMorph;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

public class GuiHUDMorphListElement extends GuiListElement<HUDMorph> {
    public GuiHUDMorphListElement(Minecraft mc, Consumer<List<HUDMorph>> callback) {
        super(mc, callback);
    }

    @Override
    protected String elementToString(HUDMorph element) {
        return element.morph.isEmpty() ? "-" : element.morph.get().getDisplayName();
    }
}