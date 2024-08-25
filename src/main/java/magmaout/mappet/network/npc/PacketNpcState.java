package magmaout.mappet.network.npc;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.npcs.NpcState;
import magmaout.mappet.client.gui.GuiNpcStateScreen;
import magmaout.mappet.entities.EntityNpc;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketNpcState implements IMessage {
    public int entityId;
    public NBTTagCompound state;

    public PacketNpcState() {
    }

    public PacketNpcState(int entityId, NBTTagCompound state) {
        this.entityId = entityId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.state = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeTag(buf, this.state);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketNpcState> {
        @Override
        public void run(EntityPlayerMP player, PacketNpcState message) {
            Entity npc = player.world.getEntityByID(message.entityId);

            if (npc instanceof EntityNpc) {
                NpcState state = new NpcState();

                state.deserializeNBT(message.state);
                ((EntityNpc) npc).setState(state, true);
            }
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketNpcState> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketNpcState message) {
            Minecraft mc = Minecraft.getMinecraft();
            NpcState state = new NpcState();

            state.deserializeNBT(message.state);
            mc.displayGuiScreen(new GuiNpcStateScreen(mc, message.entityId, state));
        }
    }
}