package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {
	
	private static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Minigames" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	public static void sendMessage(Player p, String msg) {
		p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public static void sendMessage(CommandSender cs, String msg) {
		cs.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}
}