package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable{

	private List<ServerClient> clients=new ArrayList<ServerClient>();//online clients
	private List<String> clientResponse=new ArrayList<String>();
	private List<ServerClient> allClient=new ArrayList<ServerClient>();//all the clients
	
	private DatagramSocket socket;
	//private ServerSocket ss;
	private int port;
	private boolean running=false;
	private Thread run,send,recieve,manage;
	
	private final int MAX_ATTEMPT=10;
	
	public Server(int port){
		this.port=port;
		try {
			socket=new DatagramSocket(port);
		}catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run=new Thread(this,"Server");
		System.out.println("server started....");
		run.start();
	}
	
	
	public void run(){
		running=true;
		getAllClient();//list of all client
		manageClient();//(Thread)
		recieve();//(Thread)
		Scanner scanner=new Scanner(System.in);
		while(running){
			String str=scanner.nextLine();
			if(!str.startsWith("/")){
				continue;
			}else if(str.equalsIgnoreCase("/online_clients")){
				if(clients.size()<=0){
					System.out.println("------------------------------------------------------");
					System.out.println("no clients online");
					System.out.println("------------------------------------------------------");
					continue;
				}
				System.out.println("------------------------------------------------------");
				for(ServerClient sc:clients){
					System.out.println("[emailID:"+sc.getID()+"] [Name:"+sc.getName()+"] [IP:"+sc.getAddress().toString().substring(1)+"]");
				}
				System.out.println("------------------------------------------------------");
			}else if(str.startsWith("/kick")){
				boolean t=false;
				String id=str.split(" ")[1];
				disconnect(id,false);
				/*for(ServerClient sc:clients){
					if(sc.getID().equals(id)){
						t=true;
						disconnect(id,false);
						break;
					}
				}
				if(!t){
					System.out.println("not found");
				}*/
			}else if(str.equalsIgnoreCase("/noc")){
				System.out.println("----------------------------------");
				System.out.println("total no. of regisered users :"+(clients.size()+allClient.size()));
				System.out.println("----------------------------------");
			}else{
				continue;
			}
		}
	}
	
	
	
	
	private void manageClient(){
		manage=new Thread("Manage"){
			public void run(){
				while(running){
					sendToAll("/i/");//to check the connectivity
					sendStatus();//send online users
					sendOfflineClientsStatus();//send the status of offline users
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i=0;i<clients.size();i++){
						ServerClient c=clients.get(i);
						if(!clientResponse.contains(c.getID())){
							if(c.attempt>=MAX_ATTEMPT){
								disconnect(c.getID(),false);
							}else{
								c.attempt++;
							}
						}else{
							clientResponse.remove(c.getID());
							c.attempt=0;
						}
					}
				}
			}
		};
		manage.start();
	}
	
	private void sendStatus(){//send the names of online clients
		
		if(clients.size()<=0){
			return;
		}
		String user="/u/";
		for(int i=0;i<clients.size();i++){
			ServerClient c=clients.get(i);
			user=user+c.getName()+"-"+c.getID()+"-"+ "/n/";
		}
		sendToAll(user);
	}
	
	
	private void sendOfflineClientsStatus(){
		offlineClient();
		String offUser="/off/";
		if(allClient.size()<=0){
			sendToAll(offUser+" /n/");
			return;
		}
		
		for(int i=0;i<allClient.size();i++){
			ServerClient c=allClient.get(i);
			offUser=offUser+c.getName()+"-"+c.getID()+"-"+ "/n/";
		}
		sendToAll(offUser);
	}
	private void recieve(){
		recieve=new Thread("Recieve"){
			public void run(){
				while(running){
					byte[] data=new byte[1024*60];
					DatagramPacket packet=new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		recieve.start();
	}
	
	private void send(final byte[] data,final InetAddress address,final int port){
		send=new Thread("Send"){
			public void run(){
				DatagramPacket packet=new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	private void sendToAll(String msg){//send to all the online clients
		for(int i=0;i<clients.size();i++){
			ServerClient client=clients.get(i);
			send(msg.getBytes(),client.getAddress(),client.getPort());
		}
	}
	
	private void process(DatagramPacket packet){
		String msg=new String(packet.getData(),0,packet.getLength());
		if(msg.startsWith("/m/")){//message packet /m/toID/n/fromID/n/from msg
			sendToClient(msg,packet);
		}else if(msg.startsWith("/d/")){//disconnection packet /d/id
			disconnect(msg.substring(3, msg.length()),true);
		}else if(msg.startsWith("/i/")){//reply to the ping
			clientResponse.add(msg.substring(3,msg.length()));
		}else if(msg.startsWith("/a/")){//authentication /a/id/n/pass
			String ID=msg.split("/a/|/n/")[1];
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).getID().equals(ID)){
					String str="/x/";
					send(str.getBytes(),packet.getAddress(),packet.getPort());
					return;
				}
			}
			String pass=msg.split("/n/")[1];
			String name_id=MessageDaoImpl.authenticate(ID,pass);//returns name/n/uniqueid
			if(name_id.equals("invalid")){
				String s="/z/";//not registered user
				send(s.getBytes(),packet.getAddress(),packet.getPort());
			}else{
				String uniqueid=name_id.split("/n/")[1];
				String name=name_id.split("/n/")[0];
				String s="/c/"+name;
				clients.add(new ServerClient(name,packet.getPort(),packet.getAddress(),ID,uniqueid));//adding online client
				send(s.getBytes(),packet.getAddress(), packet.getPort());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendToAll("/m/xx/n/"+ID+"/n//j/");//user just became online
			}
		}else if(msg.startsWith("/r/")){
			String id=msg.split("/r/|/n/")[1];
			String name=msg.split("/n/")[1];
			String pass=msg.split("/n/")[2];
			String status=MessageDaoImpl.register(name, id, pass);//   /r/uniqueid
			if(status.startsWith("/nr/")){
				send(status.getBytes(),packet.getAddress(),packet.getPort());
				return;
			}
			String uniqueid=status.split("/r/")[1];
			send(status.getBytes(),packet.getAddress(),packet.getPort());
			if(status.startsWith("/r/")){
				allClient.add(new ServerClient(name,packet.getPort(),packet.getAddress(),id,uniqueid));
			}
			
		}else if(msg.startsWith("/s/")){ // /s/fromID/n/toID
			String fromID=msg.split("/s/|/n/")[1];//my id
			String toID=msg.split("/n/")[1];//person to send
			ServerClient c=null;//to member email id
			ServerClient c1=null;//from member email id i.e my id
			boolean flag=false;
			for(int i=0;i<clients.size();i++){
				c=clients.get(i);
				if(c.getID().equals(toID)){
					flag=true;
					break;
				}
			}
			if(flag==false){//since it is in offline client array list
				for(int i=0;i<allClient.size();i++){
					c=allClient.get(i);
					if(c.getID().equals(toID)){
						break;
					}
				}
				
			}
			
			
			for(int i=0;i<clients.size();i++){
				c1=clients.get(i);
				if(c1.getID().equals(fromID)){
					break;
				}
			}
		
			String chatHistory="/s/"+fromID+"/n/"+toID+"/n/"+MessageDaoImpl.readMessage(Long.parseLong(c.getUniqueID()),Long.parseLong(c1.getUniqueID()));
			send(chatHistory.getBytes(),packet.getAddress(),packet.getPort());
		}else if(msg.startsWith("/l/")){// /l/to/n/from
			String id=msg.split("/l/|/n/")[1];
			String from=msg.split("/n/")[1];
			
			String status="/m/"+from+"/n/"+id+"/n/"+MessageDaoImpl.lastSeen(id);
			send(status.getBytes(),packet.getAddress(), packet.getPort());
		}else if(msg.startsWith("/dp/")){
			String id=msg.split("/dp/|/n/")[1];
			String pic=msg.split("/n/")[1];
			try {
				ImageOperation.writeImage(pic.getBytes(),id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}else if(msg.startsWith("/dpretrieve/")){
			try {
				byte[] b=ImageOperation.readImage(msg.substring(12));
				String str=new String(b,0,b.length);
				str="/dp/"+str;
				send(str.getBytes(),packet.getAddress(),packet.getPort());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(msg.startsWith("/f/")){
			String id=msg.split("/f/|/n/")[1];
		
			for(ServerClient sc:clients){
				if(sc.getID().equals(id)){
					send(msg.getBytes(),sc.getAddress(),sc.getPort());
				}
			}
		}else if(msg.startsWith("/ff/")){
			String toId=msg.split("/n/")[1];
			msg=msg+"/n/"+packet.getAddress().toString();
			for(ServerClient sc:clients){
				if(sc.getID().equals(toId)){
					send(msg.getBytes(),sc.getAddress() ,sc.getPort());
				}
			}
			
		}else if(msg.startsWith("/call/")){
			String id=msg.split("/call/|/n/")[1];
			for(ServerClient sc:clients){
				if(sc.getID().equals(id)){
					send(msg.getBytes(),sc.getAddress(),sc.getPort());
				}
			}
		}else if(msg.startsWith("/callAck/")){
			String toId=msg.split("/n/")[1];
			msg=msg+"/n/"+packet.getAddress().toString();
			for(ServerClient sc:clients){
				if(sc.getID().equals(toId)){
					send(msg.getBytes(),sc.getAddress() ,sc.getPort());
				}
			}
			
		}
	}
	
	
	private List<ServerClient> getAllClient(){//to get all the clients from the database
		String sql="select * from users";
		//String name=null;
		PreparedStatement pst=null;
		Connection con=null;
		ResultSet rs;
		try{
			con=ConnectionFactory.getConnection();
			pst=con.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()){
				ServerClient c=new ServerClient(rs.getString("name"),0,null,rs.getString("id"),rs.getString("uniqueid"));
				allClient.add(c);
			}
		}catch(Exception e){	
			e.printStackTrace();
		}
		finally{
			try {
				pst.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return allClient;
	}
	
	
	

	public void offlineClient(){
		for(int i=0;i<allClient.size();i++){
			for(int j=0;j<clients.size();j++){
				if(allClient.get(i).getID().equals(clients.get(j).getID())){
					allClient.remove(i);
					break;
				}
			}
		}
	}
	
	private void sendToClient(String msg,DatagramPacket packet){// /m/to/n/fromID/n/msg
		String toID=msg.split("/m/|/n/")[1];
		String fromID=msg.split("/n/")[1];
		String message=msg.split("/n/")[2];
		ServerClient c=null;//to member email id
		ServerClient c1=null;//from member email id
		boolean flag=false;
		for(int i=0;i<clients.size();i++){
			c=clients.get(i);
			if(c.getID().equals(toID)){
				flag=true;
				break;
			}
		}
		
		if(flag==false){
			for(int i=0;i<allClient.size();i++){
				c=allClient.get(i);
				if(c.getID().equals(toID)){
					break;
				}
			}
		}
		
		
		for(int i=0;i<clients.size();i++){
			c1=clients.get(i);
			if(c1.getID().equals(fromID)){
				break;
			}
		}
		MessageDaoImpl.writeMessage(Long.parseLong(c1.getUniqueID()),Long.parseLong(c.getUniqueID()), message);
		if(flag==false){
			return;
		}
		send(msg.getBytes(),c.getAddress(),c.getPort());
	}
	private void disconnect(String id,boolean status){
		boolean b=false;
		ServerClient c=null;
		InetAddress ip=null;
		int port=0;
		for(int i=0;i<clients.size();i++){
			if(clients.get(i).getID().equals(id)){
				c=clients.get(i);
				ip=c.getAddress();
				port=c.getPort();
				c.setAddress(null);
				c.setPort(0);
				allClient.add(c);
				clients.remove(i);
				b=true;
				break;
			}
		}
		if(!b){
			//System.out.println("no c found");
			return;
		}
		sendToAll("/m/xx/n/"+c.getID()+"/n//b/");// sending by by to all
		String message="";
		if(status){
			System.out.println(message="Client "+c.getName()+" got disconnected");
			MessageDaoImpl.updateTime(c.getID()); 
		}else{
			System.out.println(message="Client "+c.getName()+" got disconnected abnormally");
			MessageDaoImpl.updateTime(c.getID());
			//send("/sc/".getBytes(),ip, port);//closing the socket of the client
		}
		
	}
	
	
}
