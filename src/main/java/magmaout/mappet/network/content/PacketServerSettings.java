package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketServerSettings implements IMessage {
    public NBTTagCompound tag;

    public PacketServerSettings() {
    }

    public PacketServerSettings(NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tag = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketServerSettings> {
        @Override
        public void run(EntityPlayerMP player, PacketServerSettings message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            Mappet.settings.deserializeNBT(message.tag);
            Mappet.settings.save();
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketServerSettings> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketServerSettings message) {
            GuiMappetDashboard.get(Minecraft.getMinecraft()).settings.fill(message.tag);
        }
    }
}