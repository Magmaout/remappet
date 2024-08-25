package magmaout.mappet.providers;

import magmaout.mappet.api.data.ClientData;
import magmaout.mappet.api.data.IClientDataProvider;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.minecraft.nbt.NBTTagCompound;

public class WebLinkProvider implements IClientDataProvider {
    @Override
    public void setData(NBTTagCompound data) {
        GuiUtils.openWebLink(data.getString(ClientData.WEB_LINK.toString()));
    }
}