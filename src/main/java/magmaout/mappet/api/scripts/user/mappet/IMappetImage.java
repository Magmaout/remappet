package magmaout.mappet.api.scripts.user.mappet;

import javax.vecmath.Vector2d;
import java.awt.image.BufferedImage;

public interface IMappetImage {
    Vector2d getResolution();

    void addOverlay(IMappetImage overlay, int x, int y, boolean erase);

    String getPixelHex(int x, int y);

    void forceDownload(String name);

    BufferedImage getBufferedImage();
}