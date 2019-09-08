package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Position;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class ClientList extends JFrame implements Runnable {
	private ArrayList<EachClientQueue> clientQueue=new ArrayList<EachClientQueue>();
	
	private JPanel contentPane;
	private JList<String> list;
	private Thread listen,clientlist;
	private Client client;
	private boolean running=true;
	private JList<String> list_2;
	private boolean rendering=false;
	private Profile p=null;
	
	//private Clock clock;
	private RecordAndPlay recordAndPlay;
	/**
	 * Create the frame.
	 */
	public ClientList(Client c) {
		client=c;
		setTitle(client.getName());
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 393);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setBackground(new Color(102, 224, 255));
		contentPane.setBackground(new Color(153, 255, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 42, 414, 120);
		contentPane.add(scrollPane);
		clientlist=new Thread(this);
		
		list = new JList<String>();
		scrollPane.setViewportView(list);
		
		JLabel lblOnlineFriends = new JLabel("Online Friends...");
		lblOnlineFriends.setFont(new Font("Monotype Corsiva",Font.BOLD,15));
		lblOnlineFriends.setBounds(28, 23, 139, 14);
		contentPane.add(lblOnlineFriends);
		
		JLabel lblOfflineFriends = new JLabel("Offline Friends...");
		lblOfflineFriends.setFont(new Font("Monotype Corsiva",Font.BOLD,15));
		lblOfflineFriends.setBounds(28, 202, 139, 14);
		contentPane.add(lblOfflineFriends);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 225, 414, 120);
		contentPane.add(scrollPane_1);
		
		list_2 = new JList<String>();
		scrollPane_1.setViewportView(list_2);
		
		ImageIcon i=new ImageIcon(this.getClass().getResource("/img/on.png"));
		JLabel label = new JLabel("");
		label.setBounds(10, 17, 13, 20);
		label.setIcon(i);
		contentPane.add(label);
		
		ImageIcon i2=new ImageIcon(this.getClass().getResource("/img/off.png"));
		JLabel label_1 = new JLabel("");
		label_1.setBounds(10, 202, 17, 14);
		label_1.setIcon(i2);
		contentPane.add(label_1);
		
		recordAndPlay=new RecordAndPlay();//opening port for audio calling
		recordAndPlay.openTCPForListeningToSound();
		recordAndPlay.openTCPForSendingSound();
		
		
		
		
		list.setFont(new Font("Rockwell",Font.BOLD, 15));
		list.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            // Double-click detected
		        	rendering=false;
		            int index = list.locationToIndex(evt.getPoint());
		            String str=(String)list.getModel().getElementAt(index);
		            String toName=str.split("-")[0];//debmalya-cyberdeb.93@gmail.com-
		            String toID=str.split("-")[1];
		            String retrive="/s/"+client.getID()+"/n/"+toID;// /s/fromID/n/toID for retrieving
		            try{
		    			EachClientQueue q=null;
		    			for(int i=0;i<clientQueue.size();i++){
		    				q=clientQueue.get(i);
		    				if(q.ID.equals(toID)){
		    					q.msgQueue.clear();
		    					break;
		    				}
		    			}	
		    		}catch(Exception e){
		    			System.out.println("exception");
		    		}
		            client.send(retrive.getBytes());  
		            new ClientFrame(toName,toID,client,setQueue(toID),false).setVisible(true);
		        } 
		    }
		});
		
		list_2.setFont(new Font("rockwell",Font.BOLD, 15));
		JButton btnProfile = new JButton(new ImageIcon(this.getClass().getResource("/img/pro.png")));
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				p.setVisible(true);
				//p.retrieve();
			}
		});
		btnProfile.setBounds(381, 11, 43, 26);
		contentPane.add(btnProfile);
		list_2.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        list_2 = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            // Double-click detected
		            int index = list_2.locationToIndex(evt.getPoint());
		            String str=(String)list_2.getModel().getElementAt(index);
		            String toName=str.split("-")[0];//debmalya-cyberdeb.93@gmail.com-
		            String toID=str.split("-")[1];
		            String retrive="/s/"+client.getID()+"/n/"+toID;// /s/fromID/n/toID for retrieving
		            client.send(retrive.getBytes());
		            new ClientFrame(toName,toID,client,setQueue(toID),true).setVisible(true);
		        } 
		    }
		});
		
		clientlist.start();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				String disconnect="/d/"+client.getID();
				client.send(disconnect.getBytes());
				running=false;
			}
			
		});
		
		p=new Profile(client,true);
		
	}
	private BlockingDeque<String> setQueue(String ID){
		EachClientQueue q;
		for(int i=0;i<clientQueue.size();i++){
			q=clientQueue.get(i);
			if(q.ID.equals(ID)){
				return q.msgQueue;	
			}
		}
		
		BlockingDeque<String> b=new LinkedBlockingDeque<String>(10);
		EachClientQueue c=new EachClientQueue(ID, b);
		clientQueue.add(c);
		return b;
	}
	
	
	public void update(String[] u){
		list.setListData(u);
	}
	
	public void updateOffClient(String[] u){
		list_2.setListData(u);
	}
	
	
	
	public void listen(){
		listen=new Thread(){
			public void run(){
				while(running){
					String msg=null;
					try{
						msg=client.recieve();
					}catch(Exception e){
						running=false;
						JOptionPane.showMessageDialog(null,"server error! try again latter");
						System.exit(0);
						
					}
					//String msg=client.recieve();
					if(msg.startsWith("/c/")){
						System.out.print("connected");
					}else if(msg.startsWith("/i/")){//receive to ping packet from server 
						String ping="/i/"+client.getID();
						client.send(ping.getBytes());
					}else if(msg.startsWith("/u/")){//online clients
						String u[]=msg.split("/u/|/n/");
						update(Arrays.copyOfRange(u,1,u.length));
					}else if(msg.startsWith("/m/")){// /m/ID/n/fromID/n/name : msg
						process(msg);
					}else if(msg.startsWith("/off/")){
						String offUser[]=msg.split("/off/|/n/");
						updateOffClient(Arrays.copyOfRange(offUser,1,offUser.length));
					}else if(msg.startsWith("/s/")){//retrieved history
						processRetrivedMsg(msg);
					}else if(msg.equals("/sc/")){
						running=false;
						client.close();
					}else if(msg.startsWith("/dp/")){
						msg=msg.substring(4);
						p.setImage(msg.getBytes());
					}else if(msg.startsWith("/f/")){
						String from=msg.split("/n/")[1];
						msg=msg.split("/f/")[1];
						msg="/ff/"+msg;
						int reply=JOptionPane.showConfirmDialog(null,"incoming file from "+from,"Response",JOptionPane.YES_NO_OPTION);
						if(reply==JOptionPane.NO_OPTION){
							continue;
						}
						client.send(msg.getBytes());
						
					}else if(msg.startsWith("/ff/")){
						String ip=msg.split("/n/")[3];
						client.sendThroughTCP(ip.split("/")[1],msg.split("/n/")[2]);
					}else if(msg.startsWith("/call/")){
						String from=msg.split("/n/")[1];
						msg=msg.split("/call/")[1];
						msg="/callAck/"+msg;
						
						Thread t=new Thread(){
							public void run(){
								 // open the sound file as a Java input stream
								 try {
									Player playMP3 = new Player(this.getClass().getResourceAsStream("/ringtone/ring1.mp3"));
									playMP3.play();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};t.start();   
						int reply=JOptionPane.showConfirmDialog(null,"incoming call from "+from,"CALL REQUEST",JOptionPane.YES_NO_OPTION);
						t.stop();
						if(reply==JOptionPane.YES_OPTION){
							client.send(msg.getBytes());
							recordAndPlay.flag=true;
							recordAndPlay.setVisible(true);
						}
						
					}else if(msg.startsWith("/callAck/")){
						String ip=msg.split("/n/")[2];
						recordAndPlay.sendThroughTCPSound(ip.split("/")[1]);
						recordAndPlay.recieveThroughTCPSound(ip.split("/")[1]);
						recordAndPlay.flag=true;
						recordAndPlay.setVisible(true);
					}
				}
			}
		};
		listen.start();
	}
	private void process(String msg){
		try{
			String txt=msg.split("/n/")[2];
			String fromID=msg.split("/n/")[1];
			String name=msg.split(" |/n/")[2];
			int index = list.getNextMatch(name+"-"+fromID+"-",0,Position.Bias.Forward);
			
			//incomingMsgFrom.add(name+"-"+fromID+"-");
			Thread t=new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					rendering=true;
					while(rendering){
						list.setSelectedIndex(index);
						list.setSelectionBackground(Color.GREEN);
						try{
							Thread.currentThread().sleep(1000);
						}catch(Exception e){
							
						}
					}
					
				}
			});t.start();
			
			/*Point p=list.indexToLocation(index);
			MouseEvent evt=new MouseEvent(list, MouseEvent.MOUSE_CLICKED, 0,0,p.x,p.y,2,true);
			list.getMouseListeners()[0].mouseClicked(evt);*/
			EachClientQueue q=null;
			
			for(int i=0;i<clientQueue.size();i++){
				q=clientQueue.get(i);
				if(q.ID.equals(fromID)){
					q.msgQueue.put(txt);
					break;
				}
			}
			
		}catch(Exception e){
			//System.out.println("exception!!!!");
		}
		
	}
	
	private void processRetrivedMsg(String msg){
		try{
			String txt=msg.split("/n/")[2];
			String fromID=msg.split("/n/")[1];
			EachClientQueue q=null;
			for(int i=0;i<clientQueue.size();i++){
				q=clientQueue.get(i);
				if(q.ID.equals(fromID)){
					q.msgQueue.addFirst(txt);
					break;
				}
			}
			
		}catch(Exception e){
			System.out.println("exception");
		}
		
		
	}
	public void run(){
		listen();
	}
}
