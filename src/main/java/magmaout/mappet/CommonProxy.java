package magmaout.mappet;

import magmaout.mappet.api.conditions.blocks.*;
import magmaout.mappet.api.triggers.blocks.*;
import magmaout.mappet.api.ui.components.*;
import magmaout.mappet.api.utils.factory.IFactory;
import magmaout.mappet.api.utils.factory.MapFactory;
import magmaout.mappet.capabilities.camera.Camera;
import magmaout.mappet.capabilities.camera.CameraStorage;
import magmaout.mappet.capabilities.camera.ICamera;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.capabilities.character.CharacterStorage;
import magmaout.mappet.capabilities.character.ICharacter;
import magmaout.mappet.capabilities.hand.Hand;
import magmaout.mappet.capabilities.hand.HandStorage;
import magmaout.mappet.capabilities.hand.IHand;
import magmaout.mappet.capabilities.location.ILocation;
import magmaout.mappet.capabilities.location.Location;
import magmaout.mappet.capabilities.location.LocationStorage;
import magmaout.mappet.events.*;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.utils.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

/**
 * Common proxy
 */
public class CommonProxy {
    private static IFactory<AbstractConditionBlock> conditionBlocks;
    private static IFactory<AbstractTriggerBlock> triggerBlocks;
    private static IFactory<UIComponent> uiComponents;

    public static IFactory<AbstractConditionBlock> getConditionBlocks() {
        return conditionBlocks;
    }

    public static IFactory<AbstractTriggerBlock> getTriggerBlocks() {
        return triggerBlocks;
    }

    public static IFactory<UIComponent> getUiComponents() {
        return uiComponents;
    }

    /**
     * Client folder where saved selectors and animations are getting
     * stored.
     */
    public static File configFolder;

    public static EventHandler eventHandler;
    public static ScriptedItemEventHandler scriptedItemEventHandler;
    public static PlayerInputEventHandler playerInputEventHandler;

    public void preInit(FMLPreInitializationEvent event) {
        /* Setup config folder path */
        String path = event.getModConfigurationDirectory().getAbsolutePath();

        configFolder = new File(path, Mappet.MOD_ID);
        configFolder.mkdir();

        Dispatcher.register();

        MinecraftForge.EVENT_BUS.register(eventHandler = new EventHandler());
        MinecraftForge.EVENT_BUS.register(scriptedItemEventHandler = new ScriptedItemEventHandler());
        MinecraftForge.EVENT_BUS.register(playerInputEventHandler = new PlayerInputEventHandler());

        GameRegistry.registerEntitySelector(new MappetNpcSelector(), MappetNpcSelector.ARGUMENT_MAPPET_NPC_ID, MappetNpcSelector.ARGUMENT_MAPPET_STATES);

        CapabilityManager.INSTANCE.register(ICharacter.class, new CharacterStorage(), Character::new);
        CapabilityManager.INSTANCE.register(ILocation.class, new LocationStorage(), Location::new);
        CapabilityManager.INSTANCE.register(ICamera.class, new CameraStorage(), Camera::new);
        CapabilityManager.INSTANCE.register(IHand.class, new HandStorage(), Hand::new);
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new MetamorphHandler());
        Mappet.EVENT_BUS.register(eventHandler);

        ScriptUtils.initiateScriptEngines();
        MPIcons.register();
    }


    public void postInit(FMLPostInitializationEvent event) {
        /* Register condition blocks */
        MapFactory<AbstractConditionBlock> conditions = new MapFactory<AbstractConditionBlock>()
                .register("state", StateConditionBlock.class, Colors.STATE)
                .register("item", ItemConditionBlock.class, Colors.CRAFTING)
                .register("world_time", WorldTimeConditionBlock.class, Colors.TIME)
                .register("entity", EntityConditionBlock.class, Colors.ENTITY)
                .register("condition", ConditionConditionBlock.class, Colors.CONDITION)
                .register("morph", MorphConditionBlock.class, Colors.MORPH)
                .register("expression", ExpressionConditionBlock.class, Colors.CANCEL);

        conditionBlocks = conditions;
        Mappet.EVENT_BUS.post(new RegisterConditionBlockEvent(conditions));

        /* Register condition blocks */
        MapFactory<AbstractTriggerBlock> triggers = new MapFactory<AbstractTriggerBlock>()
                .register("command", CommandTriggerBlock.class, Colors.COMMAND)
                .register("sound", SoundTriggerBlock.class, Colors.REPLY)
                .register("script", ScriptTriggerBlock.class, Colors.ENTITY)
                .register("item", ItemTriggerBlock.class, Colors.CRAFTING)
                .register("state", StateTriggerBlock.class, Colors.STATE)
                .register("morph", MorphTriggerBlock.class, Colors.MORPH);

        triggerBlocks = triggers;
        Mappet.EVENT_BUS.post(new RegisterTriggerBlockEvent(triggers));

        /* Register UI components */
        MapFactory<UIComponent> ui = new MapFactory<UIComponent>()
                .register("graphics", UIGraphicsComponent.class, 0xffffff)
                .register("button", UIButtonComponent.class, 0xffffff)
                .register("icon", UIIconButtonComponent.class, 0xffffff)
                .register("label", UILabelComponent.class, 0xffffff)
                .register("text", UITextComponent.class, 0xffffff)
                .register("textbox", UITextboxComponent.class, 0xffffff)
                .register("textarea", UITextareaComponent.class, 0xffffff)
                .register("toggle", UIToggleComponent.class, 0xffffff)
                .register("trackpad", UITrackpadComponent.class, 0xffffff)
                .register("strings", UIStringListComponent.class, 0xffffff)
                .register("item", UIStackComponent.class, 0xffffff)
                .register("layout", UILayoutComponent.class, 0xffffff)
                .register("morph", UIMorphComponent.class, 0xffffff)
                .register("clickarea", UIClickComponent.class, 0xffffff);

        uiComponents = ui;
        Mappet.EVENT_BUS.post(new RegisterUIComponentEvent(ui));
    }
}