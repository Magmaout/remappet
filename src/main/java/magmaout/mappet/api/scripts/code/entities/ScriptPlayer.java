package magmaout.mappet.api.scripts.code.entities;

import io.netty.buffer.Unpooled;
import magmaout.mappet.api.data.ClientData;
import magmaout.mappet.api.scripts.code.mappet.MappetUIBuilder;
import magmaout.mappet.api.scripts.code.mappet.MappetUIContext;
import magmaout.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import magmaout.mappet.api.scripts.code.render.ScriptCamera;
import magmaout.mappet.api.scripts.code.render.ScriptHand;
import magmaout.mappet.api.scripts.user.items.IScriptInventory;
import magmaout.mappet.api.scripts.user.items.IScriptItemStack;
import magmaout.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import magmaout.mappet.api.scripts.user.mappet.IMappetUIContext;
import magmaout.mappet.api.scripts.user.nbt.INBT;
import magmaout.mappet.api.scripts.user.nbt.INBTCompound;
import magmaout.mappet.api.scripts.user.render.IScriptCamera;
import magmaout.mappet.api.scripts.user.render.IScriptHand;
import magmaout.mappet.api.ui.UI;
import magmaout.mappet.api.ui.UIContext;
import magmaout.mappet.network.scripts.PacketRefreshResources;
import magmaout.mappet.network.utils.PacketClientData;
import magmaout.mappet.utils.AccessType;
import magmaout.mappet.utils.CapabilityTypes;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.capabilities.character.ICharacter;
import magmaout.mappet.capabilities.location.Location;
import magmaout.mappet.network.scripts.PacketCapability;
import magmaout.mappet.network.ui.PacketUI;
import magmaout.mappet.utils.WorldUtils;
import mchorse.aperture.network.common.PacketCameraState;
import magmaout.mappet.api.scripts.code.items.ScriptInventory;
import magmaout.mappet.api.scripts.user.data.ScriptVector;
import magmaout.mappet.api.scripts.user.entities.IScriptPlayer;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.scripts.PacketEntityRotations;
import magmaout.mappet.network.scripts.PacketSound;
import magmaout.mappet.network.ui.PacketCloseUI;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.util.UUID;
import java.util.function.Consumer;

public class ScriptPlayer extends ScriptEntity<EntityPlayerMP> implements IScriptPlayer {
    private IScriptInventory inventory;

    private IScriptInventory enderChest;

    public ScriptPlayer(EntityPlayerMP entity) {
        super(entity);
    }

    @Override
    public EntityPlayerMP getMinecraftPlayer() {
        return this.entity;
    }

    @Override
    public IScriptCamera getCamera() {
        return new ScriptCamera(this.entity);
    }

    @Override
    public IScriptHand getHand(int side) {
        return new ScriptHand(this.entity, side);
    }

    @Override
    public void setPrimaryHand(int hand) {
        EnumHandSide side = hand == 0 ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
        this.entity.setPrimaryHand(side);
    }

    @Override
    public int getPrimaryHand() {
        int hand = this.entity.getPrimaryHand() == EnumHandSide.RIGHT ? 0 : 1;
        return hand;
    }

    @Override
    public void setRenderHUD(String name, boolean render) {
        Character character = Character.get(this.entity);
        character.setHUDName(name.toUpperCase());
        character.setRender(render);

        Dispatcher.sendTo(new PacketCapability(character.serializeNBT(), CapabilityTypes.CHARACTER), this.entity);
    }

    @Override
    public void setMotion(double x, double y, double z) {
        super.setMotion(x, y, z);

        this.entity.connection.sendPacket(new SPacketEntityVelocity(this.entity.getEntityId(), x, y, z));
    }

    @Override
    public void setRotations(float pitch, float yaw, float yawHead) {
        super.setRotations(pitch, yaw, yawHead);

        Dispatcher.sendTo(new PacketEntityRotations(this.entity.getEntityId(), yaw, yawHead, pitch), this.entity);
    }

