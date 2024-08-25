package magmaout.mappet.capabilities.hand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;

public class Hand implements IHand {
    private EntityPlayer player;

    public String skinPath = new String();
    public String skinType = new String();
    
    public boolean mainRender = true;
    public Vector3d mainRotations = new Vector3d(0, 0, 0);
    public Vector3d mainStartRotations = new Vector3d(0, 0, 0);
    public Vector3d mainEndRotations = new Vector3d(0, 0, 0);
    public String mainInterpRotations = "linear";
    public long mainDurationRotations = 0L;
    public long mainSystemtimeRotations = 0L;
    public Vector3d mainPosition = new Vector3d(0, 0, 0);
    public Vector3d mainStartPosition = new Vector3d(0, 0, 0);
    public Vector3d mainEndPosition = new Vector3d(0, 0, 0);
    public String mainInterpPosition = "linear";
    public long mainDurationPosition = 0L;
    public long mainSystemtimePosition = 0L;
    
    public boolean offRender = false;
    public Vector3d offRotations = new Vector3d(0, 0, 0);
    public Vector3d offStartRotations = new Vector3d(0, 0, 0);
    public Vector3d offEndRotations = new Vector3d(0, 0, 0);
    public String offInterpRotations = "linear";
    public long offDurationRotations = 0L;
    public long offSystemtimeRotations = 0L;
    public Vector3d offPosition = new Vector3d(0, 0, 0);
    public Vector3d offStartPosition = new Vector3d(0, 0, 0);
    public Vector3d offEndPosition = new Vector3d(0, 0, 0);
    public String offInterpPosition = "linear";
    public long offDurationPosition = 0L;
    public long offSystemtimePosition = 0L;


    public static Hand get(EntityPlayer player) {
        IHand capability = player == null ? null : player.getCapability(HandProvider.HAND, null);
        if (capability instanceof Hand) {
            Hand profile = (Hand) capability;
            profile.player = player;

            return profile;
        }
        return null;
    }

    @Override
    public void setSkinPath(String path) { this.skinPath = path; }
    @Override
    public void setSkinType(String type) { this.skinType = type; }

    @Override
    public void setMainRotations(double x, double y, double z) { this.mainRotations = new Vector3d(x, y, z); }
    @Override
    public void setOffRotations(double x, double y, double z) { this.offRotations = new Vector3d(x, y, z); }
    @Override
    public void setMainStartRotations(double x, double y, double z) { this.mainStartRotations = new Vector3d(x, y, z); }
    @Override
    public void setOffStartRotations(double x, double y, double z) { this.offStartRotations = new Vector3d(x, y, z); }
    @Override
    public void setMainEndRotations(double x, double y, double z) { this.mainEndRotations = new Vector3d(x, y, z); }
    @Override
    public void setOffEndRotations(double x, double y, double z) { this.offEndRotations = new Vector3d(x, y, z); }
    @Override
    public void setMainInterpRotations(String interp) { this.mainInterpRotations = interp; }
    @Override
    public void setOffInterpRotations(String interp) { this.offInterpRotations = interp; }
    @Override
    public void setMainDurationRotations(long duration) { this.mainSystemtimeRotations = Minecraft.getSystemTime(); this.mainDurationRotations = this.mainSystemtimeRotations + duration; }
    @Override
    public void setOffDurationRotations(long duration) { this.offSystemtimeRotations = Minecraft.getSystemTime(); this.offDurationRotations = this.offSystemtimeRotations + duration; }

    @Override
    public void setMainPosition(double x, double y, double z) { this.mainPosition = new Vector3d(x, y, z); }
    @Override
    public void setOffPosition(double x, double y, double z) { this.offPosition = new Vector3d(x, y, z); }
    @Override
    public void setMainStartPosition(double x, double y, double z) { this.mainStartPosition = new Vector3d(x, y, z); }
    @Override
    public void setOffStartPosition(double x, double y, double z) { this.offStartPosition = new Vector3d(x, y, z); }
    @Override
    public void setMainEndPosition(double x, double y, double z) { this.mainEndPosition = new Vector3d(x, y, z); }
    @Override
    public void setOffEndPosition(double x, double y, double z) { this.offEndPosition = new Vector3d(x, y, z); }
    @Override
    public void setMainInterpPosition(String interp) { this.mainInterpPosition = interp; }
    @Override
    public void setOffInterpPosition(String interp) { this.offInterpPosition = interp; }
    @Override
    public void setMainDurationPosition(long duration) { this.mainSystemtimePosition = Minecraft.getSystemTime(); this.mainDurationPosition = this.mainSystemtimePosition + duration; }
    @Override
    public void setOffDurationPosition(long duration) { this.offSystemtimePosition = Minecraft.getSystemTime(); this.offDurationPosition = this.offSystemtimePosition + duration; }

    @Override
    public void setMainRender(boolean render) { this.mainRender = render; }
    @Override
    public void setOffRender(boolean render) { this.offRender = render; }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound skin = new NBTTagCompound();
        NBTTagCompound main = new NBTTagCompound();
        NBTTagCompound off = new NBTTagCompound();
        
        NBTTagCompound rotations; NBTTagCompound position;
        NBTTagCompound start; NBTTagCompound end;

        rotations = new NBTTagCompound();
            rotations.setDouble("x", this.mainRotations.x);
            rotations.setDouble("y", this.mainRotations.y);
            rotations.setDouble("z", this.mainRotations.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.mainStartRotations.x);
                start.setDouble("y", this.mainStartRotations.y);
                start.setDouble("z", this.mainStartRotations.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.mainEndRotations.x);
                end.setDouble("y", this.mainEndRotations.y);
                end.setDouble("z", this.mainEndRotations.z);
            rotations.setTag("start", start);
            rotations.setTag("end", end);
            rotations.setString("interp", this.mainInterpRotations);
            rotations.setLong("duration", this.mainDurationRotations);
            rotations.setLong("time", this.mainSystemtimeRotations);
        main.setTag("rotations", rotations);

