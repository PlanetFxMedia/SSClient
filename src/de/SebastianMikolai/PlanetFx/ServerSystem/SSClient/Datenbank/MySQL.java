package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;

import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.SSClient;
import de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.MinecraftServer.MinecraftServer;

public class MySQL {

	public static Connection c = null;
	public static Statement database;
	
	public static void Connect() {
		try {
			c = DriverManager.getConnection("jdbc:mysql://" + SSClient.getInstance().getConfig().getString("database.host") + ":" + 
					SSClient.getInstance().getConfig().getInt("database.port") + "/" + SSClient.getInstance().getConfig().getString("database.db") + 
					"?user=" + SSClient.getInstance().getConfig().getString("database.user") + "&password=" + SSClient.getInstance().getConfig().getString("database.password"));
			database = c.createStatement();
			SSClient.getInstance().getLogger().info("Die Verbindung zur Datenbank wurde hergestellt!");
		} catch (Exception e) {
			System.exit(0);
		}
	}

	public static void LadeTabellen() {
		try {
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SHOW TABLES LIKE 'MinecraftServer'");
			if (rss.next()) {
				SSClient.getInstance().getLogger().info("Die Tabelle MinecraftServer wurde geladen!");
			} else {
				int rs = stmt.executeUpdate("CREATE TABLE MinecraftServer (id INTEGER PRIMARY KEY AUTO_INCREMENT, BungeeCordServername TEXT, Port INTEGER, Map TEXT, Modi TEXT)");
				SSClient.getInstance().getLogger().info("Die Tabelle MinecraftServer wurde erstellt! (" + rs + ")");
			}
			rss = stmt.executeQuery("SHOW TABLES LIKE 'ServerStatus'");
			if (rss.next()) {
				SSClient.getInstance().getLogger().info("Die Tabelle ServerStatus wurde geladen!");
			} else {
				int rs = stmt.executeUpdate("CREATE TABLE ServerStatus (id INTEGER PRIMARY KEY AUTO_INCREMENT, BungeeCordServername TEXT, Online INTEGER, Status TEXT)");
				SSClient.getInstance().getLogger().info("Die Tabelle ServerStatus wurde erstellt! (" + rs + ")");
			}
		} catch (SQLException e) {
			System.exit(0);
		}
	}
	
	public static void updateMinecraftServerStatus(JsonObject jsonObject) {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("UPDATE ServerStatus SET Online='" + jsonObject.get("online").getAsInt() + "', Status='" + jsonObject.get("status").getAsString() + "' WHERE BungeeCordServername='" + jsonObject.get("servername").getAsString() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static MinecraftServer getMinecraftServer(String BungeeCordServername) {
		MinecraftServer mcs = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SELECT * FROM MinecraftServer");
			while (rss.next()) {
				mcs = new MinecraftServer(rss.getString("BungeeCordServername"), rss.getInt("Port"), rss.getString("Map"), rss.getString("Modi"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mcs;
	}
}