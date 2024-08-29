package magmaout.mappet.capabilities.character;

import magmaout.mappet.api.huds.HUDScene;
import magmaout.mappet.api.states.States;
import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.utils.CurrentSession;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ICharacter extends INBTSerializable<NBTTagCompound> {
    void setFirstPersonMorph(AbstractMorph morph);

    void setHUDName(String name);

    String getHUDName();

    boolean isRender();

    void setRender(boolean render);

    public States getStates();

    public Instant getLastClear();

    public void updateLastClear(Instant instant);

    public CurrentSession getCurrentSession();

    public UIContext getUIContext();

    public void setUIContext(UIContext context);

    public boolean setupHUD(String id, boolean addToDisplayedList);

    public void changeHUDMorph(String id, int index, NBTTagCompound tag);

    public void closeHUD(String id);

    public void closeAllHUDs();

    public Map<String, List<HUDScene>> getDisplayedHUDs();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound tag);

}