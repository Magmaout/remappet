package magmaout.mappet.client;

import magmaout.mappet.api.misc.hotkeys.TriggerHotkey;
import magmaout.mappet.api.scripts.Script;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.scripts.scriptedItem.GuiScriptedItemScreen;
import magmaout.mappet.Mappet;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.*;

/**
 * Keyboard handler
 * <p>
 * This class is responsible for handling keyboard input (i.e. key
 * presses) and storing keybindings associated with this mod.
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler {
    public static final Set<TriggerHotkey> hotkeys = new HashSet<TriggerHotkey>();
    public static final List<TriggerHotkey> held = new ArrayList<TriggerHotkey>();
    public static boolean clientPlayerJournal;

    public KeyBinding openMappetDashboard;
    public KeyBinding runCurrentScript;
    private KeyBinding openScriptedItem;

    public static void updateHeldKeys() {
        if (held.isEmpty()) {
            return;
        }

        Iterator<TriggerHotkey> it = held.iterator();

        while (it.hasNext()) {
            int keycode = it.next().keycode;

            if (!Keyboard.isKeyDown(keycode)) {
                it.remove();
            }
        }
    }

    public KeyboardHandler() {
        String prefix = "mappet.keys.";

        this.openMappetDashboard = new KeyBinding(prefix + "dashboard", Keyboard.KEY_EQUALS, prefix + "category");
        this.runCurrentScript = new KeyBinding(prefix + "runCurrentScript", Keyboard.KEY_F6, prefix + "category");
        this.openScriptedItem = new KeyBinding(prefix + "scripted_item", Keyboard.KEY_END, prefix + "category");

        ClientRegistry.registerKeyBinding(this.openMappetDashboard);
        ClientRegistry.registerKeyBinding(this.runCurrentScript);
        ClientRegistry.registerKeyBinding(this.openScriptedItem);
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (this.openMappetDashboard.isPressed() && OpHelper.isPlayerOp()) {
            if (Mappet.dashboardOnlyCreative.get()) {
                if (mc.player.capabilities.isCreativeMode) {
                    mc.displayGuiScreen(GuiMappetDashboard.get(mc));
                }
            } else {
                mc.displayGuiScreen(GuiMappetDashboard.get(mc));
            }
        }

        if (this.runCurrentScript.isPressed()) {
            Script script = GuiMappetDashboard.get(mc).script.getData();

            if (script == null) {
                return;
            }

            mc.player.sendChatMessage("/mp script exec " + mc.player.getName() + " " + script.getId());
        }

        if (this.openScriptedItem.isPressed()) {
            ItemStack stack = mc.player.getHeldItemMainhand();

            if (!stack.getItem().equals(Items.AIR)) {
                mc.displayGuiScreen(new GuiScriptedItemScreen(mc, stack));
            }
        }

        if (Keyboard.getEventKeyState()) {
            handleKeys();
        }
    }

    private void handleKeys() {
        int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

        for (TriggerHotkey hotkey : hotkeys) {
            if (hotkey.keycode == key) {

                if (hotkey.toggle) {
                    held.add(hotkey);
                }

                return;
            }
        }
    }
}