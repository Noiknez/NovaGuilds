package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.exception.MissingDependencyException;
import co.marcin.novaguilds.util.LoggerUtils;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;

public class DependencyManager {
	private final Map<Dependency, Plugin> pluginMap = new HashMap<>();

	private Economy economy;

	public void setUp() throws FatalNovaGuildsException {
		try {
			checkDependencies();
			setupEconomy();
		}
		catch(MissingDependencyException e) {
			throw new FatalNovaGuildsException("Could not satisfy dependencies", e);
		}
	}

	public void checkDependencies() throws MissingDependencyException {
		pluginMap.clear();

		for(Dependency dependency : Dependency.values()) {
			Plugin plugin = getPlugin(dependency.getName());

			if(plugin != null) {
				pluginMap.put(dependency, plugin);
				LoggerUtils.info("Found plugin " + dependency.getName());

				if(dependency.hasAdditionalTasks()) {
					for(RunnableWithException additionalTask : dependency.getAdditionalTasks()) {
						try {
							LoggerUtils.info("Running additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName());
							additionalTask.run();
						}
						catch(Exception e) {
							throw new MissingDependencyException("Could not pass additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName(), e);
						}
					}
				}
			}
			else {
				if(dependency.isHardDependency()) {
					throw new MissingDependencyException("Missing dependency " + dependency.getName());
				}
				else {
					LoggerUtils.info("Could not find plugin: " + dependency.getName() + ", disabling certain features");
				}
			}
		}
	}

	public boolean isEnabled(Dependency dependency) {
		return pluginMap.containsKey(dependency);
	}

	/**
	 * Setups economy
	 */
	private void setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		economy = rsp.getProvider();
		Validate.notNull(economy);
	}

	private Plugin getPlugin(String name) {
		return ListenerManager.getLoggedPluginManager().getPlugin(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Dependency dependency, Class<T> cast) {
		return (T) pluginMap.get(dependency);
	}

	public static class HolographicDisplaysAPIChecker implements RunnableWithException {
		@Override
		public void run() throws ClassNotFoundException {
			Class.forName("com.gmail.filoghost.holographicdisplays.api.HologramsAPI");
		}
	}

	public interface RunnableWithException {
		void run() throws Exception;
	}

	public Economy getEconomy() {
		return economy;
	}
}
