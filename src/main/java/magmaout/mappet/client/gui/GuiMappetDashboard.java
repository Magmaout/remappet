package magmaout.mappet.client.gui;

import magmaout.mappet.client.RenderingHandler;
import magmaout.mappet.client.gui.panels.*;
import magmaout.mappet.utils.MPIcons;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.content.PacketContentExit;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.mclib.GuiAbstractDashboard;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanels;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiMappetDashboard extends GuiAbstractDashboard {
    public static GuiMappetDashboard dashboard;

    public GuiServerSettingsPanel settings;
    public GuiRegionPanel region;
    public GuiConditionModelPanel conditionModel;
    public GuiNpcPanel npc;

    public GuiScriptPanel script;
    public GuiHUDScenePanel hud;
    public GuiLogPanel logs;

    public GuiCreativeMorphsMenu morphs;

    public static GuiMappetDashboard get(Minecraft mc) {
        if (dashboard == null) {
            dashboard = new GuiMappetDashboard(mc);
        }

        return dashboard;
    }

    public GuiMappetDashboard(Minecraft mc) {
        super(mc);
    }

    @Override
    protected GuiDashboardPanels createDashboardPanels(Minecraft mc) {
        return new GuiDashboardPanels(mc);
    }

    public GuiCreativeMorphsMenu getMorphMenu() {
        if (this.morphs == null) {
            this.morphs = new GuiCreativeMorphsMenu(Minecraft.getMinecraft(), null).pickUponExit();
        }

        return this.morphs;
    }

    public void openMorphMenu(GuiElement parent, boolean editing, AbstractMorph morph, Consumer<AbstractMorph> callback) {
        GuiBase.getCurrent().unfocus();

        GuiCreativeMorphsMenu menu = this.getMorphMenu();

        menu.callback = callback;
        menu.flex().reset().relative(parent).wh(1F, 1F);
        menu.resize();
        menu.setSelected(morph);

        if (editing) {
            menu.enterEditMorph();
        }

        menu.removeFromParent();
        parent.add(menu);
    }

    @Override
    protected void registerPanels(Minecraft mc) {
        this.settings = new GuiServerSettingsPanel(mc, this);
        this.region = new GuiRegionPanel(mc, this);
        this.conditionModel = new GuiConditionModelPanel(mc, this);
        this.npc = new GuiNpcPanel(mc, this);
        this.script = new GuiScriptPanel(mc, this);
        this.hud = new GuiHUDScenePanel(mc, this);
        this.logs = new GuiLogPanel(mc, this);

        this.panels.registerPanel(this.settings, IKey.lang("mappet.gui.panels.settings"), Icons.GEAR);
        this.panels.registerPanel(this.region, IKey.lang("mappet.gui.panels.regions"), Icons.FULLSCREEN);
        this.panels.registerPanel(this.conditionModel, IKey.lang("mappet.gui.panels.condition_models"), Icons.BLOCK);
        this.panels.registerPanel(this.npc, IKey.lang("mappet.gui.panels.npcs"), Icons.PROCESSOR);
        this.panels.registerPanel(this.script, IKey.lang("mappet.gui.panels.scripts"), MMIcons.PROPERTIES);
        this.panels.registerPanel(this.hud, IKey.lang("mappet.gui.panels.huds"), Icons.POSE);
        this.panels.registerPanel(this.logs, IKey.lang("mappet.gui.panels.logs"), MPIcons.REPL);

        this.panels.setPanel(this.settings);
    }

    @Override
    protected void closeScreen() {
        super.closeScreen();

        Dispatcher.sendToServer(new PacketContentExit());
        RenderingHandler.currentStage = null;
    }
}