package magmaout.mappet.client.gui.npc;

import magmaout.mappet.api.npcs.NpcState;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.npc.utils.GuiNpcDrops;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;

public class GuiNpcGeneralPanel extends GuiNpcPanel {
    public GuiButtonElement faction;
    public GuiNestedEdit morph;
    public GuiNpcDrops drops;
    public GuiTrackpadElement xp;
    public GuiToggleElement hasNoGravity;
    public GuiTrackpadElement shadowSize;

    public GuiNpcGeneralPanel(Minecraft mc) {
        super(mc);

        this.morph = new GuiNestedEdit(mc, this::openMorphMenu);
        this.drops = new GuiNpcDrops(mc);
        this.xp = new GuiTrackpadElement(mc, (v) -> this.state.xp.set(v.intValue()));
        this.hasNoGravity = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.general.has_no_gravity"), (b) -> this.state.hasNoGravity.set(b.isToggled()));
        this.shadowSize = new GuiTrackpadElement(mc, (v) -> this.state.shadowSize.set(v.floatValue()));

        this.xp.limit(0).integer();
        this.add(Elements.label(IKey.lang("mappet.gui.npcs.general.morph")).marginTop(12), this.morph);
        this.add(this.drops.marginTop(12));
        this.add(Elements.label(IKey.lang("mappet.gui.npcs.general.xp")).marginTop(12), this.xp);
        this.add(this.hasNoGravity.marginTop(12));
        this.add(Elements.label(IKey.lang("mappet.gui.npcs.general.shadow_size")).marginTop(12), this.shadowSize);
    }

    private void openMorphMenu(boolean editing) {
        GuiMappetDashboard.get(this.mc).openMorphMenu(this.morph.getParentContainer(), editing, this.state.morph, this::setMorph);
    }

    private void setMorph(AbstractMorph morph) {
        this.state.morph = MorphUtils.copy(morph);
        this.morph.setMorph(morph);
    }

    @Override
    public void set(NpcState state) {
        super.set(state);

        this.morph.setMorph(state.morph);
        this.drops.set(state.drops);
        this.xp.setValue(state.xp.get());
        this.hasNoGravity.toggled(state.hasNoGravity.get());
        this.shadowSize.setValue(state.shadowSize.get());
    }
}