        position = new NBTTagCompound();
            position.setDouble("x", this.mainPosition.x);
            position.setDouble("y", this.mainPosition.y);
            position.setDouble("z", this.mainPosition.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.mainStartPosition.x);
                start.setDouble("y", this.mainStartPosition.y);
                start.setDouble("z", this.mainStartPosition.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.mainEndPosition.x);
                end.setDouble("y", this.mainEndPosition.y);
                end.setDouble("z", this.mainEndPosition.z);
            position.setTag("start", start);
            position.setTag("end", end);
            position.setString("interp", this.mainInterpPosition);
            position.setLong("duration", this.mainDurationPosition);
            position.setLong("time", this.mainSystemtimePosition);
        main.setTag("position", position);
        main.setBoolean("render", this.mainRender);
        tag.setTag("main", main);

        rotations = new NBTTagCompound();
            rotations.setDouble("x", this.offRotations.x);
            rotations.setDouble("y", this.offRotations.y);
            rotations.setDouble("z", this.offRotations.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.offStartRotations.x);
                start.setDouble("y", this.offStartRotations.y);
                start.setDouble("z", this.offStartRotations.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.offEndRotations.x);
                end.setDouble("y", this.offEndRotations.y);
                end.setDouble("z", this.offEndRotations.z);
            rotations.setTag("start", start);
            rotations.setTag("end", end);
            rotations.setString("interp", this.offInterpRotations);
            rotations.setLong("duration", this.offDurationRotations);
            rotations.setLong("time", this.offSystemtimeRotations);
        off.setTag("rotations", rotations);

        position = new NBTTagCompound();
            position.setDouble("x", this.offPosition.x);
            position.setDouble("y", this.offPosition.y);
            position.setDouble("z", this.offPosition.z);
            start = new NBTTagCompound();
                start.setDouble("x", this.offStartPosition.x);
                start.setDouble("y", this.offStartPosition.y);
                start.setDouble("z", this.offStartPosition.z);
            end = new NBTTagCompound();
                end.setDouble("x", this.offEndPosition.x);
                end.setDouble("y", this.offEndPosition.y);
                end.setDouble("z", this.offEndPosition.z);
            position.setTag("start", start);
            position.setTag("end", end);
            position.setString("interp", this.offInterpPosition);
            position.setLong("duration", this.offDurationPosition);
            position.setLong("time", this.offSystemtimePosition);
        off.setTag("position", position);
        off.setBoolean("render", this.offRender);
        tag.setTag("off", off);

        skin.setString("path", this.skinPath);
        skin.setString("type", this.skinType);
        tag.setTag("skin", skin);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("main")) {
            NBTTagCompound main = tag.getCompoundTag("main");

            NBTTagCompound rotations = main.getCompoundTag("rotations");
                this.mainRotations = new Vector3d(rotations.getDouble("x"), rotations.getDouble("y"), rotations.getDouble("z"));
                NBTTagCompound start = rotations.getCompoundTag("start");
                    this.mainStartRotations = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                NBTTagCompound end = rotations.getCompoundTag("end");
                    this.mainEndRotations = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.mainInterpRotations = rotations.getString("interp");
                this.mainDurationRotations = rotations.getLong("duration");
                this.mainSystemtimeRotations = rotations.getLong("time");

                NBTTagCompound position = main.getCompoundTag("position");
                this.mainPosition = new Vector3d(position.getDouble("x"), position.getDouble("y"), position.getDouble("z"));
                start = position.getCompoundTag("start");
                    this.mainStartPosition = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                end = position.getCompoundTag("end");
                    this.mainEndPosition = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.mainInterpPosition = position.getString("interp");
                this.mainDurationPosition = position.getLong("duration");
                this.mainSystemtimePosition = position.getLong("time");
            this.mainRender = main.getBoolean("render");
        }

        if (tag.hasKey("off")) {
            NBTTagCompound off = tag.getCompoundTag("off");

            NBTTagCompound rotations = off.getCompoundTag("rotations");
                this.offRotations = new Vector3d(rotations.getDouble("x"), rotations.getDouble("y"), rotations.getDouble("z"));
                NBTTagCompound start = rotations.getCompoundTag("start");
                    this.offStartRotations = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                NBTTagCompound end = rotations.getCompoundTag("end");
                    this.offEndRotations = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.offInterpRotations = rotations.getString("interp");
                this.offDurationRotations = rotations.getLong("duration");
                this.offSystemtimeRotations = rotations.getLong("time");

            NBTTagCompound position = off.getCompoundTag("position");
                this.offPosition = new Vector3d(position.getDouble("x"), position.getDouble("y"), position.getDouble("z"));
                start = position.getCompoundTag("start");
                    this.offStartPosition = new Vector3d(start.getDouble("x"), start.getDouble("y"), start.getDouble("z"));
                end = position.getCompoundTag("end");
                    this.offEndPosition = new Vector3d(end.getDouble("x"), end.getDouble("y"), end.getDouble("z"));
                this.offInterpPosition = position.getString("interp");
                this.offDurationPosition = position.getLong("duration");
                this.offSystemtimePosition = position.getLong("time");
            this.offRender = off.getBoolean("render");
        }

        if (tag.hasKey("skin")) {
            NBTTagCompound skin = tag.getCompoundTag("skin");

            this.skinPath = skin.getString("path");
            this.skinType = skin.getString("type");
        }
    }
}
