package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class ProgressBar extends JFrame {

	private JPanel contentPane;
	JProgressBar progressBar;
	
	public ProgressBar() {
		super("Progess Information");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 359, 64);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(0, 11, 353, 22);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		contentPane.add(progressBar);
		setResizable(false);
		setLocationRelativeTo(null);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if(progressBar.getValue()==100){
					dispose();
					
				}
				
			}
			
		});
	}
	
	
	
	
}
