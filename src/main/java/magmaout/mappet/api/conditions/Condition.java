package magmaout.mappet.api.conditions;

import magmaout.mappet.CommonProxy;
import magmaout.mappet.api.conditions.blocks.AbstractConditionBlock;
import magmaout.mappet.api.utils.DataContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class Condition implements INBTSerializable<NBTTagCompound> {
    public final List<AbstractConditionBlock> blocks = new ArrayList<AbstractConditionBlock>();

    private boolean defaultValue;

    public Condition(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean execute(DataContext context) {
        if (this.blocks.isEmpty()) {
            return this.defaultValue;
        }

        boolean result = this.blocks.get(0).evaluate(context);

        for (int i = 1; i < this.blocks.size(); i++) {
            AbstractConditionBlock block = this.blocks.get(i);
            boolean value = block.evaluate(context);

            if (block.or) {
                result = result || value;
            } else {
                result = result && value;
            }
        }

        return result;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList blocks = new NBTTagList();

        for (AbstractConditionBlock block : this.blocks) {
            NBTTagCompound blockTag = block.serializeNBT();

            blockTag.setString("Type", CommonProxy.getConditionBlocks().getType(block));
            blocks.appendTag(blockTag);
        }

        if (blocks.tagCount() > 0) {
            tag.setTag("Blocks", blocks);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        NBTTagList blocks = tag.getTagList("Blocks", Constants.NBT.TAG_COMPOUND);

        this.blocks.clear();

        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound blockTag = blocks.getCompoundTagAt(i);
            AbstractConditionBlock block = CommonProxy.getConditionBlocks().create(blockTag.getString("Type"));

            if (block != null) {
                block.deserializeNBT(blockTag);
                this.blocks.add(block);
            }
        }
    }

    @Override
    public String toString() {
        String result = "mappet.condition[";
        for (AbstractConditionBlock block : this.blocks) {
            result += block.toString() + ",";
        }
        result += "]";
        return result;
    }
}