package client;

public class CheckDomain {
	
	static boolean verifyDomainName(String email) throws IllegalDomainNameException
	{
		if(email.charAt(0)!='@')
		{
			String dname=email.substring(email.indexOf('@')+1,email.lastIndexOf("com")-1);
			//System.out.println(dname);
			
			if(dname.equals("gmail")||dname.equals("yahoo")||dname.equals("hotmail"))
				return true;
			else
				throw new IllegalDomainNameException();
		}
		else
			throw new IllegalDomainNameException();
		
	}
	
	
}

class IllegalDomainNameException extends Exception{
}
