package magmaout.mappet.events;

import magmaout.mappet.CommonProxy;
import magmaout.mappet.Mappet;
import magmaout.mappet.api.triggers.Trigger;
import magmaout.mappet.api.utils.DataContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.MouseEvent;

public class PlayerInputEventHandler {
    public void onKeyboardEvent(NBTTagCompound data, EntityPlayerMP player) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerKeyboard;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("code", data.getInteger("code"));
        context.getValues().put("state", data.getBoolean("state"));
        context.getValues().put("name", data.getString("name"));

        trigger.trigger(context);
    }

    public void onMouseEvent(NBTTagCompound data, EntityPlayerMP player) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerMouse;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("button", data.getInteger("button"));
        context.set("x", data.getInteger("x"));
        context.set("y", data.getInteger("y"));
        context.set("dwheel", data.getInteger("dwheel"));
        context.set("dx", data.getInteger("dx"));
        context.set("dy", data.getInteger("dy"));
        context.getValues().put("state", data.getBoolean("state"));

        CommonProxy.eventHandler.trigger(new MouseEvent() , trigger, context);
    }

    private boolean shouldCancelTrigger(Trigger trigger) {
        return trigger == null || trigger.isEmpty();
    }
}
