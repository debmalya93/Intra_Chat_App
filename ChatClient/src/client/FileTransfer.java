package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileTransfer {
	public static void sendFile(Socket socket,String path){
		
		File file = new File(path);
		String filePath=file.toString();
		String ext=filePath.substring(filePath.lastIndexOf('.')+1);
		
		
		FileInputStream fis=null;
		BufferedInputStream bis=null;
		OutputStream os=null;
		try{
		    //Specify the file
		    fis = new FileInputStream(file);
		    bis = new BufferedInputStream(fis); 
		      
		    //Get socket's output stream
		    os = socket.getOutputStream();
		    
		    byte[] extention=ext.getBytes();
		    os.write(extention);
		            
		    //Read File Contents into contents array 
		    byte[] contents;
		    long fileLength = file.length(); 
		    long current = 0;
		     
		    ProgressBar pb=new ProgressBar();
		    pb.setVisible(true);
		    while(current!=fileLength){ 
		        int size = 1000000;//1MB
		        if(fileLength - current >= size)
		            current += size;    
		        else{ 
		            size = (int)(fileLength - current); 
		            current = fileLength;
		        } 
		        contents = new byte[size]; 
		        bis.read(contents, 0, size); 
		        os.write(contents);
		        pb.progressBar.setValue((int)((current*100)/fileLength));
		    }   
		    
		    os.flush(); 
		    pb.setTitle("Transfer successfull");
		    System.out.println("File sent succesfully!");
		}catch(Exception e){
			System.out.println("File sent unsuccesfully!");
		}finally{
			try {
				fis.close();
				bis.close();
				os.close();
				socket.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		
	}
	
	
	public static void recieveFile(Socket socket){
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		InputStream is=null;
		try{
			
	        byte[] contents = new byte[1000000];//1Mb
	        is = socket.getInputStream();
	        
	        //getting extention
	        int i=is.read(contents);
	        String ext=new String(contents);
	        ext=ext.trim();
	        JFileChooser choose = new JFileChooser();
			choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			choose.showSaveDialog(null);
			File file = choose.getSelectedFile();
			
			if(file==null){
				return;
			}
			
			file=new File(file.toString()+"."+ext);
			//Initialize the FileOutputStream to the output file's full path.
	        fos = new FileOutputStream(file);
	        bos = new BufferedOutputStream(fos);
	        
	        
	        
	        //No of bytes read in one read() call
	        int bytesRead = 0; 
	        
	        while((bytesRead=is.read(contents))!=-1)
	            bos.write(contents, 0, bytesRead); 
	        
	        bos.flush(); 
	       
	        //System.out.println("File saved successfully!");
			JOptionPane.showMessageDialog(null,"file recieved successfully");
		}catch(Exception e){
			System.out.println("File saved unsuccessfully!");
		}finally{
			try{
				fos.close();
				bos.close();
				is.close();
				socket.close();
			}catch(Exception e){
				
			}
		}
	}
}


