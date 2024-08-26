package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRefreshResources implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class ClientHandler extends ClientMessageHandler<PacketRefreshResources> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketRefreshResources message) {
            Minecraft.getMinecraft().refreshResources();
        }
    }
}