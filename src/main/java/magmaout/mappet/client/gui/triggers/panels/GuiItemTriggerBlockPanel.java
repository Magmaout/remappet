package magmaout.mappet.client.gui.triggers.panels;

import magmaout.mappet.api.triggers.blocks.ItemTriggerBlock;
import magmaout.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import magmaout.mappet.client.gui.utils.GuiTargetElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiItemTriggerBlockPanel extends GuiAbstractTriggerBlockPanel<ItemTriggerBlock> {
    public GuiTargetElement target;
    public GuiCirculateElement mode;
    public GuiSlotElement slot;
    public GuiToggleElement ignoreNBT;

    public GuiItemTriggerBlockPanel(Minecraft mc, GuiTriggerOverlayPanel overlay, ItemTriggerBlock block) {
        super(mc, overlay, block);

        this.target = new GuiTargetElement(mc, null).skipGlobal();
        this.mode = new GuiCirculateElement(mc, this::toggleItemCheck);

        for (ItemTriggerBlock.ItemMode mode : ItemTriggerBlock.ItemMode.values()) {
            this.mode.addLabel(IKey.lang("mappet.gui.item_trigger.mode." + mode.name().toLowerCase()));
        }

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.block.stack = stack.copy());
        this.slot.marginTop(-2).marginBottom(-2);

        this.ignoreNBT = new GuiToggleElement(mc, IKey.lang("mappet.gui.item_trigger.ignoreNBT"), (b) -> this.block.ignoreNBT = b.isToggled());
        this.ignoreNBT.setVisible(this.block.mode == ItemTriggerBlock.ItemMode.TAKE);

        this.target.setTarget(block.target);
        this.mode.setValue(block.mode.ordinal());
        this.slot.setStack(block.stack);
        this.ignoreNBT.toggled(block.ignoreNBT);

        this.add(Elements.row(mc, 5, this.slot, this.mode));
        this.add(this.ignoreNBT.marginTop(12));
        this.add(this.target.marginTop(12));
    }

    private void toggleItemCheck(GuiCirculateElement b) {
        this.block.mode = ItemTriggerBlock.ItemMode.values()[b.getValue()];
        this.ignoreNBT.setVisible(this.block.mode == ItemTriggerBlock.ItemMode.TAKE);
    }
}