package magmaout.mappet;

import magmaout.mappet.api.data.DataManager;
import magmaout.mappet.api.expressions.ExpressionManager;
import magmaout.mappet.api.huds.HUDManager;
import magmaout.mappet.api.misc.ServerSettings;
import magmaout.mappet.api.npcs.NpcManager;
import magmaout.mappet.api.schematics.SchematicManager;
import magmaout.mappet.api.scripts.ScriptManager;
import magmaout.mappet.api.states.States;
import magmaout.mappet.api.utils.DataContext;
import magmaout.mappet.api.utils.logs.MappetLogger;
import magmaout.mappet.blocks.BlockConditionModel;
import magmaout.mappet.blocks.BlockEmitter;
import magmaout.mappet.blocks.BlockRegion;
import magmaout.mappet.blocks.BlockTrigger;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.commands.CommandMappet;
import magmaout.mappet.utils.ScriptUtils;
import magmaout.mappet.utils.ValueButtons;
import magmaout.mappet.utils.ValueSyntaxStyle;
import mchorse.mclib.McLib;
import mchorse.mclib.commands.utils.L10n;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.events.RemoveDashboardPanels;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.logging.Handler;

/**
 * Mappet mod
 * <p>
 * Custom game map toolset mod
 */
@Mod(
        modid = Mappet.MOD_ID,
        name = "Mappet",
        version = Mappet.VERSION,
        dependencies =
                "required-after:mclib@[@MCLIB@,);" +
                        "required-after:metamorph@[@METAMORPH@,);" +
                        "after:blockbuster@[@BLOCKBUSTER@,);" +
                        "after:aperture@[@APERTURE@,);",
        updateJSON = "https://raw.githubusercontent.com/mchorse/mappet/master/version.json"
)

public final class Mappet {
    public static final String MOD_ID = "mappet";

    public static final String VERSION = "@MAPPET@";

    @Mod.Instance
    public static Mappet instance;

    @SidedProxy(serverSide = "magmaout.mappet.CommonProxy", clientSide = "magmaout.mappet.ClientProxy")
    public static CommonProxy proxy;

    public static L10n l10n = new L10n(MOD_ID);

    public static final EventBus EVENT_BUS = new EventBus();

    public static MappetLogger logger;

    /* Content */
    public static Item npcTool;

    public static BlockEmitter emitterBlock;

    public static BlockTrigger triggerBlock;

    public static BlockRegion regionBlock;

    public static BlockConditionModel conditionModelBlock;

    public static CreativeTabs creativeTab = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(emitterBlock);
        }
    };

    /* Server side data */
    public static ServerSettings settings;

    public static States states;

    public static SchematicManager schematics;


    public static ExpressionManager expressions;

    public static NpcManager npcs;

    public static DataManager data;

    public static ScriptManager scripts;

    public static HUDManager huds;

    /* Configuration */
    public static ValueBoolean generalDataCaching;

    public static ValueBoolean loadCustomSoundsOnLogin;

    public static ValueBoolean npcsPeacefulDamage;

    public static ValueBoolean npcsToolOnlyOP;

    public static ValueBoolean npcsToolOnlyCreative;

    public static ValueBoolean dashboardOnlyCreative;

    public static ValueBoolean enableForgeTriggers;

    public static ValueSyntaxStyle scriptEditorSyntaxStyle;

    public static ValueBoolean scriptEditorSounds;

    public static ValueBoolean scriptUIDebug;

    public static ValueBoolean scriptDocsNewStructure;

    public static ValueBoolean eventUseServerForCommands;

    public Mappet() {
        MinecraftForge.EVENT_BUS.register(new RegisterHandler());
    }

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event) {
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        builder.category("general").register(new ValueButtons("buttons").clientSide());
        generalDataCaching = builder.getBoolean("data_caching", true);
        enableForgeTriggers = builder.getBoolean("enable_forge_triggers", false);
        loadCustomSoundsOnLogin = builder.getBoolean("load_custom_sounds_on_login", true);
        eventUseServerForCommands = builder.getBoolean("use_server_for_commands", false);

        npcsPeacefulDamage = builder.category("npc").getBoolean("peaceful_damage", true);
        npcsToolOnlyOP = builder.getBoolean("tool_only_op", true);
        npcsToolOnlyCreative = builder.getBoolean("tool_only_creative", false);
        dashboardOnlyCreative = builder.getBoolean("dashboard_only_creative", false);
        builder.getCategory().markClientSide();

        builder.category("script_editor").register(scriptEditorSyntaxStyle = new ValueSyntaxStyle("syntax_style"));
        scriptEditorSounds = builder.getBoolean("sounds", true);
        scriptUIDebug = builder.getBoolean("ui_debug", false);
        scriptDocsNewStructure = builder.getBoolean("docs_new_structure", false);
        builder.getCategory().markClientSide();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDashboardPanelsRemove(RemoveDashboardPanels event) {
        GuiMappetDashboard.dashboard = null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        McLib.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        /* Register command */
        event.registerServerCommand(new CommandMappet());

        /* Initiate managers and global state*/
        File mappetWorldFolder = new File(DimensionManager.getCurrentSaveRootDirectory(), MOD_ID);

        mappetWorldFolder.mkdirs();

        if (logger != null) {
            Handler[] handlers = logger.getHandlers();

            for (Handler handler : handlers) {
                handler.close();
                logger.removeHandler(handler);
            }

            logger = null;
        }

        logger = new MappetLogger(MOD_ID, mappetWorldFolder);

        settings = new ServerSettings(new File(mappetWorldFolder, "settings.json"));
        settings.load();
        states = new States(new File(mappetWorldFolder, "states.json"));
        states.load();

        schematics = new SchematicManager(new File(mappetWorldFolder, "schematics"));
        expressions = new ExpressionManager();
        npcs = new NpcManager(new File(mappetWorldFolder, "npcs"));
        data = new DataManager(new File(mappetWorldFolder, "data"));
        scripts = new ScriptManager(new File(mappetWorldFolder, "scripts"));
        huds = new HUDManager(new File(mappetWorldFolder, "huds"));

        /* Initiate */
        if (!settings.serverLoad.isEmpty()) {
            settings.serverLoad.trigger(new DataContext(event.getServer()));
        }

        ScriptUtils.initiateScriptEngines();
        scripts.initiateAllScripts();

        EventHandler.getRegisteredEvents();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        if (settings != null) {
            settings.save();
            settings = null;
            states.save();
            states = null;

            expressions = null;
            npcs = null;
            data = null;
            scripts = null;
            huds = null;
        }

        CommonProxy.eventHandler.reset();
    }
}