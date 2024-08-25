package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.api.states.States;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketStates implements IMessage {
    public String target;
    public NBTTagCompound states;

    public PacketStates() {
    }

    public PacketStates(String target, NBTTagCompound states) {
        this.target = target;
        this.states = states;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = ByteBufUtils.readUTF8String(buf);
        this.states = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.target);
        ByteBufUtils.writeTag(buf, this.states);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketStates> {
        public static States getStates(MinecraftServer server, String target) {
            if (target.equals("~")) {
                return Mappet.states;
            } else {
                EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(target);

                if (target != null) {
                    return Character.get(player).getStates();
                }
            }

            return null;
        }

        @Override
        public void run(EntityPlayerMP player, PacketStates message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            States states = getStates(player.world.getMinecraftServer(), message.target);

            if (states != null) {
                states.deserializeNBT(message.states);
            }
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketStates> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketStates message) {
            GuiMappetDashboard.get(Minecraft.getMinecraft()).settings.fillStates(message.target, message.states);
        }
    }
}