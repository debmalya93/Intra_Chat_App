package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientLogin extends JFrame {

	private JPanel contentPane;
	private JTextField txtId;
	private Client c;
	private JPasswordField pwdPassword;
	private JButton btnBack;

	/**
	 * Launch the application.
	 */
	
	/**
	 * Create the frame.
	 */
	public ClientLogin() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setBackground(new Color(102, 224, 255));
		contentPane.setBackground(new Color(153, 255, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setBounds(113, 201, 232, 23);
		contentPane.add(pwdPassword);
		
		JLabel lblEmailId = new JLabel("Email ID");
		lblEmailId.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblEmailId.setBounds(41, 158, 62, 14);
		contentPane.add(lblEmailId);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblPassword.setBounds(41, 204, 62, 14);
		contentPane.add(lblPassword);
		
		JButton btnLogin = new JButton(new ImageIcon(this.getClass().getResource("/img/login1.png")));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String id=txtId.getText();
				String pass=pwdPassword.getText();
				if(id.equals("")||pass.equals("")){
					JOptionPane.showMessageDialog(null,"Empty Field(s)!!");
					return;
				}
				String authenticate="/a/"+id+"/n/"+pass;
				c=new Client(null,"192.168.1.103",9000,id,pass);
				if(c.openConnection()){
					c.send(authenticate.getBytes());
					String status;
					try{
						status=c.recieve();
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"server down");
						return;
					}
					//String status=c.recieve();
				
					if(status.startsWith("/c/")){ // /c/name
						//System.out.println("you r connected...."+status.split("/c/")[1]);
						String name=status.split("/c/")[1];
						c.setName(name);//setting the name of the client
						dispose();
						c.openTCPAndListen();
						new ClientList(c).setVisible(true);
					}else if(status.startsWith("/z/")){
						JOptionPane.showMessageDialog(null,"Opss!! not a registered user");
					}else if(status.startsWith("/x/")){
						JOptionPane.showMessageDialog(null,"Opss!! you are already loggedin");
					}
				}
				
			}
		});
		btnLogin.setBounds(355, 201, 79, 23);
		contentPane.add(btnLogin);
		
		txtId = new JTextField();
		txtId.setBounds(113, 156, 232, 20);
		contentPane.add(txtId);
		txtId.setColumns(10);
		
		btnBack = new JButton(new ImageIcon(this.getClass().getResource("/img/back.png")));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ClientStart().setVisible(true);
				dispose();
			}
		});
		btnBack.setBounds(14, 11, 37, 30);
		contentPane.add(btnBack);
		
		JLabel lblLog = new JLabel();
		lblLog.setBounds(124, 24, 199, 111);
		lblLog.setIcon(new ImageIcon(this.getClass().getResource("/img/login.png")));
		contentPane.add(lblLog);
		
		
		setLocationRelativeTo(null);
		setResizable(false);
	}
}
