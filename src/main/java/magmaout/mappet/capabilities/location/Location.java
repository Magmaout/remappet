package magmaout.mappet.capabilities.location;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Location implements ILocation {
    private EntityPlayer player;

    public List<Float> pitchClamping = new ArrayList<>(Arrays.asList(-90f, 90f));
    public List<Float> yawClamping = new ArrayList<>(Arrays.asList(-1080f, 1080f));

    public Vector3f start = new Vector3f(0, 0, 0);
    public Vector3f end = new Vector3f(0, 0, 0);
    public String interp = "linear";
    public Long systemtime = 0L;
    public Long duration = 0L;

    public static Location get(EntityPlayer player) {
        ILocation locationCapability = player == null ? null : player.getCapability(LocationProvider.LOCATION, null);

        if (locationCapability instanceof Location) {
            Location profile = (Location) locationCapability;
            profile.player = player;

            return profile;
        }
        return null;
    }

    @Override
    public void setStart(float x, float y, float z) { this.start = new Vector3f(x, y, z); }
    @Override
    public void setEnd(float x, float y, float z) { this.end = new Vector3f(x, y, z); }
    @Override
    public void setInterp(String interp) { this.interp = interp; }
    @Override
    public void setDuration(Long duration) { this.systemtime = Minecraft.getSystemTime(); this.duration = this.systemtime + duration; }

    @Override
    public void setPitchClamping(float min, float max) { this.pitchClamping.set(0, min); this.pitchClamping.set(1, max); }
    @Override
    public void setYawClamping(float min, float max) { this.yawClamping.set(0, min); this.yawClamping.set(1, max); }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        NBTTagCompound rotate = new NBTTagCompound();
            NBTTagCompound start = new NBTTagCompound();
                start.setDouble("x", this.start.x);
                start.setDouble("y", this.start.y);
                start.setDouble("z", this.start.z);
            NBTTagCompound end = new NBTTagCompound();
                end.setDouble("x", this.end.x);
                end.setDouble("y", this.end.y);
                end.setDouble("z", this.end.z);
            rotate.setTag("start", start);
            rotate.setTag("end", end);
            rotate.setString("interp", this.interp);
            rotate.setLong("systemtime", this.systemtime);
            rotate.setLong("duration", this.duration);
        tag.setTag("rotate", rotate);

        NBTTagCompound clamping = new NBTTagCompound();
            NBTTagCompound pitch = new NBTTagCompound();
                pitch.setFloat("min", this.pitchClamping.get(0));
                pitch.setFloat("max", this.pitchClamping.get(1));
            NBTTagCompound yaw = new NBTTagCompound();
                yaw.setFloat("min", this.yawClamping.get(0));
                yaw.setFloat("max", this.yawClamping.get(1));
            clamping.setTag("pitch", pitch);
            clamping.setTag("yaw", yaw);
        tag.setTag("clamping", clamping);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("clamping")) {
            NBTTagCompound clamping = tag.getCompoundTag("clamping");
                NBTTagCompound pitch = clamping.getCompoundTag("pitch");
                    this.pitchClamping = new ArrayList<>(Arrays.asList(pitch.getFloat("min"), pitch.getFloat("max")));
                NBTTagCompound yaw = clamping.getCompoundTag("yaw");
                    this.yawClamping = new ArrayList<>(Arrays.asList(yaw.getFloat("min"), yaw.getFloat("max")));
        }

        if (tag.hasKey("rotate")) {
            NBTTagCompound rotate = tag.getCompoundTag("rotate");
                NBTTagCompound start = rotate.getCompoundTag("start");
                    this.start = new Vector3f(start.getFloat("x"), start.getFloat("y"), start.getFloat("z"));
                NBTTagCompound end = rotate.getCompoundTag("end");
                    this.end = new Vector3f(end.getFloat("x"), end.getFloat("y"), end.getFloat("z"));
                this.interp = rotate.getString("interp");
                this.duration = rotate.getLong("duration");
                this.systemtime = rotate.getLong("systemtime");
        }
    }
}