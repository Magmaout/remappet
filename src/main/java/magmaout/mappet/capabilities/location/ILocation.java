package magmaout.mappet.capabilities.location;

import net.minecraft.nbt.NBTTagCompound;

public interface ILocation {
    void setStart(float x, float y, float z);
    void setEnd(float x, float y, float z);
    void setDuration(Long duration);
    void setInterp(String interp);

    void setPitchClamping(float min, float max);
    void setYawClamping(float min, float max);

    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
