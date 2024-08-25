package magmaout.mappet.api.conditions.blocks;

import magmaout.mappet.api.conditions.Condition;
import magmaout.mappet.api.utils.DataContext;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConditionConditionBlock extends AbstractConditionBlock {
    public Condition condition = new Condition(false);

    @Override
    public boolean evaluateBlock(DataContext context) {
        return this.condition.execute(context);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String stringify() {
        return I18n.format("mappet.gui.conditions.condition.string", this.condition.blocks.size());
    }

    @Override
    public void serializeNBT(NBTTagCompound tag) {
        super.serializeNBT(tag);

        tag.setTag("Condition", this.condition.serializeNBT());
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        super.deserializeNBT(tag);

        this.condition.deserializeNBT(tag.getCompoundTag("Condition"));
    }
}