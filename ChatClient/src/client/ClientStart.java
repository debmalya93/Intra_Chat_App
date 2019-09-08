package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ClientStart extends JFrame {

	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	public ClientStart() {
		super("Intra-Chat");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 455, 323);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setBackground(new Color(102, 224, 255));
		contentPane.setBackground(new Color(153, 255, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		ImageIcon bg=new ImageIcon(this.getClass().getResource("/img/pic.png"));
		
		JButton btnLogin = new JButton(new ImageIcon(this.getClass().getResource("/img/login1.png")));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientLogin c=new ClientLogin();
				dispose();
				c.setVisible(true);
			}
		});
		btnLogin.setBounds(51, 261, 89, 23);
		contentPane.add(btnLogin);
		JButton btnRegister = new JButton(new ImageIcon(this.getClass().getResource("/img/register1.png")));
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientRegister c=new ClientRegister();
				c.setLocationRelativeTo(null);
				c.setResizable(false);
				c.setVisible(true);
				dispose();
			}
		});
		btnRegister.setBounds(303, 261, 89, 23);
		contentPane.add(btnRegister);
		
		JLabel label = new JLabel("");
		label.setBounds(127, 0, 276, 250);
		label.setIcon(bg);
		contentPane.add(label);
		setLocationRelativeTo(null);
	}
}
