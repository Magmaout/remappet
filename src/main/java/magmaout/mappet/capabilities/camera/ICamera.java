package magmaout.mappet.capabilities.camera;

import net.minecraft.nbt.NBTTagCompound;

public interface ICamera {
    void setRotations(double x, double y, double z);
    void setStartRotations(double x, double y, double z);
    void setEndRotations(double x, double y, double z);
    void setInterpRotations(String interp);
    void setDurationRotations(Long duration);

    void setPosition(double x, double y, double z);
    void setStartPosition(double x, double y, double z);
    void setEndPosition(double x, double y, double z);
    void setInterpPosition(String interp);
    void setDurationPosition(Long duration);

    void setScaling(double x, double y, double z);
    void setStartScaling(double x, double y, double z);
    void setEndScaling(double x, double y, double z);
    void setInterpScaling(String interp);
    void setDurationScaling(Long duration);


    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
