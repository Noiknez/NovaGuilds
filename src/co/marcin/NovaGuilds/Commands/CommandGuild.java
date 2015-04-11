package co.marcin.NovaGuilds.Commands;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandGuild implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length>0) {
			String command = args[0].toLowerCase();
			String[] newargs = Utils.parseArgs(args,1);
			
			if(command.equals("pay")) {
				new CommandGuildBankPay(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("withdraw")) {
				new CommandGuildBankWithdraw(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("leader")) {
				new CommandGuildLeader(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("info")) {
				new CommandGuildInfo(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("leave")) {
				new CommandGuildLeave(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("home")) {
				new CommandGuildHome(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("buyregion")) {
				new CommandRegionBuy(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("ally")) {
				new CommandGuildAlly(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("kick")) {
				new CommandGuildKick(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("abandon")) {
				new CommandGuildAbandon(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("invite")) {
				new CommandGuildInvite(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("join")) {
				new CommandGuildJoin(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(command.equals("create")) {
				new CommandGuildCreate(plugin).onCommand(sender, cmd, label, newargs);
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			if(plugin.getPlayerManager().getPlayerBySender(sender).hasGuild()) {
				List<String> cmdlist = plugin.getMessages().getStringList("chat.commands.guild.hasguild");
				for(String cmdinfo : cmdlist) {
					sender.sendMessage(Utils.fixColors(cmdinfo));
				}
			}
			else {
				List<String> cmdlist = plugin.getMessages().getStringList("chat.commands.guild.noguild");
				for(String cmdinfo : cmdlist) {
					sender.sendMessage(Utils.fixColors(cmdinfo));
				}
			}
		}
		return true;
	}
}