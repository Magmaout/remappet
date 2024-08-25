package magmaout.mappet.capabilities.location;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class LocationProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ILocation.class)
    public static final Capability<ILocation> LOCATION = null;

    private ILocation instance = LOCATION.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == LOCATION;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return hasCapability(capability, facing) ? LOCATION.<T>cast(instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return LOCATION.getStorage().writeNBT(LOCATION, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        LOCATION.getStorage().readNBT(LOCATION, instance, null, nbt);
    }
}
