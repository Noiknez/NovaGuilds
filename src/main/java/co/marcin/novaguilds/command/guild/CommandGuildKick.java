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
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildKick extends AbstractCommandExecutor {
	private static final Command command = Command.GUILD_KICK;

	public CommandGuildKick() {
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

		if(!nPlayer.hasPermission(GuildPermission.KICK)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(args.length == 0) {
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}

		NovaPlayer nPlayerKick = PlayerManager.getPlayer(args[0]);

		if(nPlayerKick == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(!nPlayerKick.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		if(!nPlayerKick.getGuild().getName().equalsIgnoreCase(guild.getName())) {
			Message.CHAT_PLAYER_NOTINYOURGUILD.send(sender);
			return;
		}

		if(nPlayer.getName().equalsIgnoreCase(nPlayerKick.getName())) {
			Message.CHAT_GUILD_KICKYOURSELF.send(sender);
			return;
		}

		//all passed
		guild.removePlayer(nPlayerKick);
		nPlayerKick.cancelToolProgress();

		if(nPlayerKick.isOnline()) {
			guild.hideVaultHologram(nPlayerKick.getPlayer());
		}

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYERNAME, nPlayerKick.getName());
		vars.put(VarKey.GUILDNAME, guild.getName());
		Message.BROADCAST_GUILD_KICKED.vars(vars).broadcast();

		//tab/tag
		TagUtils.refresh();
		TabUtils.refresh();
	}
}
