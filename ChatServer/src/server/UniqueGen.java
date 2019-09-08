package server;

import java.util.Calendar;
import java.util.Formatter;

public class UniqueGen {
	
	public static String getID()
	{ 
		String dateString;
		Calendar cal=Calendar.getInstance();
		Formatter dd=new Formatter();
		dd.format("%td", cal);
		
		Formatter mm=new Formatter();
		mm.format("%tm", cal);
		
		Formatter yy=new Formatter();
		yy.format("%ty", cal);
		
		Formatter hour=new Formatter();
		hour.format("%tH", cal);
		
		Formatter min=new Formatter();
		min.format("%tM", cal);
		
		Formatter sec=new Formatter();
		sec.format("%tS", cal);
		
		dateString=yy.toString()+mm.toString()+dd.toString()+hour.toString()+min.toString()+sec.toString();
		return dateString;
	}

}
