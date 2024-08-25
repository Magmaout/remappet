package magmaout.mappet.capabilities.hand;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;

public interface IHand {
    void setSkinPath(String path);
    void setSkinType(String type);

    void setMainRotations(double x, double y, double z);
    void setMainStartRotations(double x, double y, double z);
    void setMainEndRotations(double x, double y, double z);
    void setMainInterpRotations(String interp);
    void setMainDurationRotations(long duration);
    void setMainPosition(double x, double y, double z);
    void setMainStartPosition(double x, double y, double z);
    void setMainEndPosition(double x, double y, double z);
    void setMainInterpPosition(String interp);
    void setMainDurationPosition(long duration);

    void setOffRotations(double x, double y, double z);
    void setOffStartRotations(double x, double y, double z);
    void setOffEndRotations(double x, double y, double z);
    void setOffInterpRotations(String interp);
    void setOffDurationRotations(long duration);
    void setOffPosition(double x, double y, double z);
    void setOffStartPosition(double x, double y, double z);
    void setOffEndPosition(double x, double y, double z);
    void setOffInterpPosition(String interp);
    void setOffDurationPosition(long duration);

    void setMainRender(boolean render);
    void setOffRender(boolean render);

    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
