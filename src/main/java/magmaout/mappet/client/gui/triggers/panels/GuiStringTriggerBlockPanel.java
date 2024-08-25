package magmaout.mappet.client.gui.triggers.panels;

import magmaout.mappet.api.triggers.blocks.StringTriggerBlock;
import magmaout.mappet.api.utils.ContentType;
import magmaout.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import magmaout.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public abstract class GuiStringTriggerBlockPanel<T extends StringTriggerBlock> extends GuiAbstractTriggerBlockPanel<T> {
    public GuiButtonElement picker;

    public GuiStringTriggerBlockPanel(Minecraft mc, GuiTriggerOverlayPanel overlay, T block) {
        super(mc, overlay, block);

        this.picker = new GuiButtonElement(mc, this.getLabel(), (b) -> this.openOverlay());
    }

    public void addPicker() {
        this.add(this.picker);
    }

    protected abstract IKey getLabel();

    protected abstract ContentType getType();

    protected void openOverlay() {
        GuiMappetUtils.openPicker(this.getType(), this.block.string, this::setString);
    }

    private void setString(String string) {
        this.block.string = string;
    }
}