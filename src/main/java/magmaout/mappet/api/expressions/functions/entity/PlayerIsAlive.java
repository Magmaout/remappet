package magmaout.mappet.api.expressions.functions.entity;

import magmaout.mappet.Mappet;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class PlayerIsAlive extends SNFunction {
    public PlayerIsAlive(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double doubleValue() {
        try {
            String target = this.getArg(0).stringValue();

            MinecraftServer server = Mappet.expressions.getServer();
            List<EntityPlayerMP> players = CommandBase.getPlayers(server, server, target);

            for (EntityPlayerMP player : players) {
                if (!player.isEntityAlive()) {
                    return 0;
                }
            }

            return 1;
        } catch (Exception e) {
        }

        return 0;
    }
}