package magmaout.mappet.api.utils;

import magmaout.mappet.api.utils.manager.IManager;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IContentType {

    /* Every Karen be like :D */
    IManager<? extends AbstractData> getManager();

    @SideOnly(Side.CLIENT)
    GuiMappetDashboardPanel get(GuiMappetDashboard dashboard);

    @SideOnly(Side.CLIENT)
    IKey getPickLabel();

    String getName();

    static IContentType getType(String name) {
        IContentType type = ContentType.valueOf(name);
        /* Place for mixins */
        return type;
    }
}
