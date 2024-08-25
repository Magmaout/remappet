package magmaout.mappet.network.ui;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCloseUI implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class ClientHandlerCloseUI extends ClientMessageHandler<PacketCloseUI> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketCloseUI message) {
            Minecraft mc = Minecraft.getMinecraft();

            /* Just in case there will server side grievers who will spam the
             * close UI packets to some players. This check should at least allow
             * players to leave that server/world */
            if (!(mc.currentScreen instanceof GuiIngameMenu)) {
                mc.displayGuiScreen(null);
            }
        }
    }
}