package magmaout.mappet.api.scripts.user.mappet;

import magmaout.mappet.api.scripts.user.IScriptWorld;
import magmaout.mappet.api.scripts.user.entities.IScriptPlayer;

import javax.vecmath.Vector2d;
import java.awt.image.BufferedImage;

public interface IMappetImage {
    /**
     * @return Vector2, width (x) and height (y)
     */
    Vector2d getResolution();

    /**
     * Overlays the mappet image
     *
     * @param overlay
     */
    void addOverlay(IMappetImage overlay, int x, int y);

    /**
     * Overlays the mappet image
     *
     * @param overlay
     * @param erase
     */
    void addOverlay(IMappetImage overlay, boolean erase);

    /**
     * Overlays the mappet image
     *
     * @param overlay
     * @param x
     * @param y
     * @param erase
     */
    void addOverlay(IMappetImage overlay, int x, int y, boolean erase);

    /**
     * @param x
     * @param y
     * @return HEX color (String)
     */
    String getPixelHex(int x, int y);

    /**
     * Download image to world/mappet/textures/name.png
     *
     * @param world
     * @param name
     */
    void downloadTo(IScriptWorld world, String name);

    /**
     * Download image to player config/blockbuster/models/image/name.png
     *
     * @param player
     * @param name
     */
    void downloadTo(IScriptPlayer player, String name);

    /**
     * @return BufferedImage
     */
    BufferedImage getBufferedImage();
}