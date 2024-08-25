package magmaout.mappet.network.huds;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.huds.HUDMorph;
import magmaout.mappet.api.huds.HUDScene;
import magmaout.mappet.client.RenderingHandler;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHUDMorph implements IMessage {
    public String id = "";
    public int index;
    public NBTTagCompound morph;

    public PacketHUDMorph() {
    }

    public PacketHUDMorph(String id, int index, NBTTagCompound morph) {
        this.id = id;
        this.index = index;
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = ByteBufUtils.readUTF8String(buf);
        this.index = buf.readInt();
        this.morph = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.id);
        buf.writeInt(this.index);
        ByteBufUtils.writeTag(buf, this.morph);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketHUDMorph> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketHUDMorph message) {
            HUDScene scene = RenderingHandler.stage.scenes.get(message.id);

            if (scene != null && message.index >= 0 && message.index < scene.morphs.size()) {
                HUDMorph morph = scene.morphs.get(message.index);

                morph.morph.set(MorphManager.INSTANCE.morphFromNBT(message.morph));
            }
        }
    }
}