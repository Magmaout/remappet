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
    private String name;
    private BufferedImage image;

    public PacketDownloadImage() {}

    public PacketDownloadImage(BufferedImage image, String name) {
        this.name = name;
        this.image = image;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = ByteBufUtils.readUTF8String(buf);
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        try {
            this.image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.name);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketDownloadImage> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketDownloadImage message) {
            Path path = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), "config/blockbuster/models/image");
            String name = message.name.endsWith(".png") ? message.name : message.name + ".png";
            try {
                if (!Files.exists(path)) Files.createDirectories(path);
                path = path.resolve(name);
                ImageIO.write(message.image, "png", path.toFile());
            } catch (IOException e) {}
        }
    }
}