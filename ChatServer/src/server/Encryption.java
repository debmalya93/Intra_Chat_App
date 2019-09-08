package server;

public class Encryption {
	public static String encrypt(String msg) {
		int l=msg.length();
		String encryptedMsg = "";
		if (msg != null) {
			for (int i = 0; i <l; i++) {
				encryptedMsg += (char) (msg.charAt(i) + 4);
			}
		}
		return encryptedMsg;

	}

	public static String decrypt(String msg) {
		int l=msg.length();
		String decryptedMsg = "";
		if (msg != null) {
			for (int i = 0; i < l; i++) {
				decryptedMsg += (char) (msg.charAt(i) - 4);
			}
		}

		return decryptedMsg;
	}

}
