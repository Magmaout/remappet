package magmaout.mappet.api.scripts.code.items;

import magmaout.mappet.api.scripts.user.items.IScriptItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ScriptItem implements IScriptItem {
    private Item item;

    public ScriptItem(Item item) {
        this.item = item;
    }

    @Override
    public Item getMinecraftItem() {
        return this.item;
    }

    @Override
    public String getId() {
        ResourceLocation location = this.item == null ? null : this.item.getRegistryName();

        return location == null ? "" : location.toString();
    }

    @Override
    public boolean isSame(IScriptItem item) {
        return this.item == ((ScriptItem) item).getMinecraftItem();
    }
}