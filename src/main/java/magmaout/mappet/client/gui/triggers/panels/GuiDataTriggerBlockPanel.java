package magmaout.mappet.client.gui.triggers.panels;

import magmaout.mappet.api.triggers.blocks.DataTriggerBlock;
import magmaout.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import magmaout.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public abstract class GuiDataTriggerBlockPanel<T extends DataTriggerBlock> extends GuiStringTriggerBlockPanel<T> {
    public GuiTextElement data;

    public GuiDataTriggerBlockPanel(Minecraft mc, GuiTriggerOverlayPanel overlay, T block) {
        super(mc, overlay, block);

        this.data = GuiMappetUtils.fullWindowContext(
                new GuiTextElement(mc, 100000, (text) -> this.block.customData = text),
                IKey.lang("mappet.triggers.data")
        );
        this.data.tooltip(IKey.lang("mappet.gui.triggers.data_tooltip"));
        this.data.setText(block.customData);
    }

    public void addData() {
        this.add(Elements.label(IKey.lang("mappet.gui.triggers.data")).marginTop(12), this.data);
    }
}