package magmaout.mappet.events;

import magmaout.mappet.api.ui.components.UIComponent;
import magmaout.mappet.api.utils.factory.MapFactory;

public class RegisterUIComponentEvent extends RegisterFactoryEvent<UIComponent> {
    public RegisterUIComponentEvent(MapFactory<UIComponent> factory) {
        super(factory);
    }
}