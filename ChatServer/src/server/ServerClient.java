package server;

import java.net.InetAddress;

public class ServerClient {

	private String name;
	private int port;
	private final String ID;
	private InetAddress address;
	private String uniqueID;
	public int attempt=0;
	
	
	public ServerClient(String name,int port,InetAddress address,String ID,String uniqueID){
		this.name=name;
		this.port=port;
		this.address=address;
		this.ID=ID;
		this.uniqueID=uniqueID;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}


	public String getID() {
		return ID;
	}

	
}
