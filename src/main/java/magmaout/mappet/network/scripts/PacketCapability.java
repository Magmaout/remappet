package magmaout.mappet.network.scripts;

import io.netty.buffer.ByteBuf;
import magmaout.mappet.capabilities.character.Character;
import magmaout.mappet.utils.CapabilityTypes;
import magmaout.mappet.capabilities.camera.Camera;
import magmaout.mappet.capabilities.hand.Hand;
import magmaout.mappet.capabilities.location.Location;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCapability implements IMessage {
    NBTTagCompound profile;
    CapabilityTypes type;
    public PacketCapability() {}

    public PacketCapability(NBTTagCompound profile, CapabilityTypes type) {
        this.profile = profile;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.profile = ByteBufUtils.readTag(buf);
        this.type = CapabilityTypes.valueOf(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.profile);
        ByteBufUtils.writeUTF8String(buf, this.type.toString());
    }

    public static class ClientHandler extends ClientMessageHandler<PacketCapability> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketCapability message) {
            CapabilityTypes type = message.type;
            player = Minecraft.getMinecraft().player;

            switch (type){
                case CAMERA:
                    Camera.get(player).deserializeNBT(message.profile);
                    break;
                case HAND:
                    Hand.get(player).deserializeNBT(message.profile);
                    break;
                case CHARACTER:
                    Character.get(player).deserializeNBT(message.profile);
                    break;
                case LOCATION:
                    Location.get(player).deserializeNBT(message.profile);
                    break;
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketCapability> {
        @Override
        public void run(EntityPlayerMP player, PacketCapability message) {
            CapabilityTypes type = message.type;

            switch (type){
                case CAMERA:
                    Camera.get(player).deserializeNBT(message.profile);
                    break;
                case HAND:
                    Hand.get(player).deserializeNBT(message.profile);
                    break;
                case CHARACTER:
                    Character.get(player).deserializeNBT(message.profile);
                    break;
                case LOCATION:
                    Location.get(player).deserializeNBT(message.profile);
                    break;
            }
        }
    }
}