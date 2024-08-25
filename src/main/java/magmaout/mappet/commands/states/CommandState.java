package magmaout.mappet.commands.states;

import magmaout.mappet.Mappet;
import magmaout.mappet.api.states.States;
import magmaout.mappet.commands.MappetSubCommandBase;
import magmaout.mappet.utils.EntityUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public class CommandState extends MappetSubCommandBase {
    public static States getStates(MinecraftServer server, ICommandSender sender, String target) throws CommandException {
        if (target.equals("~")) {
            return Mappet.states;
        }

        Entity entity = getEntity(server, sender, target);
        States states = EntityUtils.getStates(entity);

        if (states != null) {
            return states;
        }

        throw new CommandException("states.invalid_target", target);
    }

    public CommandState() {
        this.add(new CommandStateAdd());
        this.add(new CommandStateClear());
        this.add(new CommandStateIf());
        this.add(new CommandStatePrint());
        this.add(new CommandStateSet());
        this.add(new CommandStateSub());
    }

    @Override
    public String getName() {
        return "state";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "mappet.commands.mp.state.help";
    }
}
