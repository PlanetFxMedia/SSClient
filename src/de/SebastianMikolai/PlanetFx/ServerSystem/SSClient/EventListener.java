package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (!p.isOp()) {
			if (e.getMessage().contains("pl")) {
				e.setCancelled(true);
				p.sendMessage("Plugins (6): " + ChatColor.GREEN + "Essentials" + ChatColor.RESET + ", " + ChatColor.GREEN + "Minigames" + ChatColor.RESET + ", " + ChatColor.GREEN + "PlanetFxServerSystem" + ChatColor.RESET + ", " + ChatColor.GREEN + "PermissionsEx" + ChatColor.RESET + ", " + ChatColor.GREEN + "WorldEdit" + ChatColor.RESET + ", " + ChatColor.GREEN + "WorldGuard");
			} else if (e.getMessage().contains("lobby")) {
				SSClient.sendToHub(p);
			} else {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		SSClient.getInstance().sendServerStatus(SSClient.getInstance().status);
		e.setJoinMessage(ChatColor.GREEN + "[+] " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " hat das Spiel betreten " + ChatColor.YELLOW + "(" + Bukkit.getOnlinePlayers().size() + "/" + (Bukkit.getMaxPlayers() - 1) + ")");
  		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.setFoodLevel(20);
		p.setTotalExperience(0);
		p.updateInventory();
		p.getWorld().setTime(6000);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		SSClient.getInstance().sendServerStatus(SSClient.getInstance().status);
		e.setQuitMessage(ChatColor.RED + "[-] " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " hat das Spiel verlassen " + ChatColor.YELLOW + "(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + (Bukkit.getMaxPlayers() - 1) + ")");
	}
}