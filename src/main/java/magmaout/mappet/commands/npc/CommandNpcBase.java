package magmaout.mappet.commands.npc;

import magmaout.mappet.Mappet;
import magmaout.mappet.api.npcs.Npc;
import magmaout.mappet.commands.MappetCommandBase;
import net.minecraft.command.CommandException;

public abstract class CommandNpcBase extends MappetCommandBase {
    protected Npc getNpc(String id) throws CommandException {
        Npc npc = Mappet.npcs.load(id);

        if (npc == null) {
            throw new CommandException("npc.missing", id);
        }

        return npc;
    }
}