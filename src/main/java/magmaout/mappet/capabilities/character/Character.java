package magmaout.mappet.capabilities.character;

import magmaout.mappet.Mappet;
import magmaout.mappet.api.huds.HUDMorph;
import magmaout.mappet.api.huds.HUDScene;
import magmaout.mappet.api.states.States;
import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.huds.PacketHUDMorph;
import magmaout.mappet.network.huds.PacketHUDScene;
import magmaout.mappet.utils.CurrentSession;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.time.Instant;
import java.util.*;

public class Character implements ICharacter {
    private EntityPlayer player;

    public static Character get(EntityPlayer player) {
        ICharacter characterCapability = player == null ? null : player.getCapability(CharacterProvider.CHARACTER, null);
        if (characterCapability instanceof Character) {
            Character character = (Character) characterCapability;
            character.player = player;
            return character;
        }
        return null;
    }
    public Map<String, Boolean> HUDs = this.createHUDs();
    public String HUDname;
    public AbstractMorph morph = null;
    private States states = new States();
    private Instant lastClear = Instant.now();
    private CurrentSession session = new CurrentSession();
    private UIContext uiContext;
    private Map<String, List<HUDScene>> displayedHUDs = new HashMap<>();

    @Override
    public void setFirstPersonMorph(AbstractMorph morph) {
        this.morph = morph;
    }

    @Override
    public void setHUDName(String name){
        this.HUDname = name;
    }

    @Override
    public String getHUDName(){
        return this.HUDname;
    }

    @Override
    public boolean isRender() { return this.HUDs.get(this.getHUDName()); }

    @Override
    public void setRender(boolean render) { this.HUDs.replace(this.getHUDName(), render); }

    @Override
    public States getStates() {
        return this.states;
    }

    @Override
    public Instant getLastClear() {
        return this.lastClear;
    }

    @Override
    public void updateLastClear(Instant instant) {
        this.lastClear = instant;
    }

