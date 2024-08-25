package magmaout.mappet.client.gui.scripts.utils.documentation;

import magmaout.mappet.client.gui.utils.text.GuiText;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocMethod extends DocEntry {
    public DocReturn returns;
    public List<DocParameter> arguments = new ArrayList<DocParameter>();
    public List<String> annotations = new ArrayList<String>();

    @Override
    public String getName() {
        String args = this.arguments.stream().map(DocParameter::getType).collect(Collectors.joining(", "));

        return super.getName() + "(" + TextFormatting.GRAY + args + TextFormatting.RESET + ")";
    }

    @Override
    public void fillIn(Minecraft mc, GuiScrollElement target) {
        super.fillIn(mc, target);

        boolean first = true;

        for (DocParameter parameter : this.arguments) {
            GuiText text = new GuiText(mc).text(TextFormatting.GOLD + parameter.getType() + TextFormatting.RESET + " " + parameter.name);

            if (first) {
                text.marginTop(8);
            }

            target.add(text);

            if (!parameter.doc.isEmpty()) {
                process(parameter.doc, mc, target);

                ((GuiElement) target.getChildren().get(target.getChildren().size() - 1)).marginBottom(8);
            }

            first = false;
        }

        target.add(new GuiText(mc).text("Returns " + TextFormatting.GOLD + this.returns.getType()).marginTop(8));

        List<String> annotations = this.annotations.stream()
                .map(annotation -> "@" + annotation.substring(annotation.lastIndexOf(".") + 1))
                .filter(annotation -> !annotation.equals("@Override"))
                .collect(Collectors.toList());
        if (annotations.size() > 0) {
            String annotationsText = String.join(", ", annotations);
            target.add(new GuiText(mc).text(String.valueOf(TextFormatting.GRAY) + TextFormatting.BOLD + annotationsText).marginTop(8));
        }

        if (!this.returns.doc.isEmpty()) {
            process(this.returns.doc, mc, target);
        }
    }

    @Override
    public List<DocEntry> getEntries() {
        return this.parent == null ? super.getEntries() : this.parent.getEntries();
    }
}