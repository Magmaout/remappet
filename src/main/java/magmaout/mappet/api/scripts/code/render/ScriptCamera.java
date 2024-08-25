package magmaout.mappet.api.scripts.code.render;

import magmaout.mappet.api.scripts.user.data.ScriptVector;
import magmaout.mappet.api.scripts.user.render.IScriptCamera;
import magmaout.mappet.utils.CapabilityTypes;
import magmaout.mappet.capabilities.camera.Camera;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.scripts.PacketCapability;
import net.minecraft.entity.player.EntityPlayerMP;

public class ScriptCamera implements IScriptCamera {
    private final EntityPlayerMP player;
    private final Camera camera;
    public ScriptCamera(EntityPlayerMP player) {
        this.player = player;
        this.camera = Camera.get(player);
    }

    @Override
    public void resetAll() {
        this.camera.setStartRotations(0, 0, 0);
        this.camera.setStartPosition(0, 0, 0);
        this.camera.setStartScaling(1, 1, 1);

        this.camera.setEndRotations(0, 0, 0);
        this.camera.setEndPosition(0, 0, 0);
        this.camera.setEndScaling(1, 1, 1);

        this.camera.setRotations(0, 0,0);
        this.camera.setPosition(0, 0,0);
        this.camera.setScaling(1, 1,1);

        this.camera.setDurationRotations(0L);
        this.camera.setDurationPosition(0L);
        this.camera.setDurationScaling(0L);
        sendToCapability();
    }

    @Override
    public ScriptVector getRotations() {
        return new ScriptVector(this.camera.rotations.x, this.camera.rotations.y, this.camera.rotations.z);
    }

    @Override
    public void setRotations(float x, float y, float z) {
        this.camera.setStartRotations(x, y, z);
        this.camera.setEndRotations(x, y, z);
        this.camera.setRotations(x, y, z);
        this.camera.setDurationRotations(0L);
        sendToCapability();
    }

    @Override
    public ScriptVector getPosition() {
        return new ScriptVector(this.camera.position.x, this.camera.position.y, this.camera.position.z);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.camera.setStartPosition(x, y, z);
        this.camera.setEndPosition(x, y, z);
        this.camera.setPosition(x, y, z);
        this.camera.setDurationPosition(0L);
        sendToCapability();
    }

    @Override
    public ScriptVector getScaling() {
        return new ScriptVector(this.camera.scaling.x, this.camera.scaling.y, this.camera.scaling.z);
    }
    
    @Override
    public void setScaling(float x, float y, float z) {
        this.camera.setStartScaling(x, y, z);
        this.camera.setEndScaling(x, y, z);
        this.camera.setScaling(x, y, z);
        this.camera.setDurationScaling(0L);
        sendToCapability();
    }

    @Override
    public void resetAllTo(String interpolation, int duration) {
        this.camera.setStartRotations(this.camera.rotations.x, this.camera.rotations.y, this.camera.rotations.z);
        this.camera.setStartPosition(this.camera.position.x, this.camera.position.y, this.camera.position.z);
        this.camera.setStartScaling(this.camera.scaling.x, this.camera.scaling.y, this.camera.scaling.z);

        this.camera.setEndRotations(0, 0, 0);
        this.camera.setEndPosition(0, 0, 0);
        this.camera.setEndScaling(1, 1, 1);

        this.camera.setInterpRotations(interpolation);
        this.camera.setInterpPosition(interpolation);
        this.camera.setInterpScaling(interpolation);

        this.camera.setDurationRotations(duration * 50L);
        this.camera.setDurationPosition(duration * 50L);
        this.camera.setDurationScaling(duration * 50L);
        sendToCapability();
    }

    @Override
    public void rotateTo(String interpolation, int duration, float x, float y, float z) {
        this.camera.setStartRotations(this.camera.rotations.x, this.camera.rotations.y, this.camera.rotations.z);
        this.camera.setEndRotations(x, y, z);
        this.camera.setInterpRotations(interpolation);
        this.camera.setDurationRotations(duration * 50L);
        sendToCapability();
    }

    @Override
    public void moveTo(String interpolation, int duration, float x, float y, float z) {
        this.camera.setStartPosition(this.camera.position.x, this.camera.position.y, this.camera.position.z);
        this.camera.setEndPosition(x, y, z);
        this.camera.setInterpPosition(interpolation);
        this.camera.setDurationPosition(duration * 50L);
        sendToCapability();
    }

    @Override
    public void scaleTo(String interpolation, int duration, float x, float y, float z) {
        this.camera.setStartScaling(this.camera.scaling.x, this.camera.scaling.y, this.camera.scaling.z);
        this.camera.setEndScaling(x, y, z);
        this.camera.setInterpScaling(interpolation);
        this.camera.setDurationScaling(duration * 50L);
        sendToCapability();
    }

    private void sendToCapability() {
        Dispatcher.sendTo(new PacketCapability(this.camera.serializeNBT(), CapabilityTypes.CAMERA), this.player);
    }
}