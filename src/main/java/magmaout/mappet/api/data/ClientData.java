package magmaout.mappet.api.data;

import magmaout.mappet.api.scripts.user.data.ScriptVector;
import net.minecraft.nbt.NBTTagCompound;

public enum ClientData {
    MOUSEPOSITION {
        @Override
        public Object process(NBTTagCompound data) {
            NBTTagCompound pos = data.getCompoundTag(this.name());
            return new ScriptVector(pos.getInteger("x"), pos.getInteger("y"), 0);
        }
    },

    CLIPBOARD {
        @Override
        public Object process(NBTTagCompound data) {
            return data.getString(this.name());
        }
    },

    SETTING {
        @Override
        public Object process(NBTTagCompound data) {
            String setting = data.getString(this.name());
            return SETTING.converter(setting);
        }
    },

    RESOLUTION {
        @Override
        public Object process(NBTTagCompound data) {
            NBTTagCompound display = data.getCompoundTag(this.name());
            return new ScriptVector(display.getInteger("x"), display.getInteger("y"), 0);
        }
    },

    WEB_LINK {
        @Override
        public Object process(NBTTagCompound data) {
            return data.getString(this.name());
        }
    };

    ClientData() {}

    public abstract Object process(NBTTagCompound data);

    private Object converter(String setting) {
        try {
            return Integer.parseInt(setting);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(setting);
            } catch (NumberFormatException g) {
                try {
                    return Float.parseFloat(setting);
                } catch (NumberFormatException f) {
                    try {
                        return Boolean.parseBoolean(setting);
                    } catch (NumberFormatException x) {
                        return setting;
                    }
                }
            }
        }
    }
}