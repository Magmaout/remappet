package magmaout.mappet.api.scripts.code.render;

import magmaout.mappet.api.scripts.user.data.ScriptVector;
import magmaout.mappet.api.scripts.user.render.IScriptHand;
import magmaout.mappet.utils.CapabilityTypes;
import magmaout.mappet.capabilities.hand.Hand;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.scripts.PacketCapability;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class ScriptHand implements IScriptHand {
    private final EntityPlayerMP player;
    private final Integer side;
    private final Hand hand;
    public ScriptHand(EntityPlayerMP player, int side) {
        this.player = player;
        this.side = side;
        this.hand = Hand.get(player);
    }

    @Override
    public void setSkinPath(String path) {
        this.hand.setSkinPath(path);
        this.sendToCapability();
    }

    @Override
    public void setSkinType(String type) {
        if (type.contains("alex") ||
            type.contains("slim"))
        { this.hand.setSkinType("slim"); }
        else if (type.contains("steve") ||
            type.contains("fred") ||
            type.contains("default"))
        { this.hand.setSkinType("default"); }
        else { this.hand.setSkinType(""); }
        this.sendToCapability();
    }

    @Override
    public String getSkinPath() {
        return hand.skinPath.toString();
    }

    @Override
    public String getSkinType() {
        return hand.skinType;
    }


    @Override
    public void resetAll() {
        if (this.side <= 0) {
            this.hand.setMainStartRotations(0, 0, 0);
            this.hand.setMainStartPosition(0, 0, 0);
            this.hand.setMainEndRotations(0, 0, 0);
            this.hand.setMainEndPosition(0, 0, 0);
            this.hand.setMainRotations(0, 0, 0);
            this.hand.setMainPosition(0, 0, 0);
            this.hand.setMainDurationRotations(0L);
            this.hand.setMainDurationPosition(0L);
            this.hand.setMainRender(true);
        } else {
            this.hand.setOffStartRotations(0, 0, 0);
            this.hand.setOffStartPosition(0, 0, 0);
            this.hand.setOffEndRotations(0, 0, 0);
            this.hand.setOffEndPosition(0, 0, 0);
            this.hand.setOffRotations(0, 0, 0);
            this.hand.setOffPosition(0, 0, 0);
            this.hand.setOffDurationRotations(0L);
            this.hand.setOffDurationPosition(0L);
            this.hand.setOffRender(true);
        }
        sendToCapability();
    }

    @Override
    public void resetAllTo(String interpolation, int duration) {
        if (this.side <= 0) {
            this.hand.setMainStartRotations(this.hand.mainRotations.x, this.hand.mainRotations.y, this.hand.mainRotations.z);
            this.hand.setMainStartPosition(this.hand.mainPosition.x, this.hand.mainPosition.y, this.hand.mainPosition.z);
            this.hand.setMainEndRotations(0, 0, 0);
            this.hand.setMainEndPosition(0, 0, 0);
            this.hand.setMainDurationRotations(duration * 50L);
            this.hand.setMainDurationPosition(duration * 50L);
            this.hand.setMainInterpRotations(interpolation);
            this.hand.setMainInterpPosition(interpolation);
            this.hand.setMainRender(true);
        } else {
            this.hand.setOffStartRotations(this.hand.offRotations.x, this.hand.offRotations.y, this.hand.offRotations.z);
            this.hand.setOffStartPosition(this.hand.offPosition.x, this.hand.offPosition.y, this.hand.offPosition.z);
            this.hand.setOffEndRotations(0, 0, 0);
            this.hand.setOffEndPosition(0, 0, 0);
            this.hand.setOffDurationRotations(duration * 50L);
            this.hand.setOffDurationPosition(duration * 50L);
            this.hand.setOffInterpRotations(interpolation);
            this.hand.setOffInterpPosition(interpolation);
            this.hand.setOffRender(true);
        }
        sendToCapability();
    }

    @Override
    public ScriptVector getRotations() {
        if (this.side <= 0) {
            return new ScriptVector(this.hand.mainRotations.x, this.hand.mainRotations.y, this.hand.mainRotations.z);
        } else {
            return new ScriptVector(this.hand.offRotations.x, this.hand.offRotations.y, this.hand.offRotations.z);
        }
    }

    @Override
    public void setRotations(double x, double y, double z) {
        if (this.side <= 0) {
            this.hand.setMainStartRotations(x, y, z);
            this.hand.setMainEndRotations(x, y, z);
            this.hand.setMainRotations(x, y, z);
            this.hand.setMainDurationRotations(0L);
        } else {
            this.hand.setOffStartRotations(x, y, z);
            this.hand.setOffEndRotations(x, y, z);
            this.hand.setOffRotations(x, y, z);
            this.hand.setOffDurationRotations(0L);
        }
        sendToCapability();
    }

    @Override
    public ScriptVector getPosition() {
        if (this.side <= 0) {
            return new ScriptVector(this.hand.mainPosition.x, this.hand.mainPosition.y, this.hand.mainPosition.z);
        } else {
            return new ScriptVector(this.hand.offPosition.x, this.hand.offPosition.y, this.hand.offPosition.z);
        }
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (this.side <= 0) {
            this.hand.setMainStartPosition(x, y, z);
            this.hand.setMainEndPosition(x, y, z);
            this.hand.setMainPosition(x, y, z);
            this.hand.setMainDurationPosition(0L);
        } else {
            this.hand.setOffStartPosition(x, y, z);
            this.hand.setOffEndPosition(x, y, z);
            this.hand.setOffPosition(x, y, z);
            this.hand.setOffDurationPosition(0L);
        }
        sendToCapability();
    }

    @Override
    public void rotateTo(String interpolation, int duration, double x, double y, double z) {
        if (this.side <= 0) {
            this.hand.setMainStartRotations(this.hand.mainRotations.x, this.hand.mainRotations.y, this.hand.mainRotations.z);
            this.hand.setMainEndRotations(x, y, z);
            this.hand.setMainInterpRotations(interpolation);
            this.hand.setMainDurationRotations(duration * 50L);
        } else {
            this.hand.setOffStartRotations(this.hand.offRotations.x, this.hand.offRotations.y, this.hand.offRotations.z);
            this.hand.setOffEndRotations(x, y, z);
            this.hand.setOffInterpRotations(interpolation);
            this.hand.setOffDurationRotations(duration * 50L);
        }
        sendToCapability();
    }

    @Override
    public void moveTo(String interpolation, int duration, double x, double y, double z) {
        if (this.side <= 0) {
            this.hand.setMainStartPosition(this.hand.mainPosition.x, this.hand.mainPosition.y, this.hand.mainPosition.z);
            this.hand.setMainEndPosition(x, y, z);
            this.hand.setMainInterpPosition(interpolation);
            this.hand.setMainDurationPosition(duration * 50L);
        } else {
            this.hand.setOffStartPosition(this.hand.offPosition.x, this.hand.offPosition.y, this.hand.offPosition.z);
            this.hand.setOffEndPosition(x, y, z);
            this.hand.setOffInterpPosition(interpolation);
            this.hand.setOffDurationPosition(duration * 50L);
        }
        sendToCapability();
    }

    @Override
    public void setRender(boolean render) {
        if (this.side <= 0) {
            this.hand.setMainRender(render);
        } else {
            this.hand.setOffRender(render);
        }
        sendToCapability();
    }

    @Override
    public boolean isRender() {
        if (this.side <= 0) {
            return this.hand.mainRender;
        } else {
            return this.hand.offRender;
        }
    }

    private void sendToCapability() {
        Dispatcher.sendTo(new PacketCapability(this.hand.serializeNBT(), CapabilityTypes.HAND), this.player);
    }
}