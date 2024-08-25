package magmaout.mappet.network.content;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.api.utils.IContentType;
import magmaout.mappet.api.utils.manager.IManager;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PacketContentFolder extends PacketContentBase {
    public String name = "";
    public String path = "";
    public String rename;
    public Boolean delete = false;

    public PacketContentFolder() {
        super();
    }

    public PacketContentFolder(IContentType type, String name, String path) {
        super(type);
        this.path = path;
        this.name = name;
    }

    public PacketContentFolder rename(String rename) {
        this.rename = rename;

        return this;
    }

    public PacketContentFolder delete() {
        this.delete = true;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        this.name = ByteBufUtils.readUTF8String(buf);
        this.path = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean()) {
            this.rename = ByteBufUtils.readUTF8String(buf);
        }

        this.delete = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        ByteBufUtils.writeUTF8String(buf, this.name);
        ByteBufUtils.writeUTF8String(buf, this.path);

        buf.writeBoolean(this.rename != null);

        if (this.rename != null) {
            ByteBufUtils.writeUTF8String(buf, this.rename);
        }

        buf.writeBoolean(this.delete);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketContentFolder> {

        @Override
        public void run(EntityPlayerMP player, PacketContentFolder message) {
            IManager manager = message.type.getManager();
            Path folder = manager.getFolder().toPath();

            if (message.rename != null && !message.path.isEmpty()) {
                int lastIndex = message.path.lastIndexOf('/');
                String newPath = lastIndex == -1 ? "" + message.rename : message.path.substring(0, lastIndex + 1) + message.rename;

                folder.resolve(message.path).toFile().renameTo(folder.resolve(newPath).toFile());
            } else if (message.delete && !message.path.isEmpty()) {
                File deleteFolder = folder.resolve(message.path).toFile();

                for (File file : deleteFolder.listFiles()) {
                    file.delete();
                }

                deleteFolder.delete();
            } else {
                folder.resolve(message.path + message.name).toFile().mkdirs();
            }

            /* Synchronize names to players */
            List<String> names = new ArrayList<String>(message.type.getManager().getKeys());

            for (EntityPlayerMP otherPlayer : player.getServer().getPlayerList().getPlayers()) {
                Dispatcher.sendTo(new PacketContentNames(message.type, names), otherPlayer);
            }
        }
    }
}
