package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RecordAndPlay extends JFrame {

	private JPanel contentPane;

	// port 6500 for listening and port 6600 for speaking
	ServerSocket ssl;
	ServerSocket sss;
	AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
	TargetDataLine microphone;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	boolean flag;
	private Clock clock;
	JLabel lblTime;

	public RecordAndPlay() {
		super("on going call");
		getContentPane().setLayout(null);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 273, 97);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(102, 224, 255));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		setResizable(false);
		setLocationRelativeTo(null);
		
		lblTime = new JLabel("00 : 00 : 00");
		lblTime.setBounds(107, 11, 77, 23);
		contentPane.add(lblTime);

		clock=new Clock(lblTime);
		JButton btnEnd = new JButton(new ImageIcon(this.getClass().getResource("/img/end.jpg")));
		btnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				flag = false;
				dispose();
			}
		});
		btnEnd.setBounds(102, 35, 69, 23);
		contentPane.add(btnEnd);	

	}
	
	

	

	public void sendThroughTCPSound(String ip) {// bind to another port and send
		Thread t = new Thread(){
			public void run() {
				Socket s = null;
				try {
					s = new Socket(ip, 6501);
					record(s);
				}catch (Exception e) {
					JOptionPane.showMessageDialog(null, "user busy");
				}
			}
		};
		t.start();
	}

	public void recieveThroughTCPSound(String ip) {// bind to another port and
													// send
		Thread t = new Thread() {
			public void run() {
				Socket s = null;
				try {
					s = new Socket(ip, 6600);
					play(s);
				} catch (Exception e) {
					// JOptionPane.showMessageDialog(null,"user busy");
				}
			}
		};
		t.start();

	}

	public void openTCPForSendingSound() {
		Thread t = new Thread() {
			public void run() {
				try {
					sss = new ServerSocket(6600);
					while (true) {
						Socket s = sss.accept();
						record(s);
					}
				} catch (Exception e) {
					System.out.println("error in opening tcp for sound sending");
				} finally {
					try {
						sss.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		};
		t.start();

	}

	public void openTCPForListeningToSound() {
		Thread t = new Thread() {
			public void run() {
				try {
					ssl = new ServerSocket(6501);
					while (true) {
						System.out.println("haha");
						Socket s = ssl.accept();
						play(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						ssl.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		};
		t.start();

	}

	public void play(Socket s) {
		clock.enableFlag();
		setTitle("ongoing call");	
		try {
			InputStream is = s.getInputStream();
			audioInputStream = new AudioInputStream(is, format,9000000/ format.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, format);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(format);
			sourceDataLine.start();
			int cnt = 0;
			
			byte tempBuffer[] = new byte[10000];
			while ((cnt = audioInputStream.read(tempBuffer, 0,
					tempBuffer.length)) != -1 && flag) {

				if (cnt > 0) {
					// Write data to the internal buffer of
					// the data line where it will be
					// delivered to the speaker.
					sourceDataLine.write(tempBuffer, 0, cnt);
				}// end if
			}
			// Block and wait for internal buffer of the
			// data line to empty.

			return;
		} catch (Exception e) {

			return;
		} finally {
			sourceDataLine.drain();
			sourceDataLine.close();
			flag = false;
			dispose();
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void record(Socket s) {
		try {
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			OutputStream os = s.getOutputStream();
			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[1200];
			microphone.start();

			int bytesRead = 0;
			

			while (flag) { // Just so I can test if recording
							// my mic works...
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
				bytesRead = bytesRead + numBytesRead;
				os.write(data, 0, numBytesRead);
				os.flush();
			}
			return;
		} catch (Exception e) {
			return;
		} finally {
			microphone.close();
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
