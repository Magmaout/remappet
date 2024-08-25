package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.utils.CurrentSession;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketContentExit implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class ServerHandler extends ServerMessageHandler<PacketContentExit> {
        public static void syncData(EntityPlayerMP except) {
            /* Before clearing current session, update the data for players that
             * are browsing this data */
            CurrentSession session = Character.get(except).getCurrentSession();
            NBTTagCompound data = null;
            int i = 0;

            if (session.type == null) {
                return;
            }

            for (EntityPlayerMP player : except.getServer().getPlayerList().getPlayers()) {
                if (player == except) {
                    continue;
                }

                CurrentSession otherSession = Character.get(player).getCurrentSession();

                if (otherSession.isActive(session.type, session.id)) {
                    if (data == null) {
                        data = session.type.getManager().load(session.id).serializeNBT();
                    }

                    PacketContentData packet = new PacketContentData(session.type, session.id, data);

                    if (i > 0) {
                        packet.disallow();
                    }

                    Dispatcher.sendTo(packet, player);

                    /* The first dog, gets the data editing privilege */
                    if (i == 0) {
                        otherSession.set(session.type, session.id);
                    }

                    i += 1;
                }
            }
        }

        @Override
        public void run(EntityPlayerMP player, PacketContentExit message) {
            syncData(player);

            /* Clear current session */
            Character.get(player).getCurrentSession().reset();
        }
    }
}