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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;

public class CommandRegionDelete extends AbstractCommandExecutor {
	private static final Command command = Command.REGION_DELETE;

	public CommandRegionDelete() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.REGION_REMOVE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(!nPlayer.getGuild().hasRegion()) {
			Message.CHAT_GUILD_HASNOREGION.send(sender);
			return;
		}

		Message.CHAT_REGION_DELETED.send(sender);
		plugin.getRegionManager().remove(nPlayer.getGuild().getRegion());
	}
}
