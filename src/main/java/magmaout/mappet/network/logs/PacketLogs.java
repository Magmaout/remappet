package magmaout.mappet.network.logs;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.Mappet;
import magmaout.mappet.api.utils.logs.MappetLogger;
import magmaout.mappet.client.gui.GuiMappetDashboard;
import magmaout.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class PacketLogs implements IMessage {
    public String text;

    public PacketLogs() {
    }

    public PacketLogs(String line) {
        this.text = line;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.text = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.text);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketRequestLogs> {
        @Override
        public void run(EntityPlayerMP player, PacketRequestLogs message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            LocalDateTime lastLogTime = LocalDateTime.parse(message.lastLogTime, MappetLogger.dtf);

            File mappetWorldFolder = new File(DimensionManager.getCurrentSaveRootDirectory(), Mappet.MOD_ID);

            File logFile = new File(mappetWorldFolder, "logs/latest.log");

            try {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));

                int stringEncodingLimit = 16384; // Because of forge :(

                String stringToSend = "";

                String line;
                boolean isPreviousLineNew = false;
                while ((line = reader.readLine()) != null) {
                    if (!isNewLine(lastLogTime, line, isPreviousLineNew)) {
                        isPreviousLineNew = false;
                        continue;
                    }

                    isPreviousLineNew = true;

                    if (stringToSend.getBytes().length + line.getBytes().length < stringEncodingLimit) {
                        stringToSend = stringToSend.concat(line + "\r");
                    } else {
                        Dispatcher.sendTo(new PacketLogs(stringToSend), player);

                        stringToSend = "";
                    }
                }

                if (!stringToSend.equals("")) {
                    Dispatcher.sendTo(new PacketLogs(stringToSend), player);
                }
            } catch (IOException e) {
            }
        }

        public boolean isNewLine(LocalDateTime date, String line, boolean isPreviousLineNew) {
            if (date.equals(LocalDateTime.of(1, 1, 1, 0, 0, 0))) {
                return true;
            }
            int bracketIndex = line.indexOf("]");

            if (bracketIndex == -1) {
                return isPreviousLineNew;
            }

            String logDateString = line.substring(1, bracketIndex);

            LocalDateTime logDate = LocalDateTime.parse(logDateString, MappetLogger.dtf);

            return logDate.isAfter(date);
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketLogs> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketLogs message) {
            GuiMappetDashboard.get(Minecraft.getMinecraft()).logs.update(message.text);
        }
    }
}