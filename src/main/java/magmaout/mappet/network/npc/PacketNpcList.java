package magmaout.mappet.network.npc;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.api.npcs.Npc;
import magmaout.mappet.client.gui.GuiNpcToolScreen;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PacketNpcList implements IMessage {
    public List<String> npcs = new ArrayList<String>();
    public List<String> states = new ArrayList<String>();
    public boolean isStates;

    public PacketNpcList() {
    }

    public PacketNpcList(Collection<String> npcs, Collection<String> states) {
        this.npcs.addAll(npcs);
        this.states.addAll(states);
    }

    public PacketNpcList states() {
        this.isStates = true;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        for (int i = 0, c = buf.readInt(); i < c; i++) {
            this.npcs.add(ByteBufUtils.readUTF8String(buf));
        }

        for (int i = 0, c = buf.readInt(); i < c; i++) {
            this.states.add(ByteBufUtils.readUTF8String(buf));
        }

        this.isStates = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.npcs.size());

        for (String string : this.npcs) {
            ByteBufUtils.writeUTF8String(buf, string);
        }

        buf.writeInt(this.states.size());

        for (String string : this.states) {
            ByteBufUtils.writeUTF8String(buf, string);
        }

        buf.writeBoolean(this.isStates);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketNpcList> {
        @Override
        public void run(EntityPlayerMP player, PacketNpcList message) {
            if (message.npcs.isEmpty()) {
                return;
            }

            Npc npc = Mappet.npcs.load(message.npcs.get(0));

            if (npc != null) {
                Dispatcher.sendTo(new PacketNpcList(Collections.emptyList(), npc.states.keySet()).states(), player);
            }
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketNpcList> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketNpcList message) {
            Minecraft mc = Minecraft.getMinecraft();

            if (message.isStates) {
                if (mc.currentScreen instanceof GuiNpcToolScreen) {
                    GuiNpcToolScreen tool = (GuiNpcToolScreen) mc.currentScreen;

                    tool.states.clear();
                    tool.states.add(message.states);
                    tool.states.sort();
                }
            } else {
                mc.displayGuiScreen(new GuiNpcToolScreen(mc, message.npcs, message.states));
            }
        }
    }
}