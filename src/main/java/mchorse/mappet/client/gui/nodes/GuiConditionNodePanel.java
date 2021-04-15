package mchorse.mappet.client.gui.nodes;

import mchorse.mappet.api.events.nodes.ConditionNode;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiConditionNodePanel extends GuiNodePanel<ConditionNode>
{
    public GuiTextElement condition;

    public GuiConditionNodePanel(Minecraft mc)
    {
        super(mc);

        this.condition = new GuiTextElement(mc, 10000, (text) -> this.node.expression = text);

        this.add(Elements.label(IKey.str("Condition")), this.condition, this.binary);
    }

    @Override
    public void set(ConditionNode node)
    {
        super.set(node);

        this.condition.setText(node.expression);
    }
}