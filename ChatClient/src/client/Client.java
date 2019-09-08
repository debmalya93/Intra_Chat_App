package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Client {
	DatagramSocket socket;// client socket
	ServerSocket ss;
	private int port;
	private String name;
	private String address;
	private InetAddress ip;
	private Thread send;
	private String ID;
	private String password;

	public Client(String name, String address, int port, String ID,
			String password) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;
		this.password = password;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}
	
	public void openTCPAndListen(){//receiving the file
		Thread t=new Thread(){
			public void run(){
				try{
					ss=new ServerSocket(5001);
					while(true){
						Socket s=ss.accept();
						Thread tt=new Thread(){
							public void run(){
								FileTransfer.recieveFile(s);
							}
						};tt.start();
					}
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, "multiple profiles from one system not allowed");
					System.exit(0);
					//System.out.println("exception in open");
				}finally{
					try {
						ss.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		
		
	}
	
	
	public void sendThroughTCP(String ip,String filePath){//bind to another port and send
		Thread t=new Thread(){
			public void run(){
				Socket s=null;
				try{
					s=new Socket(ip,5001);
					FileTransfer.sendFile(s,filePath);
				}catch(Exception e){
					System.out.println("couldnot bind to the server socket ");
				}
			}
		};
		t.start();
		
	}

	public boolean openConnection() {
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(address);
			socket.setSoTimeout(5000);// connection timed out
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String recieve() throws Exception{
		byte[] data = new byte[1024*60];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		/*try {
			socket.receive(packet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Server error\ntry again later");
		}*/
		socket.receive(packet);
		String msg = new String(packet.getData(), 0, packet.getLength());
		return msg;
	}

	public void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length,
						ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	public void close() {
		synchronized (socket) {
			socket.close();
		}
	}

}
