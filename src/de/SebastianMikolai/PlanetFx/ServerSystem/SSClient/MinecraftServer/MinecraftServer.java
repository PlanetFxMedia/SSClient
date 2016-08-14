package de.SebastianMikolai.PlanetFx.ServerSystem.SSClient.MinecraftServer;

public class MinecraftServer {

	private final String BungeeCordServername;
	private final int Port;
	private final String Map;
	private final String Modi;
	private int OnlinePlayer;
	private MinecraftServerStatus Status;
	
	public MinecraftServer(String _BungeeCordServername, int _Port, String _Map, String _Modi) {
		BungeeCordServername = _BungeeCordServername;
		Port = _Port;
		Map = _Map;
		Modi = _Modi;
		OnlinePlayer = 0;
		Status = MinecraftServerStatus.Offline;
	}
	
	public String getBungeeCordServername() {
		return BungeeCordServername;
	}
	
	public int getPort() {
		return Port;
	}
	
	public int getMaxPlayer() {
		int i1 = Integer.valueOf(Modi.split("x")[0]);
		int i2 = Integer.valueOf(Modi.split("x")[1]);
		int erg = i1 * i2;
		return erg;
	}
	
	public String getMap() {	
		return Map;
	}
	
	public String getModi() {
		return Modi;
	}
	
	public int getOnlinePlayer() {
		return OnlinePlayer;
	}
	
	public MinecraftServerStatus getStatus() {	
		return Status;
	}
	
	public void setOnlinePlayer(int _OnlinePlayer) {
		OnlinePlayer = _OnlinePlayer;
	}
	
	public void setStatus(MinecraftServerStatus _Status) {
		Status = _Status;
	}
}