    @Override
    public void swingArm(int arm) {
        super.swingArm(arm);

        this.entity.connection.sendPacket(new SPacketAnimation(this.entity, arm == 1 ? 3 : 0));
    }

    @Override
    public void openWebLink(String address){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString(ClientData.WEB_LINK.toString(), address);
        Dispatcher.sendTo(new PacketClientData(ClientData.WEB_LINK, AccessType.SET, nbtTagCompound), this.getMinecraftPlayer());
    }

    @Override
    public void setClipboard(String text){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString(ClientData.CLIPBOARD.toString(), text);
        Dispatcher.sendTo(new PacketClientData(ClientData.CLIPBOARD, AccessType.SET, nbtTagCompound), this.getMinecraftPlayer());
    }

    @Override
    public void getClipboard(Consumer<Object> callback){
        UUID uniqueId = UUID.randomUUID();
        PacketClientData.сallBack.put(uniqueId, callback);
        Dispatcher.sendTo(new PacketClientData(ClientData.CLIPBOARD, AccessType.GET, uniqueId), this.getMinecraftPlayer());
    }

    @Override
    public void refreshResources() {
        Dispatcher.sendTo(new PacketRefreshResources(), this.entity);
    }

    @Override
    public void getResolution(Consumer<Object> callback) {
        UUID uniqueId = UUID.randomUUID();
        PacketClientData.сallBack.put(uniqueId, callback);

        Dispatcher.sendTo(new PacketClientData(ClientData.RESOLUTION, AccessType.GET, uniqueId), this.getMinecraftPlayer());
    }

