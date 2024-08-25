package magmaout.mappet.capabilities.character;

import magmaout.mappet.capabilities.camera.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CharacterProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICharacter.class)
    public static final Capability<ICharacter> CHARACTER = null;

    private ICharacter instance = CHARACTER.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CHARACTER;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CHARACTER ? CHARACTER.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return CHARACTER.getStorage().writeNBT(CHARACTER, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        CHARACTER.getStorage().readNBT(CHARACTER, this.instance, null, nbt);
    }

    public static ICharacter getHandler(Entity entity) {
        if (entity.hasCapability(CHARACTER, EnumFacing.DOWN))
            return entity.getCapability(CHARACTER, EnumFacing.DOWN);
        return null;
    }
}