package client;

import java.awt.Color;
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

public class ClientRegister extends JFrame {

	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	private JPasswordField txtPassword_1;
	
	private Client c;
	private JButton btnBack;

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public ClientRegister() {
		super("Register Now!!!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setBackground(new Color(102, 224, 255));
		contentPane.setBackground(new Color(153, 255, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(55, 67, 70, 14);
		contentPane.add(lblName);
		
		JLabel lblEmail = new JLabel("E-mail");
		lblEmail.setBounds(55, 98, 89, 14);
		contentPane.add(lblEmail);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(55, 129, 89, 14);
		contentPane.add(lblPassword);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setBounds(55, 161, 125, 14);
		contentPane.add(lblConfirmPassword);
		
		txtName = new JTextField();
		txtName.setBounds(190, 64, 159, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(190, 95, 219, 20);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);
		
		txtPassword=new JPasswordField();
		txtPassword.setBounds(190, 126, 159, 20);
		contentPane.add(txtPassword);
		txtPassword.setColumns(10);
		
		txtPassword_1=new JPasswordField();
		txtPassword_1.setBounds(190, 158, 159, 20);
		contentPane.add(txtPassword_1);
		txtPassword_1.setColumns(10);
		
		JButton btnRegister = new JButton(new ImageIcon(this.getClass().getResource("/img/register1.png")));
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if((txtEmail.getText().equals("")) ||(txtName.getText().equals("")) ||(txtPassword.getText().equals(""))){
					JOptionPane.showMessageDialog(null,"empty fields!!");
					return;
				}
				try
				{
					if(CheckDomain.verifyDomainName(txtEmail.getText().trim()))
					{
						if(txtPassword.getText().trim().equals(txtPassword_1.getText().trim())){
							String name,id,pass;
							name=txtName.getText().trim();
							id=txtEmail.getText().trim();
							pass=txtPassword.getText().trim();
							c=new Client(name,"192.168.1.103",9000,id,pass);
							if(c.openConnection()){
								String register="/r/"+id+"/n/"+name+"/n/"+pass;
								String otp1=UniqueGen.getID();
								SendEmail.sendOTP(id,otp1);
								String otp2=JOptionPane.showInputDialog(null,"OTP has ben sent to your mail");
								if(otp2==null || otp2.equals("")){
									return;
								}
								if(!otp1.equals(otp2)){
									JOptionPane.showMessageDialog(null,"incorrect OTP");
									return;
								}
								c.send(register.getBytes());
								String status=null;
								try{
									status=c.recieve();
								}catch(Exception ee){
									JOptionPane.showMessageDialog(null,"server down");
									return;
								}
								if(status.startsWith("/r/")){//registration successful
									JOptionPane.showMessageDialog(null,"registration successful");
									new ClientStart().setVisible(true);
									dispose();
								}else{
									JOptionPane.showMessageDialog(null,"registration unsuccessful!!\ntry again");
								}
							}
						}else{
							JOptionPane.showMessageDialog(null, "re-enter password!!!");
							txtPassword.setText("");
							txtPassword_1.setText("");
						}
					}
				}catch(IllegalDomainNameException | StringIndexOutOfBoundsException e1)
				{
					JOptionPane.showMessageDialog(null, "Invalid domain name!!\nOnly gmail,yahoo & hotmail are supported\nE.g - ABC@gmail.com");
					txtEmail.setText("");
				}
			}
		});
		btnRegister.setBounds(320, 202, 89, 23);
		contentPane.add(btnRegister);
		
		btnBack = new JButton(new ImageIcon(this.getClass().getResource("/img/back.png")));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ClientStart().setVisible(true);
				dispose();
			}
		});
		btnBack.setBounds(29, 11, 34, 30);
		contentPane.add(btnBack);
		
		
	}
}
