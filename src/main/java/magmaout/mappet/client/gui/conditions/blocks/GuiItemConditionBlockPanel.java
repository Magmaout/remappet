package magmaout.mappet.client.gui.conditions.blocks;

import magmaout.mappet.api.conditions.blocks.ItemConditionBlock;
import magmaout.mappet.client.gui.conditions.GuiConditionOverlayPanel;
import magmaout.mappet.client.gui.utils.GuiTargetElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiItemConditionBlockPanel extends GuiAbstractConditionBlockPanel<ItemConditionBlock> {
    public GuiTargetElement target;
    public GuiCirculateElement check;
    public GuiSlotElement slot;

    public GuiItemConditionBlockPanel(Minecraft mc, GuiConditionOverlayPanel overlay, ItemConditionBlock block) {
        super(mc, overlay, block);

        this.target = new GuiTargetElement(mc, block.target).skipGlobal();
        this.check = new GuiCirculateElement(mc, this::toggleItemCheck);

        for (ItemConditionBlock.ItemCheck check : ItemConditionBlock.ItemCheck.values()) {
            this.check.addLabel(IKey.lang("mappet.gui.conditions.item.types." + check.name().toLowerCase()));
        }

        this.check.setValue(block.check.ordinal());
        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.block.stack = stack.copy());
        this.slot.marginTop(-2).marginBottom(-2);
        this.slot.setStack(block.stack);

        this.add(Elements.row(mc, 5, this.slot, this.check));
        this.add(this.target.marginTop(12));
    }

    private void toggleItemCheck(GuiCirculateElement b) {
        this.block.check = ItemConditionBlock.ItemCheck.values()[b.getValue()];
    }
}