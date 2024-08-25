package magmaout.mappet.api.triggers.blocks;

import magmaout.mappet.api.utils.TargetMode;
import magmaout.mappet.utils.EnumUtils;
import magmaout.mappet.utils.InventoryUtils;
import magmaout.mappet.api.utils.DataContext;
import magmaout.mappet.api.utils.Target;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTriggerBlock extends AbstractTriggerBlock {
    public Target target = new Target(TargetMode.SUBJECT);
    public ItemStack stack = ItemStack.EMPTY;
    public ItemMode mode = ItemMode.TAKE;
    public boolean ignoreNBT = true;

    @Override
    @SideOnly(Side.CLIENT)
    public String stringify() {
        String displayName = this.stack.getDisplayName();

        if (this.stack.getCount() > 1) {
            displayName += TextFormatting.GOLD + " (" + TextFormatting.GRAY + this.stack.getCount() + TextFormatting.GOLD + ")";
        }


        if (this.mode == ItemMode.GIVE) {
            return I18n.format("mappet.gui.nodes.item.give", displayName);
        }

        if (!this.ignoreNBT) {
            displayName += String.valueOf(TextFormatting.DARK_PURPLE) + TextFormatting.ITALIC + " (+NBT)";
        }

        return I18n.format("mappet.gui.nodes.item.take", displayName);
    }

    @Override
    public void trigger(DataContext context) {
        EntityPlayer player;

        if (this.stack.isEmpty() || (player = this.target.getPlayer(context)) == null) {
            context.cancel();

            return;
        }

        /* Give the item stack to player */
        if (this.mode == ItemMode.GIVE) {
            ItemStack copy = this.stack.copy();

            if (!player.addItemStackToInventory(copy) && !copy.isEmpty()) {
                player.dropItem(copy, false);
            }

            return;
        }

        if (InventoryUtils.countItems(player, this.stack, true, this.ignoreNBT) >= this.stack.getCount()) {
            NBTTagCompound nbt = this.ignoreNBT ? null : this.stack.getTagCompound();
            player.inventory.clearMatchingItems(this.stack.getItem(), -1, this.stack.getCount(), nbt);
        } else {
            context.cancel();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    @Override
    protected void serializeNBT(NBTTagCompound tag) {
        super.serializeNBT(tag);

        tag.setTag("Target", this.target.serializeNBT());
        tag.setTag("Stack", this.stack.serializeNBT());
        tag.setInteger("Mode", this.mode.ordinal());
        tag.setBoolean("IgnoreNBT", this.ignoreNBT);
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        super.deserializeNBT(tag);

        if (tag.hasKey("Target")) {
            this.target.deserializeNBT(tag.getCompoundTag("Target"));
        }

        if (tag.hasKey("Stack")) {
            this.stack = new ItemStack(tag.getCompoundTag("Stack"));
        }

        if (tag.hasKey("Mode")) {
            this.mode = EnumUtils.getValue(tag.getInteger("Mode"), ItemMode.values(), ItemMode.TAKE);
        }

        if (tag.hasKey("IgnoreNBT")) {
            this.ignoreNBT = tag.getBoolean("IgnoreNBT");
        }
    }

    public static enum ItemMode {
        TAKE, GIVE
    }
}