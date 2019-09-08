package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Profile extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JLabel label;
	private Client c;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Profile(Client c, boolean flag) {
		super(c.getName());
		this.c = c;
		setBounds(100, 100, 370, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//contentPane.setBackground(new Color(102, 224, 255));
		contentPane.setBackground(new Color(153, 255, 255));

		label = new JLabel("not available");
		label.setBounds(72, 32, 203, 156);
		contentPane.add(label);

		textField = new JTextField("Hey i am using Intra-Chat!!!");
		textField.setBounds(58, 214, 253, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setEditable(false);
		setLocationRelativeTo(null);
		setResizable(false);
		label.setIcon(new ImageIcon(getClass().getResource("/img/pic.png")));

		JButton btnEdit = new JButton("Edit");

		

		btnEdit.setBounds(274, 165, 37, 23);
		btnEdit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser();
				choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				choose.showOpenDialog(null);
				File file = choose.getSelectedFile();
				System.out.println(file);
				if (file == null) {
					return;
				}
				String fullPath = file.getAbsolutePath();
				setPath(fullPath);
				//retrieve();
				/*try {
					byte img[]=Files.readAllBytes(file.toPath());
					if(img.length>500000){
						JOptionPane.showMessageDialog(null, "select less than 500KB");
						return;
					}
					byte id[]=c.getID().getBytes();
					byte res[]=new byte[img.length+id.length];
					System.arraycopy(id,0,res,0,id.length);
					System.arraycopy(img,0, res,id.length,img.length);
					System.out.println(id.length+img.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
			}
		});
		
		if(flag){
			contentPane.add(btnEdit);
		}
	}

	public void retrieve() {
		// label.setIcon(null);
		c.send(("/dpretrieve/" + c.getID()).getBytes());
	}

	public void setImage(byte[] b) {
		try {
			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(b));
			// ImageIcon icon = new
			// ImageIcon(bi.getScaledInstance(bi.getWidth(),
			// bi.getHeight(), Image.SCALE_SMOOTH));
			// Graphics g=bi.getGraphics();
			// g.drawImage(bi, 0, 0,label);
			label.setIcon(new ImageIcon(bi));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// convert byte array back to BufferedImage

	}

	public void setPath(String str) {
		try {
			/*
			 * Path p = Paths.get(str); byte[] b = Files.readAllBytes(p); String
			 * g = new String(b,0,b.length); for(byte bb:g.getBytes()){
			 * System.out.print(bb); } System.out.println("setpath"); g = "/dp/"
			 * + c.getID() + "/n/" + g; c.send(g.getBytes());
			 */
			BufferedImage originalImage = ImageIO.read(new File(str));
			
			WritableRaster raster = originalImage.getRaster();
			 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
			
			//ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//ImageIO.write(originalImage, "jpg", baos);
			//baos.flush();
			//byte[] imageInByte = baos.toByteArray();
			//baos.close();
			 byte[] imageInByte=data.getData();
			String g = new String(imageInByte, 0, imageInByte.length);
			g = "/dp/" + c.getID() + "/n/" + g;
			c.send(g.getBytes());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("bad file path");
		}

	}

}
