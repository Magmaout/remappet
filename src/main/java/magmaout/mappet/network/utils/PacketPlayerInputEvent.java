package magmaout.mappet.network.utils;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.CommonProxy;
import magmaout.mappet.utils.EventType;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayerInputEvent implements IMessage {
    NBTTagCompound data;
    EventType accessType;

    public PacketPlayerInputEvent() {
    }

    public PacketPlayerInputEvent(EventType accessType, NBTTagCompound data) {
        this.accessType = accessType;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.accessType = EventType.valueOf(ByteBufUtils.readUTF8String(buf));
        this.data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.accessType.toString());
        ByteBufUtils.writeTag(buf, this.data);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketPlayerInputEvent> {
        @Override
        public void run(EntityPlayerMP entityPlayerMP, PacketPlayerInputEvent packet) {
            NBTTagCompound data = packet.data;
            EventType accessType = packet.accessType;

            switch (accessType) {
                case MOUSE:
                    CommonProxy.playerInputEventHandler.onMouseEvent(data, entityPlayerMP);
                    break;
                case KEYBOARD:
                    CommonProxy.playerInputEventHandler.onKeyboardEvent(data, entityPlayerMP);
                    break;
            }
        }
    }
}
