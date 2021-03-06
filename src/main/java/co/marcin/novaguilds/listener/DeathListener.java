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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.util.ChatBroadcast;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.preparedtag.PreparedTagChatImpl;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class DeathListener extends AbstractListener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		NovaPlayer nPlayer = PlayerManager.getPlayer(victim);

		//Exit from region
		if(nPlayer.isAtRegion()) {
			plugin.getRegionManager().playerExitedRegion(nPlayer.getPlayer());
		}

		if(event.getEntity().getKiller() == null) {
			return;
		}

		Player attacker = event.getEntity().getKiller();
		NovaPlayer nPlayerAttacker = PlayerManager.getPlayer(attacker);

		nPlayerAttacker.addKill();
		nPlayer.addDeath();

		if(nPlayer.isPartRaid() && nPlayerAttacker.isPartRaid() && nPlayer.getPartRaid().equals(nPlayerAttacker.getPartRaid()) && !nPlayer.getGuild().isMember(nPlayerAttacker)) {
			NovaRaid raid = nPlayer.getPartRaid();

			if(raid.getPlayersOccupying().contains(nPlayerAttacker)) {
				raid.addKillAttacker();
			}
			else {
				raid.addKillDefender();
			}
		}

		PreparedTag preparedTag1 = new PreparedTagChatImpl(nPlayer, false);
		PreparedTag preparedTag2 = new PreparedTagChatImpl(nPlayerAttacker, false);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYER1, victim.getName());
		vars.put(VarKey.PLAYER2, attacker.getName());
		ChatBroadcast chatBroadcast = Message.BROADCAST_PVP_KILLED.vars(vars).newChatBroadcast();
		chatBroadcast.setTag(1, preparedTag1);
		chatBroadcast.setTag(2, preparedTag2);
		chatBroadcast.send();

		//guildpoints and money
		if(nPlayerAttacker.canGetKillPoints(victim)) {
			//Guild points
			if(nPlayer.hasGuild()) {
				NovaGuild guildVictim = nPlayer.getGuild();
				guildVictim.takePoints(Config.GUILD_DEATHPOINTS.getInt());
			}

			if(nPlayerAttacker.hasGuild()) {
				NovaGuild guildAttacker = nPlayerAttacker.getGuild();
				guildAttacker.addPoints(Config.GUILD_KILLPOINTS.getInt());
			}

			//Raid bonus percent
			double bonusPercentMoney = 0;
			double bonusPercentPoints = 0;
			if(nPlayer.isPartRaid()) {
				bonusPercentMoney = Config.RAID_PVP_BONUSPERCENT_MONEY.getPercent();
				bonusPercentPoints = Config.RAID_PVP_BONUSPERCENT_POINTS.getPercent();
			}

			//player points
			int points = (int) Math.round(nPlayer.getPoints() * (Config.KILLING_RANKPERCENT.getPercent() + bonusPercentPoints));
			nPlayer.takePoints(points);
			nPlayerAttacker.addPoints(points);
			nPlayerAttacker.addKillHistory(victim);

			//money
			vars.clear();
			vars.put(VarKey.PLAYERNAME, victim.getName());
			double money;
			if(nPlayer.canGetKillPoints(attacker)) {
				money = (Config.KILLING_MONEYFORKILL.getPercent() + bonusPercentMoney) * nPlayer.getMoney();

				if(money > 0) {
					vars.put(VarKey.MONEY, String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_KILL.vars(vars).send(attacker);
				}
			}
			else {
				money = (Config.KILLING_MONEYFORREVENGE.getPercent() + bonusPercentMoney) * nPlayer.getMoney();

				if(money > 0) {
					vars.put(VarKey.MONEY, String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_REVENGE.vars(vars).send(attacker);
				}
			}

			if(money > 0) {
				nPlayer.takeMoney(money);
				nPlayerAttacker.addMoney(money);
			}
		}

		//disable death message
		event.setDeathMessage(null);

		//Refresh tab and tag
		TabUtils.refresh(attacker);
		TabUtils.refresh(victim);
		TagUtils.refresh(attacker);
		TagUtils.refresh(victim);
	}
}
