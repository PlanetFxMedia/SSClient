package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Utils.ChatUtils;

public class LobbyCommandListener implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			Player p = (Player) cs;
			SSClient.sendToHub(p);
		} else {
			ChatUtils.sendMessage(cs, "&4Du musst ein Spieler sein!");
		}
		return true;
	}
}