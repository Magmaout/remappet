package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEntityRotations implements IMessage {
    public int entityId;
    public float yaw;
    public float yawHead;
    public float pitch;

    public PacketEntityRotations() {}

    public PacketEntityRotations(int entityId, float pitch, float yaw, float yawHead) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.yawHead = yawHead;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.yaw = buf.readFloat();
        this.yawHead = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.yawHead);
        buf.writeFloat(this.pitch);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketEntityRotations> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketEntityRotations message) {
            Entity entity = player.world.getEntityByID(message.entityId);

            if (entity != null) {
                entity.rotationPitch = entity.prevRotationPitch = message.pitch;
                entity.rotationYaw = entity.prevRotationYaw = message.yaw;
                entity.setRenderYawOffset(message.yawHead);
                entity.setRotationYawHead(message.yawHead);
            }
        }
    }
}