package magmaout.mappet.entities.utils;

import magmaout.mappet.Mappet;
import magmaout.mappet.entities.EntityNpc;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MappetNpcRespawnManager extends WorldSavedData {
    public static final String DATA_NAME = Mappet.MOD_ID + "_RespawnData";

    public final List<DiedNpcHolder> diedNpcHolders = new ArrayList<>();
    public final List<DiedNpcHolder> deleteCache = new ArrayList<>();
    public World world;

    private BlockPos.MutableBlockPos block = new BlockPos.MutableBlockPos();

    public MappetNpcRespawnManager(String mapName) {
        super(mapName);
    }

    public void onTick() {
        for (DiedNpcHolder diedNpcHolder : this.diedNpcHolders) {
            block.setPos(diedNpcHolder.posX, diedNpcHolder.posY, diedNpcHolder.posZ);

            if (diedNpcHolder.respawnTime <= this.world.getTotalWorldTime() && this.world.isBlockLoaded(block)) {
                this.respawnNpc(diedNpcHolder);
            }
        }

        this.removeSpawnedNpcs();
        this.markDirty();
    }

    public void addDiedNpc(EntityNpc npc) {
        long respawnTime = npc.world.getTotalWorldTime() + npc.getState().respawnDelay.get();
        double posX = npc.getState().respawnOnCoordinates.get() ? npc.getState().respawnPosX.get() : npc.posX;
        double posY = npc.getState().respawnOnCoordinates.get() ? npc.getState().respawnPosY.get() : npc.posY;
        double posZ = npc.getState().respawnOnCoordinates.get() ? npc.getState().respawnPosZ.get() : npc.posZ;

        this.diedNpcHolders.add(new DiedNpcHolder(npc, respawnTime, posX, posY, posZ));
        this.markDirty();
    }

    public void respawnNpc(DiedNpcHolder diedNpcHolder) {
        diedNpcHolder.spawn(this.world);

        this.deleteCache.add(diedNpcHolder);
    }

    public void removeSpawnedNpcs() {
        for (DiedNpcHolder diedNpcHolder : this.deleteCache) {
            this.diedNpcHolders.remove(diedNpcHolder);
        }

        this.clearDeleteCache();
    }

    public void clearDeleteCache() {
        this.deleteCache.clear();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        NBTTagList tagList = nbtTagCompound.getTagList("DiedNPCHolders", Constants.NBT.TAG_COMPOUND);

        for (NBTBase tag : tagList) {
            NBTTagCompound compound = (NBTTagCompound) tag;
            NBTTagCompound nbt = compound.getCompoundTag("NBT");

            String uuid = compound.getString("UUID");
            long respawnTime = compound.getLong("RespawnTime");
            int worldID = compound.getInteger("WorldID");
            double posX = compound.getDouble("PosX");
            double posY = compound.getDouble("PosY");
            double posZ = compound.getDouble("PosZ");

            this.diedNpcHolders.add(new DiedNpcHolder(nbt, uuid, respawnTime, worldID, posX, posY, posZ));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        NBTTagList tagList = new NBTTagList();

        for (DiedNpcHolder diedNpcHolder : diedNpcHolders) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setTag("NBT", diedNpcHolder.nbt);
            tag.setString("UUID", diedNpcHolder.uuid);
            tag.setLong("RespawnTime", diedNpcHolder.respawnTime);
            tag.setInteger("WorldID", diedNpcHolder.worldID);
            tag.setDouble("PosX", diedNpcHolder.posX);
            tag.setDouble("PosY", diedNpcHolder.posY);
            tag.setDouble("PosZ", diedNpcHolder.posZ);

            tagList.appendTag(tag);
        }

        nbtTagCompound.setTag("DiedNPCHolders", tagList);

        return nbtTagCompound;
    }

    public static MappetNpcRespawnManager get(World world) {
        MappetNpcRespawnManager data = (MappetNpcRespawnManager) world.getMapStorage().getOrLoadData(MappetNpcRespawnManager.class, DATA_NAME);

        if (data == null) {
            data = new MappetNpcRespawnManager(DATA_NAME);

            world.getMapStorage().setData(DATA_NAME, data);
        }

        data.world = world;

        return data;
    }
}