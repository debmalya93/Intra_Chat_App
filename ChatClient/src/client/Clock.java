package client;

import javax.swing.JLabel;

public class Clock extends Thread{
	JLabel label;
	//boolean flag;
	int min;
	int sec;
	
	public Clock(JLabel l){
		label=l;
		this.start();
	}
	
	public void enableFlag(){
		//flag=true;
		min=sec=0;
		
	}
	
	public void run(){
		String time = "";
		try{
			while (true) {
				Thread.currentThread().sleep(1000);
				if (sec < 59) {
					sec++;
				} else{
					min++;
					sec = 0;
				}

				if (min < 10 && sec < 10) {
					time = "00 : 0" + min + " : 0" + sec;
				} else if (sec < 10) {
					time = "00 : " + min + " : 0" + sec;
				} else if (min < 10) {
					time = "00 : 0" + min + " : " + sec;
				} else {
					time = "00 : " + min + " : " + sec;
				}
				label.setText(time);
			}
		}catch(Exception e){
			
		}
	}
	
	
}
