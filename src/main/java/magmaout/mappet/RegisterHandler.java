package magmaout.mappet;

import magmaout.mappet.api.huds.HUDManager;
import magmaout.mappet.api.npcs.NpcManager;
import magmaout.mappet.api.schematics.SchematicManager;
import magmaout.mappet.api.scripts.ScriptManager;
import magmaout.mappet.blocks.BlockConditionModel;
import magmaout.mappet.blocks.BlockEmitter;
import magmaout.mappet.blocks.BlockRegion;
import magmaout.mappet.blocks.BlockTrigger;
import magmaout.mappet.client.RenderingHandler;
import magmaout.mappet.items.ItemNpcTool;
import magmaout.mappet.tile.TileConditionModel;
import magmaout.mappet.tile.TileEmitter;
import magmaout.mappet.tile.TileRegion;
import magmaout.mappet.tile.TileTrigger;
import magmaout.mappet.client.KeyboardHandler;
import magmaout.mappet.entities.EntityNpc;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Calendar;

public class RegisterHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!event.isLocal()) {
            Mappet.schematics = new SchematicManager(null);
            Mappet.npcs = new NpcManager(null);
            Mappet.scripts = new ScriptManager(null);
            Mappet.huds = new HUDManager(null);
        }

        KeyboardHandler.clientPlayerJournal = true;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Mappet.npcs = null;
        Mappet.scripts = null;
        Mappet.huds = null;

        KeyboardHandler.hotkeys.clear();
        RenderingHandler.reset();
    }

    @SubscribeEvent
    public void onBlocksRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(Mappet.emitterBlock = new BlockEmitter());
        event.getRegistry().register(Mappet.triggerBlock = new BlockTrigger());
        event.getRegistry().register(Mappet.regionBlock = new BlockRegion());
        event.getRegistry().register(Mappet.conditionModelBlock = new BlockConditionModel());
    }

    @SubscribeEvent
    public void onItemsRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(Mappet.npcTool = new ItemNpcTool()
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "npc_tool"))
                .setUnlocalizedName(Mappet.MOD_ID + ".npc_tool"));

        event.getRegistry().register(new ItemBlock(Mappet.emitterBlock)
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "emitter"))
                .setUnlocalizedName(Mappet.MOD_ID + ".emitter"));

        event.getRegistry().register(new ItemBlock(Mappet.triggerBlock)
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "trigger"))
                .setUnlocalizedName(Mappet.MOD_ID + ".trigger"));

        event.getRegistry().register(new ItemBlock(Mappet.regionBlock)
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "region"))
                .setUnlocalizedName(Mappet.MOD_ID + ".region"));

        event.getRegistry().register(new ItemBlock(Mappet.conditionModelBlock)
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "condition_model"))
                .setUnlocalizedName(Mappet.MOD_ID + ".condition_model"));
    }

    @SubscribeEvent
    public void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityNpc.class)
                .name(Mappet.MOD_ID + ".npc")
                .id(new ResourceLocation(Mappet.MOD_ID, "npc"), 0)
                .tracker(EntityNpc.RENDER_DISTANCE, 3, false)
                .build());

        GameRegistry.registerTileEntity(TileEmitter.class, Mappet.MOD_ID + ":emitter");
        GameRegistry.registerTileEntity(TileTrigger.class, Mappet.MOD_ID + ":trigger");
        GameRegistry.registerTileEntity(TileRegion.class, Mappet.MOD_ID + ":region");
        GameRegistry.registerTileEntity(TileConditionModel.class, Mappet.MOD_ID + ":condition_model");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Mappet.npcTool, 0, getNpcToolTexture());

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Mappet.emitterBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":emitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Mappet.triggerBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":trigger", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Mappet.regionBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":region", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Mappet.conditionModelBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":condition_model", "inventory"));
    }

    public ModelResourceLocation getNpcToolTexture() {
        String postfix = "";
        Calendar calendar = Calendar.getInstance();

        if (isWinter(calendar)) {
            postfix = "_winter";
        }

        if (isChristmas(calendar)) {
            postfix = "_christmas";
        } else if (isEaster(calendar)) {
            postfix = "_easter";
        } else if (isAprilFoolsDay(calendar)) {
            postfix = "_april";
        } else if (isHalloween(calendar)) {
            postfix = "_halloween";
        }

        return new ModelResourceLocation(Mappet.MOD_ID + ":npc_tool" + postfix, "inventory");
    }

    public boolean isChristmas(Calendar calendar) {
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26;
    }

    public boolean isAprilFoolsDay(Calendar calendar) {
        return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) <= 2;
    }

    public boolean isWinter(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);

        return month == Calendar.DECEMBER || month == Calendar.JANUARY || month == Calendar.FEBRUARY;
    }

    public boolean isEaster(Calendar calendar) {
        Calendar easterDate = getEasterDate(calendar.get(Calendar.YEAR));

        return calendar.get(Calendar.MONTH) == easterDate.get(Calendar.MONTH) && calendar.get(Calendar.DATE) == easterDate.get(Calendar.DATE);
    }

    public boolean isHalloween(Calendar calendar) {
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DATE) >= 24;
    }

    public Calendar getEasterDate(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int month = (h + l + 114) / 31;
        int day = (h + l + 114) % 31;

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month - 1, day + 1);

        return calendar;
    }
}