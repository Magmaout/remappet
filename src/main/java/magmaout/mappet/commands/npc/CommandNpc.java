package magmaout.mappet.commands.npc;

import magmaout.mappet.commands.MappetSubCommandBase;
import net.minecraft.command.ICommandSender;

public class CommandNpc extends MappetSubCommandBase {
    public CommandNpc() {
        this.add(new CommandNpcDespawn());
        this.add(new CommandNpcEdit());
        this.add(new CommandNpcState());
        this.add(new CommandNpcSummon());
    }

    @Override
    public String getName() {
        return "npc";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "mappet.commands.mp.npc.help";
    }
}