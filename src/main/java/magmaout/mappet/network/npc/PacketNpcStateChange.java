package magmaout.mappet.network.npc;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.npcs.NpcState;
import magmaout.mappet.utils.NpcStateUtils;
import magmaout.mappet.entities.EntityNpc;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//This used to be `PacketNpcMorph`, but I (Otaku) changed it to `PacketNpcStateChange` because it's more accurate.
public class PacketNpcStateChange implements IMessage {
    public int id;
    public NpcState state;

    public PacketNpcStateChange() {
    }

    public PacketNpcStateChange(EntityNpc npc) {
        this(npc.getEntityId(), npc.getState());
    }

    public PacketNpcStateChange(int id, NpcState state) {
        this.id = id;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
        this.state = NpcStateUtils.stateFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.id);
        NpcStateUtils.stateToBuf(buf, this.state);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketNpcStateChange> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketNpcStateChange message) {
            Entity entity = player.world.getEntityByID(message.id);

            if (entity instanceof EntityNpc) {
                ((EntityNpc) entity).setState(message.state, true);
            }
        }
    }
}