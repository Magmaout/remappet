package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.utils.IContentType;
import magmaout.mappet.api.utils.manager.IManager;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.panels.GuiMappetDashboardPanel;
import magmaout.mappet.utils.CurrentSession;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PacketContentData extends PacketContentBase {
    public String name = "";
    public String rename;
    public NBTTagCompound data;
    public boolean allowed = true;

    public PacketContentData() {
        super();
    }

    public PacketContentData(IContentType type, String name) {
        super(type);

        this.name = name;
    }

    public PacketContentData(IContentType type, String name, NBTTagCompound data) {
        this(type, name);

        this.data = data;
    }

    public PacketContentData rename(String rename) {
        this.rename = rename;

        return this;
    }

    public PacketContentData disallow() {
        this.allowed = false;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        this.name = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean()) {
            this.data = NBTUtils.readInfiniteTag(buf);
        }

        if (buf.readBoolean()) {
            this.rename = ByteBufUtils.readUTF8String(buf);
        }

        this.allowed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        ByteBufUtils.writeUTF8String(buf, this.name);

        buf.writeBoolean(this.data != null);

        if (this.data != null) {
            ByteBufUtils.writeTag(buf, this.data);
        }

        buf.writeBoolean(this.rename != null);

        if (this.rename != null) {
            ByteBufUtils.writeUTF8String(buf, this.rename);
        }

        buf.writeBoolean(this.allowed);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketContentData> {
        @Override
        public void run(EntityPlayerMP player, PacketContentData message) {
            boolean isEditing = !Character.get(player).getCurrentSession().isEditing(message.type, message.name);
            boolean exists = message.type.getManager().exists(message.name);

            if (!OpHelper.isPlayerOp(player) || (isEditing && exists)) {
                return;
            }

            IManager manager = message.type.getManager();

            if (message.rename != null) {
                manager.rename(message.name, message.rename);

                if (message.data != null) {
                    manager.save(message.rename, message.data);
                }
            } else if (message.data == null) {
                manager.delete(message.name);
            } else {
                manager.save(message.name, message.data);
            }

            if (!exists && manager.exists(message.name)) {
                CurrentSession session = Character.get(player).getCurrentSession();

                session.set(message.type, message.name);
                session.setActive(message.type, message.name);
            }

            /* Synchronize names to other players */
            List<String> names = new ArrayList<String>(message.type.getManager().getKeys());

            for (EntityPlayerMP otherPlayer : player.getServer().getPlayerList().getPlayers()) {
                if (otherPlayer == player) {
                    continue;
                }

                CurrentSession session = Character.get(otherPlayer).getCurrentSession();

                Dispatcher.sendTo(new PacketContentNames(message.type, names), otherPlayer);

                if (session.isActive(message.type, message.name)) {
                    Dispatcher.sendTo(message.disallow(), otherPlayer);
                }
            }
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketContentData> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketContentData message) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiMappetDashboard) {
                GuiMappetDashboard dashboard = (GuiMappetDashboard) screen;
                GuiMappetDashboardPanel panel = message.type.get(dashboard);

                if (panel != null) {
                    panel.fill(message.type.getManager().create(message.name, message.data), message.allowed);
                }
            }
        }
    }
}