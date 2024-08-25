package magmaout.mappet.capabilities.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3d;

public class Camera implements ICamera {
    private EntityPlayer player;


    public Vector3d rotations = new Vector3d(0, 0, 0);
    public Vector3d startRotations = new Vector3d(0, 0, 0);
    public Vector3d endRotations = new Vector3d(0, 0, 0);
    public String interpRotations = "linear";
    public Long durationRotations = 0L;
    public Long systemtimeRotations = 0L;


    public Vector3d position = new Vector3d(0, 0, 0);
    public Vector3d startPosition = new Vector3d(0, 0, 0);
    public Vector3d endPosition = new Vector3d(0, 0, 0);
    public String interpPosition = "linear";
    public Long durationPosition = 0L;
    public Long systemtimePosition = 0L;


    public Vector3d scaling = new Vector3d(1, 1, 1);
    public Vector3d startScaling = new Vector3d(1, 1, 1);
    public Vector3d endScaling = new Vector3d(1, 1, 1);
    public String interpScaling = "linear";
    public Long durationScaling = 0L;
    public Long systemtimeScaling = 0L;

    public static Camera get(EntityPlayer player) {
        ICamera capability = player == null ? null : player.getCapability(CameraProvider.CAMERA, null);
        if (capability instanceof Camera) {
            Camera profile = (Camera) capability;
            profile.player = player;

            return profile;
        }
        return null;
    }

    @Override
    public void setRotations(double x, double y, double z) { this.rotations = new Vector3d(x, y, z); }
    @Override
    public void setStartRotations(double x, double y, double z) { this.startRotations = new Vector3d(x, y, z); }
    @Override
    public void setEndRotations(double x, double y, double z) { this.endRotations = new Vector3d(x, y, z); }
    @Override
    public void setInterpRotations(String interp) { this.interpRotations = interp; }
    @Override
    public void setDurationRotations(Long duration) { this.systemtimeRotations = Minecraft.getSystemTime(); this.durationRotations = this.systemtimeRotations + duration; }


    @Override
    public void setPosition(double x, double y, double z) { this.position = new Vector3d(x, y, z); }
    @Override
    public void setStartPosition(double x, double y, double z) { this.startPosition = new Vector3d(x, y, z); }
    @Override
    public void setEndPosition(double x, double y, double z) { this.endPosition = new Vector3d(x, y, z); }
    @Override
    public void setInterpPosition(String interp) { this.interpPosition = interp; }
    @Override
    public void setDurationPosition(Long duration) { this.systemtimePosition = Minecraft.getSystemTime(); this.durationPosition = this.systemtimePosition + duration; }


    @Override
    public void setScaling(double x, double y, double z) { this.scaling = new Vector3d(x, y, z); }
    @Override
    public void setStartScaling(double x, double y, double z) { this.startScaling = new Vector3d(x, y, z); }
    @Override
    public void setEndScaling(double x, double y, double z) { this.endScaling = new Vector3d(x, y, z); }
    @Override
    public void setInterpScaling(String interp) { this.interpScaling = interp; }
    @Override
    public void setDurationScaling(Long duration) { this.systemtimeScaling = Minecraft.getSystemTime(); this.durationScaling = this.systemtimeScaling + duration; }


    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound start; NBTTagCompound end;

        NBTTagCompound rotations = new NBTTagCompound();
            rotations.setDouble("x", this.rotations.x);
            rotations.setDouble("y", this.rotations.y);
            rotations.setDouble("z", this.rotations.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.startRotations.x);
                start.setDouble("y", this.startRotations.y);
                start.setDouble("z", this.startRotations.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.endRotations.x);
                end.setDouble("y", this.endRotations.y);
                end.setDouble("z", this.endRotations.z);
            rotations.setTag("start", start);
            rotations.setTag("end", end);
            rotations.setString("interp", this.interpRotations);
            rotations.setLong("duration", this.durationRotations);
            rotations.setLong("time", this.systemtimeRotations);
        tag.setTag("rotations", rotations);

        NBTTagCompound position = new NBTTagCompound();
            position.setDouble("x", this.position.x);
            position.setDouble("y", this.position.y);
            position.setDouble("z", this.position.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.startPosition.x);
                start.setDouble("y", this.startPosition.y);
                start.setDouble("z", this.startPosition.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.endPosition.x);
                end.setDouble("y", this.endPosition.y);
                end.setDouble("z", this.endPosition.z);
            position.setTag("start", start);
            position.setTag("end", end);
            position.setString("interp", this.interpPosition);
            position.setLong("duration", this.durationPosition);
            position.setLong("time", this.systemtimePosition);
        tag.setTag("position", position);

        NBTTagCompound scaling = new NBTTagCompound();
            scaling.setDouble("x", this.scaling.x);
            scaling.setDouble("y", this.scaling.y);
            scaling.setDouble("z", this.scaling.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.startScaling.x);
                start.setDouble("y", this.startScaling.y);
                start.setDouble("z", this.startScaling.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.endScaling.x);
                end.setDouble("y", this.endScaling.y);
                end.setDouble("z", this.endScaling.z);
            scaling.setTag("start", start);
            scaling.setTag("end", end);
            scaling.setString("interp", this.interpScaling);
            scaling.setLong("duration", this.durationScaling);
            scaling.setLong("time", this.systemtimeScaling);
        tag.setTag("scaling", scaling);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("rotations")) {
            NBTTagCompound rotations = tag.getCompoundTag("rotations");
                this.rotations = new Vector3d(rotations.getDouble("x"), rotations.getDouble("y"), rotations.getDouble("z"));
                NBTTagCompound start = rotations.getCompoundTag("start");
                    this.startRotations = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                NBTTagCompound end = rotations.getCompoundTag("end");
                    this.endRotations = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.interpRotations = rotations.getString("interp");
                this.durationRotations = rotations.getLong("duration");
                this.systemtimeRotations = rotations.getLong("time");
        }

        if (tag.hasKey("position")) {
            NBTTagCompound position = tag.getCompoundTag("position");
                this.position = new Vector3d(position.getDouble("x"), position.getDouble("y"), position.getDouble("z"));
                NBTTagCompound start = position.getCompoundTag("start");
                    this.startPosition = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                NBTTagCompound end = position.getCompoundTag("end");
                    this.endPosition = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.interpPosition = position.getString("interp");
                this.durationPosition = position.getLong("duration");
                this.systemtimePosition = position.getLong("time");
        }

        if (tag.hasKey("scaling")) {
            NBTTagCompound scaling = tag.getCompoundTag("scaling");
                this.scaling = new Vector3d(scaling.getDouble("x"), scaling.getDouble("y"), scaling.getDouble("z"));
                NBTTagCompound start = scaling.getCompoundTag("start");
                    this.startScaling = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                NBTTagCompound end = scaling.getCompoundTag("end");
                    this.endScaling = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.interpScaling = scaling.getString("interp");
                this.durationScaling = scaling.getLong("duration");
                this.systemtimeScaling = scaling.getLong("time");
        }
    }
}