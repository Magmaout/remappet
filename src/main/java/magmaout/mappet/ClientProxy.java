package magmaout.mappet;

import magmaout.mappet.api.utils.IContentType;
import magmaout.mappet.client.RenderingHandler;
import magmaout.mappet.client.SoundPack;
import magmaout.mappet.client.gui.scripts.themes.Themes;
import magmaout.mappet.client.renders.entity.RenderNpc;
import magmaout.mappet.client.renders.tile.TileConditionModelRenderer;
import magmaout.mappet.client.renders.tile.TileRegionRenderer;
import magmaout.mappet.client.renders.tile.TileTriggerRenderer;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.content.PacketContentRequestNames;
import magmaout.mappet.tile.TileConditionModel;
import magmaout.mappet.tile.TileRegion;
import magmaout.mappet.tile.TileTrigger;
import magmaout.mappet.client.KeyboardHandler;
import magmaout.mappet.client.gui.scripts.highlights.Highlighters;
import magmaout.mappet.entities.EntityNpc;
import mchorse.mclib.McLib;
import mchorse.mclib.utils.ReflectionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private static int requestId = 0;
    private static Map<Integer, Consumer<List<String>>> consumers = new HashMap<Integer, Consumer<List<String>>>();

    public static File sounds;

    public static void requestNames(IContentType type, Consumer<List<String>> consumer) {
        consumers.put(requestId, consumer);
        Dispatcher.sendToServer(new PacketContentRequestNames(type, requestId));

        requestId += 1;
    }

    public static void process(List<String> names, int id) {
        Consumer<List<String>> consumer = consumers.remove(id);

        if (consumer != null) {
            consumer.accept(names);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        RenderingHandler handler = new RenderingHandler();

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(handler);
        McLib.EVENT_BUS.register(handler);

        ClientRegistry.bindTileEntitySpecialRenderer(TileTrigger.class, new TileTriggerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRegion.class, new TileRegionRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileConditionModel.class, new TileConditionModelRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityNpc.class, new RenderNpc.Factory());

        ReflectionUtils.registerResourcePack(new SoundPack(sounds = new File(CommonProxy.configFolder, "sounds")));

        Themes.initiate();
        Highlighters.initiate();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }
}