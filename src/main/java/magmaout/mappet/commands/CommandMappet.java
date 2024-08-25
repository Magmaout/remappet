package magmaout.mappet.commands;

import magmaout.mappet.api.utils.DataContext;
import magmaout.mappet.commands.data.CommandData;
import magmaout.mappet.commands.huds.CommandHud;
import magmaout.mappet.commands.morphs.CommandMorph;
import magmaout.mappet.commands.npc.CommandNpc;
import magmaout.mappet.commands.scripts.CommandScript;
import magmaout.mappet.commands.sounds.CommandCustomPlaySound;
import magmaout.mappet.commands.states.CommandState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.stream.Collectors;

public class CommandMappet extends MappetSubCommandBase {
    public static DataContext createContext(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
        if (argument.equals("~")) {
            return new DataContext(server);
        }

        return new DataContext(getEntity(server, sender, argument));
    }

    public static List<String> listOfPlayersAndServer(MinecraftServer server) {
        List<String> list = listOfPlayers(server);

        list.add("~");

        return list;
    }

    public static List<String> listOfPlayers(MinecraftServer server) {
        return server.getPlayerList().getPlayers().stream().map(EntityPlayer::getName).collect(Collectors.toList());
    }

    public CommandMappet() {
        this.add(new CommandData());
        this.add(new CommandHud());
        this.add(new CommandMorph());
        this.add(new CommandNpc());
        this.add(new CommandScript());
        this.add(new CommandState());
        this.add(new CommandCustomPlaySound());
    }

    @Override
    public String getName() {
        return "mp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "mappet.commands.mp.help";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
