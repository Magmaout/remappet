package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.script.ScriptException;

public class PacketRepl implements IMessage {
    public String code;

    public PacketRepl() {
    }

    public PacketRepl(String code) {
        this.code = code;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.code = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.code);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketRepl> {
        @Override
        public void run(EntityPlayerMP player, PacketRepl message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            try {
                String output = Mappet.scripts.executeRepl(player, message.code);

                Dispatcher.sendTo(new PacketRepl(output), player);
            } catch (ScriptException e) {
                e.printStackTrace();
                Mappet.logger.error(e.getMessage());

                Dispatcher.sendTo(new PacketRepl(TextFormatting.RED + e.getMessage()), player);
            } catch (Exception e) {
                e.printStackTrace();
                Mappet.logger.error(e.getMessage());
            }
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketRepl> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketRepl message) {
            GuiMappetDashboard.get(Minecraft.getMinecraft()).script.repl.log(message.code);
        }
    }
}