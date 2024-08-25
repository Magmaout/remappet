package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.client.RenderingHandler;
import magmaout.mappet.client.morphs.WorldMorph;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketWorldMorph implements IMessage {
    public WorldMorph morph;

    public PacketWorldMorph() {
        this.morph = new WorldMorph();
    }

    public PacketWorldMorph(WorldMorph morph) {
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.morph.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.morph.toBytes(buf);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketWorldMorph> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketWorldMorph message) {
            if (message.morph.morph != null) {
                RenderingHandler.worldMorphs.add(message.morph);
            }
        }
    }
}
