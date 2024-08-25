package magmaout.mappet.network.content;

import magmaout.mappet.api.utils.IContentType;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

public class PacketContentRequestNames extends PacketContentBase {
    public PacketContentRequestNames() {
        super();
    }

    public PacketContentRequestNames(IContentType type) {
        super(type);
    }

    public PacketContentRequestNames(IContentType type, int requestId) {
        super(type, requestId);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketContentRequestNames> {
        @Override
        public void run(EntityPlayerMP player, PacketContentRequestNames message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            List<String> names = new ArrayList<String>(message.type.getManager().getKeys());

            Dispatcher.sendTo(new PacketContentNames(message.type, names, message.requestId), player);
        }
    }
}