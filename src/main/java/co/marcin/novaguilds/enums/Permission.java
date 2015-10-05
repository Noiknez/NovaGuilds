package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public enum Permission {
	NOVAGUILDS_ADMIN_ACCESS,
	NOVAGUILDS_ADMIN_GUILD_ACCESS,
	NOVAGUILDS_ADMIN_GUILD_ABANDON,
	NOVAGUILDS_ADMIN_GUILD_BANK_PAY,
	NOVAGUILDS_ADMIN_GUILD_BANK_WITHDRAW,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_UPDATE,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_CLEAN,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_LIST,
	NOVAGUILDS_ADMIN_GUILD_INVITE,
	NOVAGUILDS_ADMIN_GUILD_KICK,
	NOVAGUILDS_ADMIN_GUILD_LIST,
	NOVAGUILDS_ADMIN_GUILD_SET_LEADER,
	NOVAGUILDS_ADMIN_GUILD_SET_LIVEREGENERATIONTIME,
	NOVAGUILDS_ADMIN_GUILD_SET_LIVES,
	NOVAGUILDS_ADMIN_GUILD_SET_NAME,
	NOVAGUILDS_ADMIN_GUILD_SET_POINTS,
	NOVAGUILDS_ADMIN_GUILD_SET_TAG,
	NOVAGUILDS_ADMIN_GUILD_SET_TIMEREST,
	NOVAGUILDS_ADMIN_GUILD_TP,
	NOVAGUILDS_ADMIN_REGION_ACCESS,
	NOVAGUILDS_ADMIN_REGION_BYPASS_SELF,
	NOVAGUILDS_ADMIN_REGION_BYPASS_OTHER,
	NOVAGUILDS_ADMIN_REGION_DELETE,
	NOVAGUILDS_ADMIN_REGION_LIST,
	NOVAGUILDS_ADMIN_REGION_TP,
	NOVAGUILDS_ADMIN_RELOAD,
	NOVAGUILDS_ADMIN_SAVE,
	NOVAGUILDS_ADMIN_SAVE_NOTIFY,
	NOVAGUILDS_ADMIN_UPDATEAVAILABLE,
	NOVAGUILDS_GUILD_ACCESS,
	NOVAGUILDS_GUILD_LEAVE,
	NOVAGUILDS_GUILD_ALLY,
	NOVAGUILDS_GUILD_BANK_PAY,
	NOVAGUILDS_GUILD_BANK_WITHDRAW,
	NOVAGUILDS_GUILD_COMPASS,
	NOVAGUILDS_GUILD_CREATE,
	NOVAGUILDS_GUILD_EFFECT,
	NOVAGUILDS_GUILD_HOME,
	NOVAGUILDS_GUILD_HOME_SET,
	NOVAGUILDS_GUILD_INVITE,
	NOVAGUILDS_GUILD_JOIN,
	NOVAGUILDS_GUILD_KICK,
	NOVAGUILDS_GUILD_GUI,
	NOVAGUILDS_GUILD_PVPTOGGLE,
	NOVAGUILDS_GUILD_REQUIREDITEMS,
	NOVAGUILDS_GUILD_TOP,
	NOVAGUILDS_GUILD_WAR,
	NOVAGUILDS_REGION_CREATE,
	NOVAGUILDS_REGION_RESIZE,
	NOVAGUILDS_CHAT_NOTAG,
	NOVAGUILDS_PLAYERINFO,
	NOVAGUILDS_TOOL_CHECK,
	NOVAGUILDS_TOOL_GET;

	public boolean has(CommandSender sender) {
		return sender.hasPermission(getPath());
	}

	public String getPath() {
		return name().replaceAll("_", ".").toLowerCase();
	}

	public static Permission fromPath(String path) {
		try {
			return Permission.valueOf(StringUtils.replace(path, ".", "_").toUpperCase());
		}
		catch(Exception e) {
			return null;
		}
	}
}