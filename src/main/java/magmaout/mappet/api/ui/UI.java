package magmaout.mappet.api.ui;

import magmaout.mappet.api.utils.AbstractData;
import magmaout.mappet.api.ui.utils.UIRootComponent;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class UI extends AbstractData {
    private UUID id;

    public UIRootComponent root = new UIRootComponent();
    public boolean background;
    public boolean closable = true;
    public boolean paused;

    public UI() {
        this(UUID.randomUUID());
    }

    public UI(UUID id) {
        this.id = id;
    }

    public UUID getUIId() {
        return this.id;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setUniqueId("ID", this.id);
        tag.setTag("Root", this.root.serializeNBT());
        tag.setBoolean("Background", this.background);
        tag.setBoolean("Closeable", this.closable);
        tag.setBoolean("Paused", this.paused);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        this.root.deserializeNBT(tag.getCompoundTag("Root"));

        if (tag.hasKey("IDMost")) {
            this.id = tag.getUniqueId("ID");
        }

        if (tag.hasKey("Background")) {
            this.background = tag.getBoolean("Background");
        }

        if (tag.hasKey("Closeable")) {
            this.closable = tag.getBoolean("Closeable");
        }

        if (tag.hasKey("Paused")) {
            this.paused = tag.getBoolean("Paused");
        }
    }
}