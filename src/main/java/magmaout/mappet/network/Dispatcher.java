package magmaout.mappet.network;

import magmaout.mappet.network.blocks.PacketEditConditionModel;
import magmaout.mappet.network.blocks.PacketEditEmitter;
import magmaout.mappet.network.blocks.PacketEditRegion;
import magmaout.mappet.network.blocks.PacketEditTrigger;
import magmaout.mappet.network.content.*;
import magmaout.mappet.network.huds.PacketHUDMorph;
import magmaout.mappet.network.huds.PacketHUDScene;
import magmaout.mappet.network.items.PacketScriptedItemInfo;
import magmaout.mappet.network.logs.PacketLogs;
import magmaout.mappet.network.logs.PacketRequestLogs;
import magmaout.mappet.network.npc.PacketNpcList;
import magmaout.mappet.network.npc.PacketNpcState;
import magmaout.mappet.network.npc.PacketNpcStateChange;
import magmaout.mappet.network.npc.PacketNpcTool;
import magmaout.mappet.network.scripts.*;
import magmaout.mappet.network.ui.PacketCloseUI;
import magmaout.mappet.network.ui.PacketUI;
import magmaout.mappet.network.ui.PacketUIData;
import magmaout.mappet.network.utils.PacketChangedBoundingBox;
import magmaout.mappet.Mappet;
import magmaout.mappet.network.utils.PacketClientData;
import magmaout.mappet.network.utils.PacketPlayerInputEvent;
import mchorse.mclib.network.AbstractDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network dispatcher
 */
public class Dispatcher {
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(Mappet.MOD_ID) {
        @Override
        public void register() {
            /* Blocks */
            this.register(PacketEditEmitter.class, PacketEditEmitter.ClientHandler.class, Side.CLIENT);
            this.register(PacketEditEmitter.class, PacketEditEmitter.ServerHandler.class, Side.SERVER);

            this.register(PacketEditTrigger.class, PacketEditTrigger.ClientHandler.class, Side.CLIENT);
            this.register(PacketEditTrigger.class, PacketEditTrigger.ServerHandler.class, Side.SERVER);

            this.register(PacketEditRegion.class, PacketEditRegion.ClientHandler.class, Side.CLIENT);
            this.register(PacketEditRegion.class, PacketEditRegion.ServerHandler.class, Side.SERVER);

            this.register(PacketEditConditionModel.class, PacketEditConditionModel.ClientHandler.class, Side.CLIENT);
            this.register(PacketEditConditionModel.class, PacketEditConditionModel.ServerHandler.class, Side.SERVER);

            /* Scripted item */
            this.register(PacketScriptedItemInfo.class, PacketScriptedItemInfo.ClientHandler.class, Side.CLIENT);
            this.register(PacketScriptedItemInfo.class, PacketScriptedItemInfo.ServerHandler.class, Side.SERVER);

            /* Creative editing */
            this.register(PacketContentRequestNames.class, PacketContentRequestNames.ServerHandler.class, Side.SERVER);
            this.register(PacketContentRequestData.class, PacketContentRequestData.ServerHandler.class, Side.SERVER);
            this.register(PacketContentData.class, PacketContentData.ClientHandler.class, Side.CLIENT);
            this.register(PacketContentData.class, PacketContentData.ServerHandler.class, Side.SERVER);
            this.register(PacketContentFolder.class, PacketContentFolder.ServerHandler.class, Side.SERVER);
            this.register(PacketContentNames.class, PacketContentNames.ClientHandler.class, Side.CLIENT);
            this.register(PacketContentExit.class, PacketContentExit.ServerHandler.class, Side.SERVER);

            this.register(PacketServerSettings.class, PacketServerSettings.ClientHandler.class, Side.CLIENT);
            this.register(PacketServerSettings.class, PacketServerSettings.ServerHandler.class, Side.SERVER);
            this.register(PacketRequestServerSettings.class, PacketRequestServerSettings.ServerHandler.class, Side.SERVER);
            this.register(PacketStates.class, PacketStates.ClientHandler.class, Side.CLIENT);
            this.register(PacketStates.class, PacketStates.ServerHandler.class, Side.SERVER);
            this.register(PacketRequestStates.class, PacketRequestStates.ServerHandler.class, Side.SERVER);

            /* NPCs */
            this.register(PacketNpcStateChange.class, PacketNpcStateChange.ClientHandler.class, Side.CLIENT);
            this.register(PacketNpcState.class, PacketNpcState.ClientHandler.class, Side.CLIENT);
            this.register(PacketNpcState.class, PacketNpcState.ServerHandler.class, Side.SERVER);
            this.register(PacketNpcList.class, PacketNpcList.ClientHandler.class, Side.CLIENT);
            this.register(PacketNpcList.class, PacketNpcList.ServerHandler.class, Side.SERVER);
            this.register(PacketNpcTool.class, PacketNpcTool.ServerHandler.class, Side.SERVER);

            /* Scripts */
            this.register(PacketEntityRotations.class, PacketEntityRotations.ClientHandler.class, Side.CLIENT);
            this.register(PacketCapability.class, PacketCapability.ClientHandler.class, Side.CLIENT);
            this.register(PacketCapability.class, PacketCapability.ServerHandler.class, Side.SERVER);
            this.register(PacketClick.class, PacketClick.ServerHandler.class, Side.SERVER);
            this.register(PacketRepl.class, PacketRepl.ClientHandler.class, Side.CLIENT);
            this.register(PacketRepl.class, PacketRepl.ServerHandler.class, Side.SERVER);
            this.register(PacketSound.class, PacketSound.ClientHandler.class, Side.CLIENT);
            this.register(PacketDownloadImage.class, PacketDownloadImage.ClientHandler.class, Side.CLIENT);
            this.register(PacketWorldMorph.class, PacketWorldMorph.ClientHandler.class, Side.CLIENT);

            /* HUD & UI */
            this.register(PacketHUDScene.class, PacketHUDScene.ClientHandler.class, Side.CLIENT);
            this.register(PacketHUDMorph.class, PacketHUDMorph.ClientHandler.class, Side.CLIENT);

            this.register(PacketUI.class, PacketUI.ClientHandlerUI.class, Side.CLIENT);
            this.register(PacketUI.class, PacketUI.ServerHandlerUI.class, Side.SERVER);
            this.register(PacketUIData.class, PacketUIData.ClientHandlerUIData.class, Side.CLIENT);
            this.register(PacketUIData.class, PacketUIData.ServerHandlerUIData.class, Side.SERVER);
            this.register(PacketCloseUI.class, PacketCloseUI.ClientHandlerCloseUI.class, Side.CLIENT);

            /* Logs */
            this.register(PacketClientData.class, PacketClientData.ServerHandler.class, Side.SERVER);
            this.register(PacketClientData.class, PacketClientData.ClientHandler.class, Side.CLIENT);
            this.register(PacketRequestLogs.class, PacketLogs.ServerHandler.class, Side.SERVER);
            this.register(PacketLogs.class, PacketLogs.ClientHandler.class, Side.CLIENT);

            /* Utils */
            this.register(PacketChangedBoundingBox.class, PacketChangedBoundingBox.ClientHandlerChangedBoundingBox.class, Side.CLIENT);
            this.register(PacketPlayerInputEvent.class, PacketPlayerInputEvent.ServerHandler.class, Side.SERVER);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message) {
        EntityTracker tracker = ((WorldServer) entity.world).getEntityTracker();

        for (EntityPlayer player : tracker.getTrackingPlayers(entity)) {
            sendTo(message, (EntityPlayerMP) player);
        }
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message) {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register() {
        DISPATCHER.register();
    }
}