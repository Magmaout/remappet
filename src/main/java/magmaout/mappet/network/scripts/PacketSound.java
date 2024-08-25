package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSound implements IMessage {
    public String sound = "";
    public String soundCategory = "";
    public float volume = 1F;
    public float pitch = 1F;

    public PacketSound() {
    }

    public PacketSound(String sound, String soundCategory, float volume, float pitch) {
        this.sound = sound;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sound = ByteBufUtils.readUTF8String(buf);
        this.soundCategory = ByteBufUtils.readUTF8String(buf);
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.sound);
        ByteBufUtils.writeUTF8String(buf, this.soundCategory);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketSound> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketSound message) {
            ResourceLocation rl = new ResourceLocation(message.sound);
            SoundCategory category = SoundCategory.getSoundCategoryNames().contains(message.soundCategory) ? SoundCategory.getByName(message.soundCategory) : SoundCategory.MASTER;

            ISound masterRecord = new PositionedSoundRecord(rl, category, message.volume, message.pitch, false, 0, ISound.AttenuationType.NONE, 0, 0, 0);
            Minecraft.getMinecraft().getSoundHandler().playSound(masterRecord);
        }
    }
}