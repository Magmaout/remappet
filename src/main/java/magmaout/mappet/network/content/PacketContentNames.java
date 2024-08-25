package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.utils.IContentType;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.panels.GuiMappetDashboardPanel;
import magmaout.mappet.ClientProxy;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PacketContentNames extends PacketContentBase {
    public List<String> names = new ArrayList<String>();

    public PacketContentNames() {
        super();
    }

    public PacketContentNames(IContentType type, List<String> names) {
        super(type);

        this.names.addAll(names);
    }

    public PacketContentNames(IContentType type, List<String> names, int requestId) {
        super(type, requestId);

        this.names.addAll(names);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        for (int i = 0, c = buf.readInt(); i < c; i++) {
            this.names.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(this.names.size());

        for (String name : this.names) {
            ByteBufUtils.writeUTF8String(buf, name);
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketContentNames> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketContentNames message) {
            if (message.requestId >= 0) {
                ClientProxy.process(message.names, message.requestId);

                return;
            }

            GuiMappetDashboard dashboard = GuiMappetDashboard.get(Minecraft.getMinecraft());
            GuiMappetDashboardPanel panel = message.type.get(dashboard);

            if (panel != null) {
                panel.fillNames(message.names);
            }
        }
    }
}