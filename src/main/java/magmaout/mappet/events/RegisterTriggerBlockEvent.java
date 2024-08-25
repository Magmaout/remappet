package magmaout.mappet.events;

import magmaout.mappet.api.triggers.blocks.AbstractTriggerBlock;
import magmaout.mappet.api.utils.factory.MapFactory;

public class RegisterTriggerBlockEvent extends RegisterFactoryEvent<AbstractTriggerBlock> {
    public RegisterTriggerBlockEvent(MapFactory<AbstractTriggerBlock> factory) {
        super(factory);
    }
}