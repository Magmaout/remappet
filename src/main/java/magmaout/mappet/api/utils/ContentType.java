package magmaout.mappet.api.utils;

import magmaout.mappet.Mappet;
import magmaout.mappet.api.utils.manager.IManager;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Mappet content types
 */
public enum ContentType implements IContentType {
    NPC() {
        @Override
        public IManager<? extends AbstractData> getManager() {
            return Mappet.npcs;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
            return dashboard.npc;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IKey getPickLabel() {
            return IKey.lang("mappet.gui.overlays.npc");
        }

        @Override
        public String getName() {
            return "NPC";
        }
    },
    SCRIPTS() {
        @Override
        public IManager<? extends AbstractData> getManager() {
            return Mappet.scripts;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
            return dashboard.script;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IKey getPickLabel() {
            return IKey.lang("mappet.gui.overlays.script");
        }

        @Override
        public String getName() {
            return "SCRIPTS";
        }
    },
    HUDS() {
        @Override
        public IManager<? extends AbstractData> getManager() {
            return Mappet.huds;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
            return dashboard.hud;
        }

        @Override
        public IKey getPickLabel() {
            return IKey.lang("mappet.gui.overlays.hud");
        }

        @Override
        public String getName() {
            return "HUDS";
        }
    };
}
