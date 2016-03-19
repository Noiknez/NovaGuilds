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

package co.marcin.novaguilds.command.admin.guild;


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetLiveRegenerationTime extends AbstractCommandExecutor.Reversed<NovaGuild> {
	private static final Command command = Command.ADMIN_GUILD_SET_LIVEREGENERATIONTIME;

	public CommandAdminGuildSetLiveRegenerationTime() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaGuild guild = getParameter();

		String timeString;
		if(args.length > 1) {
			timeString = StringUtils.join(args, " ");
		}
		else {
			timeString = args[0];
		}

		int seconds = StringUtils.stringToSeconds(timeString);
		long lostLiveTime = NumberUtils.systemSeconds() + (seconds - Config.LIVEREGENERATION_REGENTIME.getSeconds());

		guild.setLostLiveTime(lostLiveTime);
		TabUtils.refresh(guild);

		Message.CHAT_ADMIN_GUILD_TIMEREST_SET.send(sender);
	}
}
