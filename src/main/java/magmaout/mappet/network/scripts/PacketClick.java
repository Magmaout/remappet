package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketClick implements IMessage {
    public EnumHand hand = EnumHand.MAIN_HAND;

    public PacketClick() {
    }

    public PacketClick(EnumHand hand) {
        this.hand = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.hand.ordinal());
    }

    public static class ServerHandler extends ServerMessageHandler<PacketClick> {
        @Override
        public void run(EntityPlayerMP player, PacketClick message) {
            if (message.hand == EnumHand.MAIN_HAND && !Mappet.settings.playerLeftClick.isEmpty()) {
                Mappet.settings.playerLeftClick.trigger(player);
            } else if (message.hand == EnumHand.OFF_HAND && !Mappet.settings.playerRightClick.isEmpty()) {
                Mappet.settings.playerRightClick.trigger(player);
            }
        }
    }
}