    @Override
    public CurrentSession getCurrentSession() {
        return this.session;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("States", this.states.serializeNBT());
        tag.setString("LastClear", this.lastClear.toString());
        tag.setTag("DisplayedHUDs", serializeDisplayedHUDs());
        tag.setTag("FirstPersonMorph", this.morph != null ? this.morph.toNBT() : new NBTTagCompound());

        this.HUDs.keySet().forEach((key) -> tag.setBoolean(key, this.HUDs.get(key)));

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("FirstPersonMorph")) {
            this.morph = !tag.getTag("FirstPersonMorph").hasNoTags() ? MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("FirstPersonMorph")) : null;
        }

        if (tag.hasKey("States")) {
            this.states.deserializeNBT(tag.getCompoundTag("States"));
        }

        if (tag.hasKey("LastClear")) {
            try {
                this.lastClear = Instant.parse(tag.getString("LastClear"));
            } catch (Exception e) {}
        }

        if (tag.hasKey("DisplayedHUDs")) {
            deserializeDisplayedHUDs(tag.getCompoundTag("DisplayedHUDs"));
        }

        this.HUDs.keySet().forEach((key) -> {
            if(tag.hasKey(key)) {
                this.HUDs.put(key, tag.getBoolean(key));
            }
        });
    }

    @Override
    public UIContext getUIContext() {
        return this.uiContext;
    }

    @Override
    public void setUIContext(UIContext context) {
        this.uiContext = context;
    }

    public boolean setupHUD(String id, boolean addToDisplayedList) {
        HUDScene scene = Mappet.huds.load(id);

        if (scene != null) {
            Dispatcher.sendTo(new PacketHUDScene(scene.getId(), scene.serializeNBT()), (EntityPlayerMP) this.player);

            //if the hud is global, display it to all players as well
            if (scene.global) {
                for (EntityPlayer player : this.player.world.playerEntities) {
                    if (player != this.player) {
                        Dispatcher.sendTo(new PacketHUDScene(scene.getId(), scene.serializeNBT()), (EntityPlayerMP) player);
                    }
                }
            }

            // Adds the morph to the displayedHUDs list
            if (addToDisplayedList) {
                getDisplayedHUDs().put(id, Arrays.asList(scene));
            }
            return true;
        }

        return false;
    }

    @Override
    public void changeHUDMorph(String id, int index, NBTTagCompound tag) {
        Dispatcher.sendTo(new PacketHUDMorph(id, index, tag), (EntityPlayerMP) this.player);

        //if the hud is global, display change it for all players as well
        HUDScene scene = Mappet.huds.load(id);

        if (scene.global) {
            for (EntityPlayer player : this.player.world.playerEntities) {
                if (player != this.player) {
                    Dispatcher.sendTo(new PacketHUDMorph(id, index, tag), (EntityPlayerMP) player);
                }
            }
        }

        // Changing the HUDMorph in the displayedHUDs list
        for (Map.Entry<String, List<HUDScene>> entry : getDisplayedHUDs().entrySet()) {
            if (entry.getKey().equals(id)) {
                List<HUDScene> scenes = entry.getValue();
                if (!scenes.isEmpty()) {
                    scene = scenes.get(0);
                    if (scene.morphs.size() > index) {
                        HUDMorph newMorph = scene.morphs.get(index).copy();
                        newMorph.morph = new Morph(MorphManager.INSTANCE.morphFromNBT(tag));
                        scene.morphs.set(index, newMorph);
                    }
                }
            }
        }
    }

    @Override
    public void closeHUD(String id) {
        Dispatcher.sendTo(new PacketHUDScene(id == null ? "" : id, null), (EntityPlayerMP) this.player);

        //if the hud is global, close it for all players as well
        HUDScene scene = Mappet.huds.load(id);

        if (scene.global) {
            for (EntityPlayer player : this.player.world.playerEntities) {
                if (player != this.player) {
                    Dispatcher.sendTo(new PacketHUDScene(id == null ? "" : id, null), (EntityPlayerMP) player);
                }
            }
        }

        getDisplayedHUDs().remove(id);
    }

    @Override
    public void closeAllHUDs() {
        for (Map.Entry<String, List<HUDScene>> entry : getDisplayedHUDs().entrySet()) {
            Dispatcher.sendTo(new PacketHUDScene(entry.getKey(), null), (EntityPlayerMP) player);
            if (((HUDScene) ((List) entry.getValue()).get(0)).global)
                for (EntityPlayer player : this.player.world.playerEntities) {
                    if (player != this.player)
                        Dispatcher.sendTo(new PacketHUDScene(entry.getKey(), null), (EntityPlayerMP) player);
                }
        }
        getDisplayedHUDs().clear();
    }

    @Override
    public Map<String, List<HUDScene>> getDisplayedHUDs() {
        return displayedHUDs;
    }

    private NBTTagCompound serializeDisplayedHUDs() {
        return getDisplayedHUDsTag();
    }

    private void deserializeDisplayedHUDs(NBTTagCompound tag) {
        displayedHUDs.clear();
        for (String key : tag.getKeySet()) {
            NBTTagList sceneList = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
            List<HUDScene> scenes = new ArrayList<>();
            for (int i = 0; i < sceneList.tagCount(); i++) {
                NBTTagCompound sceneTag = sceneList.getCompoundTagAt(i);
                HUDScene scene = new HUDScene();
                scene.deserializeNBT(sceneTag);
                scenes.add(scene);
            }
            displayedHUDs.put(key, scenes);
        }
    }

    private Map<String, Boolean> createHUDs(){
        Map<String, Boolean> map = new HashMap<>();

        String[] huds = new String[]{"ALL", "HELMET", "PORTAL", "CROSSHAIRS", "BOSSHEALTH", "BOSSINFO", "ARMOR", "HEALTH", "FOOD", "AIR", "HOTBAR", "EXPERIENCE", "TEXT", "HEALTHMOUNT", "JUMPBAR", "CHAT", "PLAYER_LIST", "DEBUG", "POTION_ICONS", "SUBTITLES", "FPS_GRAPH", "VIGNETTE"};

        for(String hud : huds)
            map.put(hud, true);

        return map;
    }


    public NBTTagCompound getDisplayedHUDsTag() {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<String, List<HUDScene>> entry : displayedHUDs.entrySet()) {
            NBTTagList sceneList = new NBTTagList();
            for (HUDScene scene : entry.getValue()) {
                sceneList.appendTag(scene.serializeNBT());
            }
            tag.setTag(entry.getKey(), sceneList);
        }
        return tag;
    }

    public NBTTagCompound getGlobalDisplayedHUDsTag() {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<String, List<HUDScene>> entry : displayedHUDs.entrySet()) {
            if (entry.getValue().get(0).global) {
                NBTTagList sceneList = new NBTTagList();
                for (HUDScene scene : entry.getValue()) {
                    sceneList.appendTag(scene.serializeNBT());
                }
                tag.setTag(entry.getKey(), sceneList);
            }
        }
        return tag;
    }

    /**
     * This method checks on character's tick if it has
     * a scene with an HUDMorph with `expire` in one of its displayedHUDs.
     * If it has, and it's not 0, then it should decrement it.
     * If it's 0, then it should remove the HUDMorph from the scene.
     * If the scene is empty, then it should remove the scene from the displayedHUDs.
     */
    public void updateDisplayedHUDsList() {
        Iterator<Map.Entry<String, List<HUDScene>>> iterator = getDisplayedHUDs().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<HUDScene>> entry = iterator.next();
            List<HUDScene> scenes = entry.getValue();
            boolean removeScene = false;
            for (HUDScene scene : scenes) {
                List<HUDMorph> morphs = scene.morphs;
                boolean updated = false;
                for (int i = 0; i < morphs.size(); i++) {
                    HUDMorph morph = morphs.get(i);
                    if (morph.expire > 0) {
                        morph.expire--;
                        if (morph.expire == 0) {
                            morphs.remove(i);
                            i--;
                            updated = true;
                        }
                    }
                }
                if (updated && morphs.isEmpty()) {
                    removeScene = true;
                    break;
                }
            }
            if (removeScene) {
                iterator.remove();
            }
        }
    }
}