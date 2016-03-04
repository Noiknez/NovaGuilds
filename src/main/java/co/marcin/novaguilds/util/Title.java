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

package co.marcin.novaguilds.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Minecraft 1.8 Title
 *
 * @author Maxim Van de Wynckel
 * @version 1.0.4
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public class Title {
	/* Title packet */
	private Class<?> packetTitle;

	/* Title packet actions ENUM */
	private Class<?> packetActions;

	/* Chat serializer */
	private Class<?> nmsChatSerializer;
	private Class<?> chatBaseComponent;

	/* Title text and color */
	private String title = "";
	private ChatColor titleColor = ChatColor.WHITE;

	/* Subtitle text and color */
	private String subtitle = "";
	private ChatColor subtitleColor = ChatColor.WHITE;

	/* Title timings */
	private int fadeInTime = -1;
	private int stayTime = -1;
	private int fadeOutTime = -1;
	private boolean ticks = false;

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<>();

	/**
	 * Create a new 1.8 title
	 *
	 * @param title Title
	 */
	public Title(String title) {
		this.title = title;
		loadClasses();
	}

	/**
	 * Create a new 1.8 title
	 *
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 */
	public Title(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		loadClasses();
	}

	/**
	 * Copy 1.8 title
	 *
	 * @param title Title
	 */
	public Title(Title title) {
		// Copy title
		this.title = title.title;
		subtitle = title.subtitle;
		titleColor = title.titleColor;
		subtitleColor = title.subtitleColor;
		fadeInTime = title.fadeInTime;
		fadeOutTime = title.fadeOutTime;
		stayTime = title.stayTime;
		ticks = title.ticks;
		loadClasses();
	}

	/**
	 * Create a new 1.8 title
	 *
	 * @param title       Title text
	 * @param subtitle    Subtitle text
	 * @param fadeInTime  Fade in time
	 * @param stayTime    Stay on screen time
	 * @param fadeOutTime Fade out time
	 */
	public Title(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeInTime = fadeInTime;
		this.stayTime = stayTime;
		this.fadeOutTime = fadeOutTime;
		loadClasses();
	}

	/**
	 * Load spigot and NMS classes
	 */
	private void loadClasses() {
		packetTitle = getNMSClass("PacketPlayOutTitle");
		packetActions = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
		chatBaseComponent = getNMSClass("IChatBaseComponent");
		nmsChatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
	}

	/**
	 * Set title text
	 *
	 * @param title Title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get title text
	 *
	 * @return Title text
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set subtitle text
	 *
	 * @param subtitle Subtitle text
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	/**
	 * Get subtitle text
	 *
	 * @return Subtitle text
	 */
	public String getSubtitle() {
		return subtitle;
	}

	/**
	 * Set the title color
	 *
	 * @param color Chat color
	 */
	public void setTitleColor(ChatColor color) {
		titleColor = color;
	}

	/**
	 * Set the subtitle color
	 *
	 * @param color Chat color
	 */
	public void setSubtitleColor(ChatColor color) {
		subtitleColor = color;
	}

	/**
	 * Set title fade in time
	 *
	 * @param time Time
	 */
	public void setFadeInTime(int time) {
		fadeInTime = time;
	}

	/**
	 * Set title fade out time
	 *
	 * @param time Time
	 */
	public void setFadeOutTime(int time) {
		fadeOutTime = time;
	}

	/**
	 * Set title stay time
	 *
	 * @param time Time
	 */
	public void setStayTime(int time) {
		stayTime = time;
	}

	/**
	 * Set timings to ticks
	 */
	public void setTimingsToTicks() {
		ticks = true;
	}

	/**
	 * Set timings to seconds
	 */
	public void setTimingsToSeconds() {
		ticks = false;
	}

	/**
	 * Send the title to a player
	 *
	 * @param player Player
	 */
	public void send(Player player) {
		if(packetTitle != null) {
			resetTitle(player);

			try {
				// Send timings first
				Object handle = getHandle(player);
				Object connection = getField(handle.getClass(), "playerConnection").get(handle);
				Object[] actions = packetActions.getEnumConstants();
				Method sendPacket = getMethod(connection.getClass(), "sendPacket");

				Object packet = packetTitle.getConstructor(packetActions,
						chatBaseComponent, Integer.TYPE, Integer.TYPE,
						Integer.TYPE).newInstance(actions[2], null,
						fadeInTime * (ticks ? 1 : 20),
						stayTime * (ticks ? 1 : 20),
						fadeOutTime * (ticks ? 1 : 20));

				if(fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1) {
					sendPacket.invoke(connection, packet);
				}


				Object serialized = getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\""
						+ StringUtils.fixColors(title) + "\",color:"
						+ titleColor.name().toLowerCase() + "}");
				packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[0], serialized);
				sendPacket.invoke(connection, packet);

				if(!subtitle.isEmpty()) {
					serialized = getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\""
							+ StringUtils.fixColors(subtitle)
							+ "\",color:"
							+ subtitleColor.name()
							.toLowerCase() + "}");

					packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[1], serialized);
					sendPacket.invoke(connection, packet);
				}
			}
			catch(Exception e) {
				LoggerUtils.exception(e);
			}
		}
	}

	/**
	 * Broadcast the title to all players
	 */
	public void broadcast() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			send(p);
		}
	}

	/**
	 * Clear the title
	 *
	 * @param player Player
	 */
	public void clearTitle(Player player) {
		try {
			// Send timings first
			Object handle = getHandle(player);
			Object connection = getField(handle.getClass(), "playerConnection").get(handle);
			Object[] actions = packetActions.getEnumConstants();
			Method sendPacket = getMethod(connection.getClass(), "sendPacket");
			Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[3], null);
			sendPacket.invoke(connection, packet);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Reset the title settings
	 *
	 * @param player Player
	 */
	public void resetTitle(Player player) {
		try {
			// Send timings first
			Object handle = getHandle(player);
			Object connection = getField(handle.getClass(), "playerConnection").get(handle);
			Object[] actions = packetActions.getEnumConstants();
			Method sendPacket = getMethod(connection.getClass(), "sendPacket");
			Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[4], null);
			sendPacket.invoke(connection, packet);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	private Class<?> getPrimitiveType(Class<?> clazz) {
		return CORRESPONDING_TYPES.containsKey(clazz) ? CORRESPONDING_TYPES.get(clazz) : clazz;
	}

	private Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
		int a = classes != null ? classes.length : 0;
		Class<?>[] types = new Class<?>[a];

		for(int i = 0; i < a; i++) {
			types[i] = getPrimitiveType(classes[i]);
		}

		return types;
	}

	private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
		if(a.length != o.length) {
			return false;
		}

		for(int i = 0; i < a.length; i++) {
			if(!a[i].equals(o[i]) && !a[i].isAssignableFrom(o[i])) {
				return false;
			}
		}

		return true;
	}

	private Object getHandle(Object obj) {
		try {
			return getMethod("getHandle", obj.getClass()).invoke(obj);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	private Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
		Class<?>[] t = toPrimitiveTypeArray(paramTypes);

		for(Method m : clazz.getMethods()) {
			Class<?>[] types = toPrimitiveTypeArray(m.getParameterTypes());
			if(m.getName().equals(name) && equalsTypeArray(types, t)) {
				return m;
			}
		}

		return null;
	}

	private String getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}

	private Class<?> getNMSClass(String className) {
		String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz = null;

		try {
			clazz = Class.forName(fullName);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}

		return clazz;
	}

	private Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	private Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	private boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;

		if(l1.length != l2.length) {
			return false;
		}

		for(int i = 0; i < l1.length; i++) {
			if(l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}

		return equal;
	}
}
