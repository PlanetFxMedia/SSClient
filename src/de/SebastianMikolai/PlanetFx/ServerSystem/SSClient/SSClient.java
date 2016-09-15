package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import be.isach.turfwars.TurfWars;
import be.isach.turfwars.api.TurfWarsAPI;

import de.SebastianMikolai.PlanetFx.IceHockey.API.HGAPI;
import de.SebastianMikolai.PlanetFx.IceHockey.API.Utils.GameState;
import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Datenbank.MySQL;
import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.MinecraftServer.MinecraftServerStatus;
import de.ftbastler.bukkitgames.api.BukkitGamesAPI;

public class SSClient extends JavaPlugin {
	
	public static SSClient instance;
	public String gamename;
	public Location bedwars;
	public MinecraftServerStatus status;
	public int online;
	
	public static SSClient getInstance() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		gamename = getConfig().getString("game");
		if (gamename.equalsIgnoreCase("bedwars")) {
			bedwars = new Location(Bukkit.getWorld(getConfig().getString("signs.bedwars.world")), getConfig().getInt("signs.bedwars.x"), getConfig().getInt("signs.bedwars.y"), getConfig().getInt("signs.bedwars.z"));
		} else if (gamename.equalsIgnoreCase("tntrun")) {
			bedwars = new Location(Bukkit.getWorld(getConfig().getString("signs.tntrun.world")), getConfig().getInt("signs.tntrun.x"), getConfig().getInt("signs.tntrun.y"), getConfig().getInt("signs.tntrun.z"));
		}
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		MySQL.LadeTabellen();
		MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
		getCommand("lobby").setExecutor(new LobbyCommandListener());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new EventListener(), this);
		updateMinecraftServerStatus();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {

			@Override
			public void run() {
				int oldonline = online;
				MinecraftServerStatus oldstatus = status;		
				if (gamename.equalsIgnoreCase("bedwars")) {
					try {
						Sign sign = (Sign) bedwars.getBlock().getState();
						String l0 = ChatColor.stripColor(sign.getLine(0));
						String l3 = ChatColor.stripColor(sign.getLine(3));
						if (l0.equalsIgnoreCase("[Bedwars]")) {
							if (l3.equalsIgnoreCase("Warten ...")) {
								status = MinecraftServerStatus.Online;
							} else if (l3.equalsIgnoreCase("Gestartet!")) {
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
						status = MinecraftServerStatus.Online;
					}					
				} else if (gamename.equalsIgnoreCase("hungergames")) {
					try {
						BukkitGamesAPI bga = BukkitGamesAPI.getApi();
						de.ftbastler.bukkitgames.enums.GameState gs = bga.getCurrentGameState();
						if (gs == de.ftbastler.bukkitgames.enums.GameState.PREGAME) {
							status = MinecraftServerStatus.Online;
						} else if (gs == de.ftbastler.bukkitgames.enums.GameState.INVINCIBILITY || gs == de.ftbastler.bukkitgames.enums.GameState.RUNNING) {
							if (Bukkit.getOnlinePlayers().size() > 0) {
								status = MinecraftServerStatus.Running;
							} else {
								status = MinecraftServerStatus.Offline;
							}
						} else {
							status = MinecraftServerStatus.Offline;
						}
					} catch (Exception e) {
						status = MinecraftServerStatus.Online;
					}
				} else if (gamename.equalsIgnoreCase("turfwars")) {				
					try {
						TurfWarsAPI twa = TurfWars.getAPI();
						be.isach.turfwars.game.GameState gs = twa.getGameState();
						if (gs == be.isach.turfwars.game.GameState.WAITING) {
							status = MinecraftServerStatus.Online;
						} else if (gs == be.isach.turfwars.game.GameState.IN_GAME) {
							if (Bukkit.getOnlinePlayers().size() > 0) {
								status = MinecraftServerStatus.Running;
							} else {
								status = MinecraftServerStatus.Offline;
							}
						} else {
							status = MinecraftServerStatus.Offline;
						}
					} catch (Exception e) {
						status = MinecraftServerStatus.Online;
					}
				} else if (gamename.equalsIgnoreCase("icehockey")) {
					try {
						GameState gs = HGAPI.getGameState("IceHockey");
						if (gs == GameState.Online) {
							status = MinecraftServerStatus.Online;
						} else if (gs == GameState.Running) {
							if (Bukkit.getOnlinePlayers().size() > 0) {
								status = MinecraftServerStatus.Running;
							} else {
								status = MinecraftServerStatus.Offline;
							}
						} else if (gs == GameState.Offline) {
							status = MinecraftServerStatus.Offline;
						} else {
							status = MinecraftServerStatus.Offline;
						}
					} catch (Exception e) {
						status = MinecraftServerStatus.Online;
					}
				} else if (gamename.equalsIgnoreCase("tntrun")) {
					try {
						Sign sign = (Sign) bedwars.getBlock().getState();
						String l0 = ChatColor.stripColor(sign.getLine(0));
						String l3 = ChatColor.stripColor(sign.getLine(3));
						if (l0.equalsIgnoreCase("TNTRun")) {
							if (l3.equalsIgnoreCase("Waiting")) {
								status = MinecraftServerStatus.Online;
							} else if (l3.equalsIgnoreCase("Starting")) {
								status = MinecraftServerStatus.Online;
							} else if (l3.equalsIgnoreCase("IN-GAME")) {
								status = MinecraftServerStatus.Running;
							} else if (l3.equalsIgnoreCase("Ending")) {
								status = MinecraftServerStatus.Offline;
							} else {
								status = MinecraftServerStatus.Offline;
							}
						} else {
							status = MinecraftServerStatus.Offline;
						}
					} catch (Exception e) {
						status = MinecraftServerStatus.Online;
					}
					Bukkit.getLogger().info(status.name());
				}
				online = Bukkit.getOnlinePlayers().size();
				if (oldstatus != status) {
					updateMinecraftServerStatus();
				} else if (oldonline != online) {
					updateMinecraftServerStatus();
				}
			}
		}, 20L, 20L);
	}
	
	public void onDisable() {
		MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelAllTasks();
	}
	
	public void updateMinecraftServerStatus() {
		online = Bukkit.getOnlinePlayers().size();
		if (gamename.equalsIgnoreCase("bedwars")) {
			try {
				Sign sign = (Sign) bedwars.getBlock().getState();
				String l0 = ChatColor.stripColor(sign.getLine(0));
				String l3 = ChatColor.stripColor(sign.getLine(3));
				if (l0.equalsIgnoreCase("[Bedwars]")) {
					if (l3.equalsIgnoreCase("Warten ...")) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
					} else if (l3.equalsIgnoreCase("Gestartet!")) {
						if (Bukkit.getOnlinePlayers().size() > 0) {
							MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Running);
						} else {
							MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
						}
					} else {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					}
				} else {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				}
			} catch (Exception e) {
				MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
			}
		} else if (gamename.equalsIgnoreCase("hungergames")) {
			try {
				BukkitGamesAPI bga = BukkitGamesAPI.getApi();
				de.ftbastler.bukkitgames.enums.GameState gs = bga.getCurrentGameState();
				if (gs == de.ftbastler.bukkitgames.enums.GameState.PREGAME) {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
				} else if (gs == de.ftbastler.bukkitgames.enums.GameState.INVINCIBILITY || gs == de.ftbastler.bukkitgames.enums.GameState.RUNNING) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Running);
					} else {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					}
				} else {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				}
			} catch (Exception e) {
				MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
			}
		} else if (gamename.equalsIgnoreCase("turfwars")) {				
			try {
				TurfWarsAPI twa = TurfWars.getAPI();
				be.isach.turfwars.game.GameState gs = twa.getGameState();
				if (gs == be.isach.turfwars.game.GameState.WAITING) {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
				} else if (gs == be.isach.turfwars.game.GameState.IN_GAME) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Running);
					} else {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					}
				} else {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				}
			} catch (Exception e) {
				MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
			}
		} else if (gamename.equalsIgnoreCase("icehockey")) {
			try {
				GameState gs = HGAPI.getGameState("IceHockey");
				if (gs == GameState.Online) {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
				} else if (gs == GameState.Running) {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Running);
					} else {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					}
				} else if (gs == GameState.Offline) {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				} else {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				}
			} catch (Exception e) {
				MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
			}
		} else if (gamename.equalsIgnoreCase("tntrun")) {
			try {
				Sign sign = (Sign) bedwars.getBlock().getState();
				String l0 = ChatColor.stripColor(sign.getLine(0));
				String l3 = ChatColor.stripColor(sign.getLine(3));
				if (l0.equalsIgnoreCase("TNTRun")) {
					if (l3.equalsIgnoreCase("Waiting")) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
					} else if (l3.equalsIgnoreCase("Starting")) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
					} else if (l3.equalsIgnoreCase("IN-GAME")) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Running);
					} else if (l3.equalsIgnoreCase("Ending")) {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					} else {
						MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
					}
				} else {
					MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
				}
			} catch (Exception e) {
				MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Online);
			}
		} else {
			MySQL.updateMinecraftServerStatus(MinecraftServerStatus.Offline);
		}
	}
	
	public static void sendToHub(Player p) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("Connect");
			out.writeUTF("mg");
			p.sendPluginMessage(SSClient.getInstance(), "BungeeCord", b.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}