package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;

import be.isach.turfwars.TurfWars;
import be.isach.turfwars.api.TurfWarsAPI;
import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Datenbank.MySQL;
import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.MinecraftServer.MinecraftServerStatus;
import de.SebastianMikolai.PlanetFx.Spleef.Spleef;
import de.SebastianMikolai.PlanetFx.Spleef.Game.Game;
import de.ftbastler.bukkitgames.api.BukkitGamesAPI;

public class SSClient extends JavaPlugin {
	
	public static SSClient instance;
	public MinecraftServerStatus status;
	public int tcp_master_port;
	public String hubserver;
	public String gamename;
	
	public static SSClient getInstance() {
		return instance;
	}

	public void onLoad() {
		instance = this;
		saveDefaultConfig();
		gamename = getConfig().getString("game");
		hubserver = getConfig().getString("hub");
		if (gamename.equalsIgnoreCase("none")) {
			System.exit(0);
		}
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		status = MinecraftServerStatus.Offline;
	}
	
	public void onEnable() {
		MySQL.LadeTabellen();
		tcp_master_port = MySQL.getMinecraftServer("master").getPort();
		getCommand("lobby").setExecutor(new LobbyCommandListener());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new EventListener(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				SSClient.getInstance().update();
			}
		}, 20L, 20L);
	}
	
	public void onDisable() {
		sendServerStatus(MinecraftServerStatus.Offline);
	}
	
	public void update() {
		try {
			if (gamename.equalsIgnoreCase("bedwars")) {
				Location loc = new Location(Bukkit.getWorld(getConfig().getString("signs.bedwars.world")), getConfig().getInt("signs.bedwars.x"), getConfig().getInt("signs.bedwars.y"), getConfig().getInt("signs.bedwars.z"));
				Sign sign = (Sign) loc.getBlock().getState();
				String l0 = ChatColor.stripColor(sign.getLine(0));
				String l3 = ChatColor.stripColor(sign.getLine(3));
				if (l0.equalsIgnoreCase("[Bedwars]")) {
					if (l3.equalsIgnoreCase("Warten ...")) {
						status = MinecraftServerStatus.Online;
					} else if (l3.equalsIgnoreCase("Gestartet!")) {
						status = MinecraftServerStatus.Running;
					} else {
						status = MinecraftServerStatus.Offline;
					}
				} else {
					status = MinecraftServerStatus.Offline;
				}
			} else if (gamename.equalsIgnoreCase("spleef")) {
				Game game = Spleef.getAPI();
				Bukkit.getLogger().info("Pre: " + game.isPreparign() + " Runn: " + game.isRunning());
				if (!game.isPreparign()) {
					status = MinecraftServerStatus.Online;
				} else {
					status = MinecraftServerStatus.Waiting;
				}
				if (game.isRunning()) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						status = MinecraftServerStatus.Running;
					} else {
						status = MinecraftServerStatus.Offline;
					}
				}
			} else if (gamename.equalsIgnoreCase("hungergames")) {
				BukkitGamesAPI bga = BukkitGamesAPI.getApi();
				de.ftbastler.bukkitgames.enums.GameState gs = bga.getCurrentGameState();
				if (gs == de.ftbastler.bukkitgames.enums.GameState.PREGAME) {
					if (Bukkit.getOnlinePlayers().size() >= 4) {
						status = MinecraftServerStatus.Waiting;
					} else {
						status = MinecraftServerStatus.Online;
					}
				} else if (gs == de.ftbastler.bukkitgames.enums.GameState.INVINCIBILITY || gs == de.ftbastler.bukkitgames.enums.GameState.RUNNING) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						status = MinecraftServerStatus.Running;
					} else {
						status = MinecraftServerStatus.Offline;
					}
				} else {
					status = MinecraftServerStatus.Offline;
				}
			} else if (gamename.equalsIgnoreCase("turfwars")) {				
				TurfWarsAPI twa = TurfWars.getAPI();
				be.isach.turfwars.game.GameState gs = twa.getGameState();
				if (gs == be.isach.turfwars.game.GameState.WAITING) {
					status = MinecraftServerStatus.Waiting;
				} else if (gs == be.isach.turfwars.game.GameState.IN_GAME) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						status = MinecraftServerStatus.Running;
					} else {
						status = MinecraftServerStatus.Offline;
					}
				} else {
					status = MinecraftServerStatus.Offline;
				}
			} else {
				status = MinecraftServerStatus.Offline;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendServerStatus(status);
	}

	public void sendServerStatus(MinecraftServerStatus Status) {
		status = Status;
		try {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("servername", Bukkit.getServerName());
			jsonObject.addProperty("online", Bukkit.getOnlinePlayers().size());
			jsonObject.addProperty("status", Status.toString());
			MySQL.updateMinecraftServerStatus(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendToHub(Player p) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("Connect");
			out.writeUTF(SSClient.getInstance().hubserver);
			p.sendPluginMessage(SSClient.getInstance(), "BungeeCord", b.toByteArray());
		} catch (IOException e) {}
	}
}