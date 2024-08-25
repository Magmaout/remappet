package magmaout.mappet.events;

import magmaout.mappet.api.conditions.blocks.AbstractConditionBlock;
import magmaout.mappet.api.utils.factory.MapFactory;

public class RegisterConditionBlockEvent extends RegisterFactoryEvent<AbstractConditionBlock> {
    public RegisterConditionBlockEvent(MapFactory<AbstractConditionBlock> factory) {
        super(factory);
    }
}