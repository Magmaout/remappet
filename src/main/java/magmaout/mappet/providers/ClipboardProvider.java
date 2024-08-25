package magmaout.mappet.providers;

import magmaout.mappet.api.data.ClientData;
import magmaout.mappet.api.data.IClientDataProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

public class ClipboardProvider implements IClientDataProvider {
    public NBTTagCompound getData() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        String clipboard = GuiScreen.getClipboardString();
        nbtTagCompound.setString(ClientData.CLIPBOARD.toString(), clipboard);

        return nbtTagCompound;
    }

    @Override
    public void setData(NBTTagCompound data) {
        GuiScreen.setClipboardString(data.getString(ClientData.CLIPBOARD.toString()));
    }
}