package magmaout.mappet.providers;

import magmaout.mappet.api.data.ClientData;
import magmaout.mappet.api.data.IClientDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class ResolutionProvider implements IClientDataProvider {
    @Override
    public NBTTagCompound getData() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int height = minecraft.displayHeight;
        int width = minecraft.displayWidth;

        NBTTagCompound object = new NBTTagCompound();
        object.setInteger("x", width);
        object.setInteger("y", height);

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        nbtTagCompound.setTag(ClientData.RESOLUTION.toString(), object);

        return nbtTagCompound;
    }

    @Override
    public void setData(NBTTagCompound data) {
    }
}