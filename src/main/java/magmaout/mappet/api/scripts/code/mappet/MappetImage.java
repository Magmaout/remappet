package magmaout.mappet.api.scripts.code.mappet;

import magmaout.mappet.api.scripts.user.mappet.IMappetImage;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import javax.imageio.ImageIO;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MappetImage implements IMappetImage {
    public BufferedImage image;

    public static MappetImage create(String path) {
        return new MappetImage(path);
    }

    public MappetImage(String path) {
        try {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                URL url = new URL(path);
                try {
                    this.image = ImageIO.read(url);
                } catch (IOException e) {
                    URLConnection connection  = url.openConnection();
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setConnectTimeout(5000);
                    this.image = ImageIO.read(connection.getInputStream());
                }
            } else {
                File file = new File(path);
                this.image = ImageIO.read(file);
            }
        } catch (IOException e) {
            this.image = null;
        }
    }

    @Override
    public Vector2d getResolution() {
        return new Vector2d(this.image.getWidth(), this.image.getHeight());
    }

    @Override
    public void addOverlay(IMappetImage overlay, boolean erase) { overlayImage(overlay, 0, 0, erase); }
    @Override
    public void addOverlay(IMappetImage overlay, int x, int y) { overlayImage(overlay, x, y, false); }
    @Override
    public void addOverlay(IMappetImage overlay, int x, int y, boolean erase) { overlayImage(overlay, x, y, erase); }

    private void overlayImage(IMappetImage overlay, int x, int y, boolean erase) {
        BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(this.image, 0, 0, null);
        if (erase) g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
        g.drawImage(overlay.getBufferedImage(), x, y, null);
        g.dispose();
        this.image = image;
    }

    @Override
    public String getPixelHex(int x, int y) {
        return Integer.toHexString(this.image.getRGB(x, y));
    }

    @Override
    public void saveToWorld(String name) {
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        Path path = Paths.get(server.getEntityWorld().getSaveHandler().getWorldDirectory().getPath(), "mappet/textures");
        name = name.endsWith(".png") ? name : name + ".png";
        try {
            if (!Files.exists(path)) Files.createDirectories(path);
            path = path.resolve(name);
            ImageIO.write(this.image, "png", path.toFile());
        } catch (IOException e) {}
    }

    @Override
    public BufferedImage getBufferedImage() {
        return this.image;
    }
}
