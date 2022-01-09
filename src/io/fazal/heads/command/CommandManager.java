package io.fazal.heads.command;

import io.fazal.heads.Main;
import io.fazal.heads.commands.*;
import io.fazal.heads.menu.DepositMenu;
import io.fazal.heads.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class CommandManager implements CommandExecutor {

    public static List<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new BalanceCommand());
        commands.add(new GiveCommand());
        commands.add(new PayCommand());
        commands.add(new TakeCommand());
        commands.add(new ViewCommand());
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            List<String> commandHelp = new ArrayList<>();
            for (Command cmd : commands) {
                CommandInfo commandInfo = cmd.getClass().getAnnotation(CommandInfo.class);
                if (commandInfo.permission().equalsIgnoreCase("none")) {
                    commandHelp.add(Utils.getInstance().toColor(Main.getInstance().getConfig().getString("Messages.COMMAND_HELP_FORMAT").replace("%command%", "/" + label + " " + ((commandInfo.aliases().length > 0) ? (StringUtils.join(commandInfo.aliases(), " | ") + " ") : "") + (commandInfo.usage().isEmpty() ? "" : (commandInfo.usage() + " ")) + "- " + commandInfo.description())));
                } else if (sender.hasPermission("heads." + commandInfo.permission()) || sender.hasPermission("heads.admin")) {
                    commandHelp.add(Utils.getInstance().toColor(Main.getInstance().getConfig().getString("Messages.COMMAND_HELP_FORMAT").replace("%command%", "/" + label + " " + ((commandInfo.aliases().length > 0) ? (StringUtils.join(commandInfo.aliases(), " | ") + " ") : "") + (commandInfo.usage().isEmpty() ? "" : (commandInfo.usage() + " ")) + "- " + commandInfo.description())));
                }
            }
            if (commandHelp.size() == 0 || !sender.hasPermission("heads.admin")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    new DepositMenu(player);
                    return true;
                }
                Utils.getInstance().sendMessage(sender, "NO_PERMISSION");
                return true;
            }
            for (String message : Main.getInstance().getConfig().getStringList("Messages.COMMAND_HELP")) {
                if (message.equalsIgnoreCase("%printcommands%")) {
                    commandHelp.forEach(sender::sendMessage);
                } else
                    sender.sendMessage(Utils.getInstance().toColor(message));
            }
            return true;
        } else {
            Command cmd = null;
            for (Command searchCommand : commands) {
                String[] aliases;
                for (int length = (aliases = searchCommand.getClass().getAnnotation(CommandInfo.class).aliases()).length, i = 0; i < length; ++i) {
                    if (aliases[i].equals(args[0])) {
                        cmd = searchCommand;
                        break;
                    }
                }
            }
            if (cmd == null) {
                Utils.getInstance().sendMessage(sender, "INVALID_CMD");
                return true;
            }
            if (!cmd.getClass().getAnnotation(CommandInfo.class).permission().equalsIgnoreCase("none") && !sender.hasPermission("heads." + cmd.getClass().getAnnotation(CommandInfo.class).permission()) && !sender.hasPermission("heads.admin")) {
                Utils.getInstance().sendMessage(sender, "NO_PERMISSION");
                return true;
            }
            Vector<String> vector = new Vector<>(Arrays.asList(args));
            vector.remove(0);
            args = vector.toArray(new String[0]);
            cmd.onCommand(sender, args);
        }
        return false;
    }

}