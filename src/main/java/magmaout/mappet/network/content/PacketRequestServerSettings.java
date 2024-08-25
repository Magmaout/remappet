package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestServerSettings implements IMessage {
    public PacketRequestServerSettings() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class ServerHandler extends ServerMessageHandler<PacketRequestServerSettings> {
        @Override
        public void run(EntityPlayerMP player, PacketRequestServerSettings message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            Dispatcher.sendTo(new PacketServerSettings(Mappet.settings.serializeNBT()), player);
        }
    }
}