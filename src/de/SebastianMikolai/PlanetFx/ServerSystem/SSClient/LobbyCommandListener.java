package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Utils.ChatUtils;

public class LobbyCommandListener implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].contains("all")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					SSClient.sendToHub(player);
				}
			}
		} else {
			if (cs instanceof Player) {
				Player p = (Player) cs;
				SSClient.sendToHub(p);
			} else {
				ChatUtils.sendMessage(cs, "&4Du musst ein Spieler sein!");
			}
		}
		return true;
	}
}