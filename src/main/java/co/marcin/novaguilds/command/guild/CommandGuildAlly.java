/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.command.guild;


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGuildAlly extends AbstractCommandExecutor {
	private static final Command command = Command.GUILD_ALLY;

	public CommandGuildAlly() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length > 0) {
			NovaGuild allyGuild = GuildManager.getGuildFind(args[0]);

			if(allyGuild == null) {
				Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
				return;
			}

			if(allyGuild.equals(guild)) {
				Message.CHAT_GUILD_ALLY_SAMENAME.send(sender);
				return;
			}

			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.GUILDNAME, guild.getName());
			vars.put(VarKey.ALLYNAME, allyGuild.getName());

			if(!guild.isAlly(allyGuild)) {
				if(guild.isWarWith(allyGuild)) {
					Message.CHAT_GUILD_ALLY_WAR.vars(vars).send(sender);
					return;
				}

				if(guild.isInvitedToAlly(allyGuild)) { //Accepting
					if(!nPlayer.hasPermission(GuildPermission.ALLY_ACCEPT)) {
						Message.CHAT_GUILD_NOGUILDPERM.send(sender);
						return;
					}

					allyGuild.addAlly(guild);
					guild.addAlly(allyGuild);
					guild.removeAllyInvitation(allyGuild);
					Message.BROADCAST_GUILD_ALLIED.vars(vars).broadcast();

					Message.CHAT_GUILD_ALLY_ACCEPTED.vars(vars).send(sender);

					//tags & tab
					TagUtils.refresh();
					TabUtils.refresh();
				}
				else { //Inviting
					if(!allyGuild.isInvitedToAlly(guild)) {
						if(!nPlayer.hasPermission(GuildPermission.ALLY_INVITE_SEND)) {
							Message.CHAT_GUILD_NOGUILDPERM.send(sender);
							return;
						}

						allyGuild.addAllyInvitation(guild);
						Message.CHAT_GUILD_ALLY_INVITED.vars(vars).send(sender);
						Message.CHAT_GUILD_ALLY_NOTIFYGUILD.vars(vars).broadcast(allyGuild);
					}
					else { //cancel inv
						if(!nPlayer.hasPermission(GuildPermission.ALLY_INVITE_CANCEL)) {
							Message.CHAT_GUILD_NOGUILDPERM.send(sender);
							return;
						}

						allyGuild.removeAllyInvitation(guild);

						Message.CHAT_GUILD_ALLY_CANCELED.vars(vars).send(sender);
						Message.CHAT_GUILD_ALLY_NOTIFYGUILDCANCELED.vars(vars).broadcast(allyGuild);
					}
				}
			}
			else { //UN-ALLY
				if(!nPlayer.hasPermission(GuildPermission.ALLY_CANCEL)) {
					Message.CHAT_GUILD_NOGUILDPERM.send(sender);
					return;
				}

				guild.removeAlly(allyGuild);
				allyGuild.removeAlly(guild);

				Message.BROADCAST_GUILD_ENDALLY.vars(vars).broadcast();

				TagUtils.refresh();
				TabUtils.refresh();
			}

			return;
		}

		//List allies
		Message.CHAT_GUILD_ALLY_LIST_HEADER_ALLIES.send(sender);
		String separator = Message.CHAT_GUILD_ALLY_LIST_SEPARATOR.get();
		String guildNameFormat = Message.CHAT_GUILD_ALLY_LIST_ITEM.get();

		if(!guild.getAllies().isEmpty()) {
			List<String> alliesNamesList = new ArrayList<>();

			for(NovaGuild guildLoop : guild.getAllies()) {
				alliesNamesList.add(guildLoop.getName());
			}

			MessageManager.sendMessage(sender, StringUtils.join(alliesNamesList, guildNameFormat, separator, "GUILDNAME"));
		}
		else {
			Message.CHAT_GUILD_ALLY_LIST_NOALLIES.send(sender);
		}

		if(!guild.getAllyInvitations().isEmpty()) {
			Message.CHAT_GUILD_ALLY_LIST_HEADER_INVITATIONS.send(sender);

			List<String> allyInvitationNames = new ArrayList<>();
			for(NovaGuild guildLoop : guild.getAllyInvitations()) {
				allyInvitationNames.add(guildLoop.getName());
			}

			MessageManager.sendMessage(sender, StringUtils.join(allyInvitationNames, guildNameFormat, separator, "GUILDNAME"));
		}
	}
}
