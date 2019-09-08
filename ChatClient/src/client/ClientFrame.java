package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.BlockingDeque;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientFrame extends JFrame implements Runnable{
	

	private JPanel contentPane;
	private JTextField textSms;
	private JTextArea txtHistory;
	private JScrollPane pane;
	JLabel lblLast;
	
	private String sendToName,sendToID;
	private Thread listen;
	Client c;
	private boolean running=false;
	BlockingDeque<String> queue;
	private DefaultCaret caret;
	private boolean flag=false;
	private JButton btnProfile;
	private Profile p=null;
	/**
	 * Create the frame.
	 */
	public ClientFrame(String sendToName,String sendToID,final Client c,BlockingDeque<String> queue,boolean flag) {
		//true flag for offline user
		//kesto open porel's chat--- sendTo=p@gmail.com sendToName=porel c.getID=k@gmail.com
		super(sendToName);
		this.sendToID=sendToID;
		this.sendToName=sendToName;
		this.c=c;
		this.queue=queue;
		this.flag=flag;
		running=true;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 481, 388);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(102, 224, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		p=new Profile(new Client(sendToName,null, 0,sendToID,null),false);
		
		txtHistory = new JTextArea();
		txtHistory.setBounds(10, 60, 455, 258);
		txtHistory.setFont(new Font("Monotype Corsiva",Font.PLAIN, 18));
		txtHistory.setEditable(false);
		txtHistory.setLineWrap(true);
		caret=(DefaultCaret)txtHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		pane=new JScrollPane(txtHistory);
		pane.setBounds(10, 60, 455, 258);
		contentPane.add(pane);
		
		textSms = new JTextField();
		
		Thread t=new Thread(this);
		t.start();
		
		textSms.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					if(textSms.getText().equals("")){
						return;
					}
					sendTo(textSms.getText());
					txtHistory.append(c.getName()+" : "+textSms.getText()+"\n");
					textSms.setText("");
				}
			}
		});
		textSms.setBounds(10, 329, 378, 20);
		contentPane.add(textSms);
		textSms.setColumns(10);
		textSms.setFont(new Font(Font.SANS_SERIF,Font.BOLD, 12));
		textSms.requestFocusInWindow();
		
		JButton btnSend = new JButton(new ImageIcon(this.getClass().getResource("/img/send1.png")));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendTo(textSms.getText());
				txtHistory.setText(c.getName()+" : "+textSms.getText()+"\n");
				textSms.setText("");
			}
		});
		btnSend.setBounds(401, 329, 64, 20);
		contentPane.add(btnSend);
		
		btnProfile = new JButton(new ImageIcon(this.getClass().getResource("/img/pro.png")));
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				p.setVisible(true);
			}
		});
		btnProfile.setBounds(10, 14, 45, 35);
		contentPane.add(btnProfile);
		
		JButton btnFile = new JButton(new ImageIcon(this.getClass().getResource("/img/attachfile.png")));
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser();
				choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				choose.showOpenDialog(null);
				File file = choose.getSelectedFile();
				if(file==null){
					return;
				}
				String str="/f/"+sendToID+"/n/"+c.getID()+"/n/"+file.toString();
				c.send(str.getBytes());		
			}
		});
		btnFile.setBounds(365, 14, 51, 35);
		contentPane.add(btnFile);
		
		JButton btnCall = new JButton(new ImageIcon(this.getClass().getResource("/img/callicon.png")));
		btnCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str="/call/"+sendToID+"/n/"+c.getID();
				c.send(str.getBytes());
			}
		});
		btnCall.setBounds(414, 14, 51, 35);
		contentPane.add(btnCall);
		
		lblLast = new JLabel("online");
		lblLast.setFont(new Font("Monotype Corsiva",Font.ITALIC,20));
		lblLast.setBounds(78, 23, 277, 26);
		contentPane.add(lblLast);
		addWindowListener(new WindowAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				running=false;
				ClientFrame.this.flag=false;
				queue.add("haha");
			}
			
		});
		
		
		
	}
	
	public void listen(){
		listen=new Thread("Listen"){
			public void run(){
				String msg="";
				while(running){
					
					try {
						msg = queue.take();
						if(msg.startsWith("/seen/")){
							//setTitle(sendToName+" : "+msg.split("/seen/")[1]);
							lblLast.setText(msg.split("/seen/")[1]);
							continue;
						}else if(msg.equals("/j/")){
							flag=false;
							//setTitle(sendToName+" : online");
							lblLast.setText("online");
							continue;
						}else if(msg.equals("/b/")){
							flag=true;
							lastSeen();
							continue;
						}
						txtHistory.append(msg+"\n");	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		listen.start();
	}
	
	public void lastSeen(){
		Thread t=new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(flag){
					c.send(("/l/"+sendToID+"/n/"+c.getID()).getBytes());
					try{
						Thread.sleep(60000);
					}catch(Exception e){
						
					}
				}
				
			}
		});
		t.start();
	}
	
	public void sendTo(String sms){// /m/to/n/id/n/message
		if(sms.equals("")){
			return;
		}
		sms="/m/"+sendToID+"/n/"+c.getID()+"/n/"+c.getName()+" : "+sms;
		c.send(sms.getBytes());
	}
	
	public void run(){
		listen();
		lastSeen();
	}
	
	
	
	
}
