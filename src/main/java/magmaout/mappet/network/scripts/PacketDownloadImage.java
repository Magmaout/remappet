package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PacketDownloadImage implements IMessage {
    public byte[] image;
    public String name;

    public PacketDownloadImage() {
    }

    public PacketDownloadImage(BufferedImage image, String name) {
        this.name = name;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            baos.flush();
            this.image = baos.toByteArray();
            baos.close();
        } catch (IOException e) {}
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.image = new byte[buf.readInt()];
        buf.readBytes(this.image);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeInt(this.image.length);
        buf.writeBytes(this.image);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketDownloadImage> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketDownloadImage message) {
            Path path = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), "config/mappet/textures");
            try {
                if (!Files.exists(path)) Files.createDirectories(path);
                path = path.resolve(message.name);
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(message.image)), "png", path.toFile());
            } catch (IOException e) {}
        }
    }
}