    @Override
    public void setMousePosition(int x, int y, boolean isInsideWindow) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        NBTTagCompound object = new NBTTagCompound();
        nbtTagCompound.setTag(ClientData.MOUSEPOSITION.toString(), object);
        object.setInteger("x", x);
        object.setInteger("y", y);
        nbtTagCompound.setBoolean("isInsideWindow", isInsideWindow);
        Dispatcher.sendTo(new PacketClientData(ClientData.MOUSEPOSITION, AccessType.SET, nbtTagCompound), this.getMinecraftPlayer());
    }

    @Override
    public void getMousePosition(Consumer<Object> callback, boolean isInsideWindow){
        NBTTagCompound data = new NBTTagCompound();
        data.setBoolean("isInsideWindow", isInsideWindow);
        UUID uniqueId = UUID.randomUUID();
        PacketClientData.сallBack.put(uniqueId, callback);
        Dispatcher.sendTo(new PacketClientData(data, ClientData.MOUSEPOSITION, AccessType.GET_WITH_DATA, uniqueId), this.getMinecraftPlayer());
    }

    @Override
    public void setSetting(String key, Object value) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString("value", value.toString());
        nbtTagCompound.setString("key", key);
        Dispatcher.sendTo(new PacketClientData(ClientData.SETTING, AccessType.SET, nbtTagCompound), this.entity);
    }

    @Override
    public void getSetting(String key, Consumer<Object> callback) {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("key", key);
        UUID uniqueId = UUID.randomUUID();
        PacketClientData.сallBack.put(uniqueId, callback);
        Dispatcher.sendTo(new PacketClientData(data, ClientData.SETTING, AccessType.GET_WITH_DATA, uniqueId), this.getMinecraftPlayer());
    }

    @Override
    public void kick(String reason) {
        this.entity.connection.disconnect(new TextComponentString(reason));
    }

    @Override
    public ScriptVector getChasingPos() {
        EntityPlayerMP player = this.entity;
        return new ScriptVector(player.chasingPosX, player.chasingPosY, player.chasingPosZ);
    }

    @Override
    public void setPitchClamp(float min, float max) {
        EntityPlayer player = this.entity;
        Location location = Location.get(player);
        location.setPitchClamping(min, max);
        Dispatcher.sendTo(new PacketCapability(location.serializeNBT(), CapabilityTypes.LOCATION), (EntityPlayerMP) player);
    }

    @Override
    public void resetPitchClamp() {
        EntityPlayer player = this.entity;
        Location location = Location.get(player);
        location.setPitchClamping(-90, 90);
        Dispatcher.sendTo(new PacketCapability(location.serializeNBT(), CapabilityTypes.LOCATION), (EntityPlayerMP) player);
    }

    @Override
    public void setYawClamp(float min, float max) {
        EntityPlayer player = this.entity;
        Location location = Location.get(player);
        location.setYawClamping(min, max);
        Dispatcher.sendTo(new PacketCapability(location.serializeNBT(), CapabilityTypes.LOCATION), (EntityPlayerMP) player);
    }

    @Override
    public void resetYawClamp() {
        EntityPlayer player = this.entity;
        Location location = Location.get(player);
        location.setYawClamping(-1080, 1080);
        Dispatcher.sendTo(new PacketCapability(location.serializeNBT(), CapabilityTypes.LOCATION), (EntityPlayerMP) player);
    }

    @Override
    public int getGameMode() {
        return this.entity.interactionManager.getGameType().getID();
    }

    @Override
    public void setGameMode(int gameMode) {
        GameType type = GameType.getByID(gameMode);

        if (type.getID() >= 0) {
            this.entity.setGameType(type);
        }
    }

    @Override
    public IScriptInventory getInventory() {
        if (this.inventory == null) {
            this.inventory = new ScriptInventory(this.entity.inventory);
        }

        return this.inventory;
    }

    @Override
    public IScriptInventory getEnderChest() {
        if (this.enderChest == null) {
            this.enderChest = new ScriptInventory(this.entity.getInventoryEnderChest());
        }

        return this.enderChest;
    }

    @Override
    public void executeCommand(String command) {
        this.entity.world.getMinecraftServer().getCommandManager().executeCommand(this.entity, command);
    }

    @Override
    public void setSpawnPoint(double x, double y, double z) {
        this.entity.setSpawnPoint(new BlockPos(x, y, z), true);
    }

    @Override
    public ScriptVector getSpawnPoint() {
        BlockPos pos = this.entity.getBedLocation(this.entity.dimension);

        if (pos == null) {
            pos = this.entity.world.getSpawnPoint();
        }

        return new ScriptVector(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean isFlying() {
        return this.entity.capabilities.isFlying;
    }

    @Override
    public float getWalkSpeed() {
        return this.entity.capabilities.getWalkSpeed();
    }

    @Override
    public void setFlyingEnabled(boolean enabled) {
        this.entity.capabilities.allowFlying = enabled;
        this.entity.sendPlayerAbilities();
    }

    @Override
    public float getFlySpeed() {
        return this.entity.capabilities.getFlySpeed();
    }

    @Override
    public void setFlySpeed(float speed) {
        this.entity.capabilities.setFlySpeed(speed);
        this.entity.sendPlayerAbilities();
    }

    @Override
    public void resetFlySpeed() {
        this.setFlySpeed(0.05F);
    }

    @Override
    public void setWalkSpeed(float speed) {
        this.entity.capabilities.setPlayerWalkSpeed(speed);
        this.entity.sendPlayerAbilities();
    }

    @Override
    public void resetWalkSpeed() {
        this.setWalkSpeed(0.1F);
    }

    @Override
    public float getCooldown(IScriptItemStack item) {
        return this.entity.getCooldownTracker().getCooldown(item.getMinecraftItemStack().getItem(), 0);
    }

    @Override
    public float getCooldown(int inventorySlot) {
        return this.getCooldown(this.getInventory().getStack(inventorySlot));
    }

    @Override
    public void setCooldown(IScriptItemStack item, int ticks) {
        this.entity.getCooldownTracker().setCooldown(item.getMinecraftItemStack().getItem(), ticks);
    }

    @Override
    public void setCooldown(int inventorySlot, int ticks) {
        this.setCooldown(this.getInventory().getStack(inventorySlot), ticks);
    }

    @Override
    public void resetCooldown(IScriptItemStack item) {
        this.entity.getCooldownTracker().removeCooldown(item.getMinecraftItemStack().getItem());
    }

    @Override
    public void resetCooldown(int inventorySlot) {
        this.resetCooldown(this.getInventory().getStack(inventorySlot));
    }

    @Override
    public int getHotbarIndex() {
        return this.entity.inventory.currentItem;
    }

    @Override
    public void setHotbarIndex(int slot) {
        if (slot < 0 || slot >= 9) {
            return;
        }

        this.entity.inventory.currentItem = slot;

        this.entity.connection.sendPacket(new SPacketHeldItemChange(slot));
    }

    @Override
    public void send(String message) {
        this.entity.sendMessage(new TextComponentString(message));
    }

    @Override
    public void sendRaw(INBT message) {
        ITextComponent component = ITextComponent.Serializer.fromJsonLenient(message.stringify());

        if (component != null) {
            this.entity.sendMessage(component);
        }
    }

    @Override
    public String getSkin() {
        return "minecraft:skins/" + StringUtils.stripControlCodes(this.getName().toLowerCase());
    }

    @Override
    public void sendTitleDurations(int fadeIn, int idle, int fadeOut) {
        SPacketTitle packet = new SPacketTitle(fadeIn, idle, fadeOut);

        this.getMinecraftPlayer().connection.sendPacket(packet);
    }

    @Override
    public void sendTitle(String title) {
        SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString(title));

        this.getMinecraftPlayer().connection.sendPacket(packet);
    }

    @Override
    public void sendSubtitle(String title) {
        SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(title));

        this.getMinecraftPlayer().connection.sendPacket(packet);
    }

    @Override
    public void sendActionBar(String title) {
        SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString(title));

        this.getMinecraftPlayer().connection.sendPacket(packet);
    }

    /* XP methods */

    @Override
    public void setXp(int level, int points) {
        this.entity.addExperienceLevel(-this.getXpLevel() - 1);
        this.entity.addExperienceLevel(level);
        this.entity.addExperience(points);
    }

    @Override
    public void addXp(int points) {
        this.entity.addExperience(points);
    }

    @Override
    public int getXpLevel() {
        return this.entity.experienceLevel;
    }

    @Override
    public int getXpPoints() {
        return (int) (this.entity.experience * this.entity.xpBarCap());
    }

    @Override
    public void setHunger(int value) {
        this.entity.getFoodStats().setFoodLevel(value);
    }

    @Override
    public int getHunger() {
        return this.entity.getFoodStats().getFoodLevel();
    }

    @Override
    public void setSaturation(float value) {
        this.entity.getFoodStats().setFoodSaturationLevel(value);
    }

    @Override
    public float getSaturation() {
        return this.entity.getFoodStats().getSaturationLevel();
    }

    /* Sounds */

    @Override
    public void playSound(String event, double x, double y, double z, float volume, float pitch) {
        WorldUtils.playSound(this.entity, event, x, y, z, volume, pitch);
    }

    @Override
    public void playSound(String event, String soundCategory, double x, double y, double z, float volume, float pitch) {
        WorldUtils.playSound(this.entity, event, soundCategory, x, y, z, volume, pitch);
    }

    @Override
    public void playSound(String event, String soundCategory, double x, double y, double z) {
        WorldUtils.playSound(this.entity, event, soundCategory, x, y, z, 1F, 1F);
    }

    @Override
    public void stopSound(String event, String category) {
        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());

        packetbuffer.writeString(category);
        packetbuffer.writeString(event);

        this.entity.connection.sendPacket(new SPacketCustomPayload("MC|StopSound", packetbuffer));
    }

    @Override
    public void playStaticSound(String event, float volume, float pitch) {
        this.playStaticSound(event, "master", volume, pitch);
    }

    @Override
    public void playStaticSound(String event, String soundCategory, float volume, float pitch) {
        Dispatcher.sendTo(new PacketSound(event, soundCategory, volume, pitch), this.entity);
    }

    /* Mappet stuff */

    @Override
    public AbstractMorph getMorph() {
        IMorphing cap = Morphing.get(this.entity);

        if (cap != null) {
            return cap.getCurrentMorph();
        }

        return super.getMorph();
    }

    @Override
    public boolean setMorph(AbstractMorph morph) {
        if (morph == null) {
            MorphAPI.demorph(this.entity);
        } else {
            MorphAPI.morph(this.entity, morph, true);
        }

        return true;
    }

    @Override
    public boolean openUI(IMappetUIBuilder in, boolean defaultData) {
        if (!(in instanceof MappetUIBuilder)) {
            return false;
        }

        MappetUIBuilder builder = (MappetUIBuilder) in;

        ICharacter character = Character.get(this.entity);
        boolean noContext = character.getUIContext() == null;

        if (!noContext) {
            character.getUIContext().close();
        }

        UI ui = builder.getUI();
        UIContext context = new UIContext(ui, this.entity, builder.getScript(), builder.getFunction());

        character.setUIContext(context);
        Dispatcher.sendTo(new PacketUI(ui), this.getMinecraftPlayer());

        if (defaultData) {
            context.populateDefaultData();
        }

        context.clearChanges();

        return noContext;
    }

    @Override
    public void closeUI() {
        Dispatcher.sendTo(new PacketCloseUI(), this.getMinecraftPlayer());
    }

    @Override
    public IMappetUIContext getUIContext() {
        ICharacter character = Character.get(this.entity);
        UIContext context = character.getUIContext();

        return context == null ? null : new MappetUIContext(context);
    }


    /* HUD scenes API */

    @Override
    public boolean setupHUD(String id) {
        return Character.get(this.entity).setupHUD(id, true);
    }

    @Override
    public void changeHUDMorph(String id, int index, AbstractMorph morph) {
        if (morph == null)
            return;
        Character.get(this.entity).changeHUDMorph(id, index, MorphUtils.toNBT(morph));
    }

    @Override
    public void changeHUDMorph(String id, int index, INBTCompound morph) {
        if (morph == null)
            return;
        Character.get(this.entity).changeHUDMorph(id, index, morph.getNBTTagCompound());
    }

    @Override
    public void closeHUD(String id) {
        Character.get(this.entity).closeHUD(id);
    }

    @Override
    public void closeAllHUDs() {
        Character.get(this.entity).closeAllHUDs();
    }

    @Override
    public INBTCompound getDisplayedHUDs() {
        ICharacter character = Character.get(this.entity);
        NBTTagCompound tag = ((Character) character).getDisplayedHUDsTag();
        return new ScriptNBTCompound(tag);
    }

    @Override
    public INBTCompound getGlobalDisplayedHUDs() {
        ICharacter character = Character.get(this.entity);
        NBTTagCompound tag = ((Character) character).getGlobalDisplayedHUDsTag();
        return new ScriptNBTCompound(tag);
    }

    /* Aperture API */

    @Override
    public void playScene(String sceneName) {
        if (Loader.isModLoaded("aperture")) {
            this.playApertureScene(sceneName, true);
        }
    }

    @Override
    public void stopScene() {
        if (Loader.isModLoaded("aperture")) {
            this.playApertureScene("", false);
        }
    }

    @Optional.Method(modid = "aperture")
    private void playApertureScene(String sceneName, boolean toPlay) {
        mchorse.aperture.network.Dispatcher.sendTo(new PacketCameraState(sceneName, toPlay), this.entity);
    }
}