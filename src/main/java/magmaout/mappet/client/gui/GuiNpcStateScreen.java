package magmaout.mappet.client.gui;

import magmaout.mappet.api.npcs.NpcState;
import magmaout.mappet.client.gui.npc.GuiNpcEditor;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.npc.PacketNpcState;
import mchorse.mclib.client.gui.framework.GuiBase;
import net.minecraft.client.Minecraft;

public class GuiNpcStateScreen extends GuiBase {
    public GuiNpcEditor editor;

    private int entityId;

    public GuiNpcStateScreen(Minecraft mc, int entityId, NpcState state) {
        this.entityId = entityId;

        this.editor = new GuiNpcEditor(mc, true);
        this.editor.flex().relative(this.root).w(1F).h(1F);

        this.root.add(this.editor);

        this.editor.set(state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void closeScreen() {
        super.closeScreen();

        Dispatcher.sendToServer(new PacketNpcState(this.entityId, this.editor.get().serializeNBT()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}