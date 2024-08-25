package magmaout.mappet.client.gui.triggers.panels;

import magmaout.mappet.api.triggers.blocks.SoundTriggerBlock;
import magmaout.mappet.api.utils.ContentType;
import magmaout.mappet.api.utils.TargetMode;
import magmaout.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import magmaout.mappet.client.gui.utils.GuiMappetUtils;
import magmaout.mappet.client.gui.utils.overlays.GuiOverlay;
import magmaout.mappet.client.gui.utils.overlays.GuiResourceLocationOverlayPanel;
import magmaout.mappet.client.gui.utils.overlays.GuiSoundOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiSoundTriggerBlockPanel extends GuiStringTriggerBlockPanel<SoundTriggerBlock> {
    public GuiCirculateElement target;

    public GuiSoundTriggerBlockPanel(Minecraft mc, GuiTriggerOverlayPanel overlay, SoundTriggerBlock block) {
        super(mc, overlay, block);

        this.target = GuiMappetUtils.createTargetCirculate(mc, block.target, (target) -> this.block.target = target);

        for (TargetMode target : TargetMode.values()) {
            if (!(target == TargetMode.PLAYER || target == TargetMode.GLOBAL)) {
                this.target.disable(target.ordinal());
            }
        }

        this.addPicker();
        this.add(Elements.label(IKey.lang("mappet.gui.conditions.target")).marginTop(12), this.target);
        this.addDelay();
    }

    @Override
    protected IKey getLabel() {
        return IKey.lang("mappet.gui.overlays.sounds.main");
    }

    @Override
    protected ContentType getType() {
        return null;
    }

    @Override
    protected void openOverlay() {
        GuiResourceLocationOverlayPanel overlay = new GuiSoundOverlayPanel(this.mc, this::setSound).set(this.block.string);

        GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.9F);
    }

    private void setSound(ResourceLocation location) {
        this.block.string = location == null ? "" : location.toString();
    }
}