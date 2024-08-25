package magmaout.mappet.network.npc;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketNpcTool implements IMessage {
    public String npc = "";
    public String state = "";

    public PacketNpcTool() {
    }

    public PacketNpcTool(String npc, String state) {
        this.npc = npc;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.npc = ByteBufUtils.readUTF8String(buf);
        this.state = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.npc);
        ByteBufUtils.writeUTF8String(buf, this.state);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketNpcTool> {
        @Override
        public void run(EntityPlayerMP player, PacketNpcTool message) {
            ItemStack stack = player.getHeldItemMainhand();

            if (stack.getItem() == Mappet.npcTool) {
                NBTTagCompound tag = stack.getTagCompound();

                if (tag == null) {
                    tag = new NBTTagCompound();
                    stack.setTagCompound(tag);
                }

                if (message.npc.isEmpty()) {
                    tag.removeTag("Npc");
                } else {
                    tag.setString("Npc", message.npc);
                }

                if (message.state.isEmpty()) {
                    tag.removeTag("State");
                } else {
                    tag.setString("State", message.state);
                }
            }
        }
    }
}