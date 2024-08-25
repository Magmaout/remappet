package magmaout.mappet.network.ui;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.capabilities.character.ICharacter;
import magmaout.mappet.client.gui.GuiUserInterface;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUIData implements IMessage {
    public NBTTagCompound data;

    public PacketUIData() {
    }

    public PacketUIData(NBTTagCompound data) {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.data = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.data);
    }

    public static class ServerHandlerUIData extends ServerMessageHandler<PacketUIData> {
        @Override
        public void run(EntityPlayerMP player, PacketUIData message) {
            ICharacter character = Character.get(player);
            UIContext context = character.getUIContext();

            if (context == null) {
                return;
            }

            context.handleNewData(message.data);
        }
    }

    public static class ClientHandlerUIData extends ClientMessageHandler<PacketUIData> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketUIData message) {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.currentScreen instanceof GuiUserInterface) {
                GuiUserInterface screen = (GuiUserInterface) mc.currentScreen;

                screen.handleUIChanges(message.data);
            }
        }
    }
}