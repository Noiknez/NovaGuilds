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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.CommandSender;

public class CommandAdminRegionDelete extends AbstractCommandExecutor.Reversed<NovaRegion> {
	private static final Command command = Command.ADMIN_REGION_DELETE;

	public CommandAdminRegionDelete() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaRegion region = getParameter();

		plugin.getRegionManager().remove(region);
		Message.CHAT_ADMIN_REGION_DELETE_SUCCESS.send(sender);
	}
}
