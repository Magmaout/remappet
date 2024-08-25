package magmaout.mappet.network.huds;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.api.huds.HUDScene;
import magmaout.mappet.client.RenderingHandler;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHUDScene implements IMessage {
    public String id = "";
    public NBTTagCompound tag;

    public PacketHUDScene() {
    }

    public PacketHUDScene(String id, NBTTagCompound tag) {
        this.id = id;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = ByteBufUtils.readUTF8String(buf);
        this.tag = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.id);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketHUDScene> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketHUDScene message) {
            if (message.tag == null) {
                if (message.id.isEmpty()) {
                    RenderingHandler.stage.scenes.clear();
                } else {
                    RenderingHandler.stage.scenes.remove(message.id);
                }
            } else {
                HUDScene scene = Mappet.huds.create(message.id, message.tag);

                RenderingHandler.stage.scenes.put(message.id, scene);
            }
        }
    }
}