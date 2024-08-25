package magmaout.mappet.network.ui;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.ui.UI;
import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.capabilities.character.ICharacter;
import magmaout.mappet.client.gui.GuiUserInterface;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUI implements IMessage {
    public UI ui;

    public PacketUI() {
    }

    public PacketUI(UI ui) {
        this.ui = ui;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.ui = new UI();
        this.ui.deserializeNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = this.ui == null ? new NBTTagCompound() : this.ui.serializeNBT();

        ByteBufUtils.writeTag(buf, tag);
    }

    public static class ServerHandlerUI extends ServerMessageHandler<PacketUI> {
        @Override
        public void run(EntityPlayerMP player, PacketUI message) {
            ICharacter character = Character.get(player);
            UIContext context = character.getUIContext();

            if (context != null && context.ui.getUIId().equals(message.ui.getUIId())) {
                context.close();
                character.setUIContext(null);
            }
        }
    }

    public static class ClientHandlerUI extends ClientMessageHandler<PacketUI> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketUI message) {
            Minecraft mc = Minecraft.getMinecraft();

            mc.displayGuiScreen(new GuiUserInterface(mc, message.ui));
        }
    }
}