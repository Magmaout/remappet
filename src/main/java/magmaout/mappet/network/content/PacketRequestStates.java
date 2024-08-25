package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.states.States;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestStates implements IMessage {
    public String target;

    public PacketRequestStates() {
    }

    public PacketRequestStates(String target) {
        this.target = target;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.target);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketRequestStates> {
        @Override
        public void run(EntityPlayerMP player, PacketRequestStates message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            States states = PacketStates.ServerHandler.getStates(player.world.getMinecraftServer(), message.target);

            if (states != null) {
                Dispatcher.sendTo(new PacketStates(message.target, states.serializeNBT()), player);
            }
        }
    }
}