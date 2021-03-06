package edu.gatech.cs4235.PGPClient.DAL;

/**
 * A class to represent User's preference
 * @author corin
 */
public class Preferences 
{
	private String keyDirectory;
	private String publicKeyFilePath;
	private String privateKeyFilePath;
	private String passphrase;
	private String username;
	private String password;
	
	/**
	 * Constructor
	 */
	public Preferences()
	{
		keyDirectory = "~/PGPClient";
		privateKeyFilePath = "key_rsa";
		publicKeyFilePath = privateKeyFilePath + ".pub";
		passphrase = "";
	}

	public String getKeyDirectory() {
		return keyDirectory;
	}

	public void setKeyDirectory(String keyDirectory) {
		this.keyDirectory = keyDirectory;
	}

	public String getPublicKeyFilePath() {
		return publicKeyFilePath;
	}

	public void setPublicKeyFilePath(String publicKeyFilePath) {
		this.publicKeyFilePath = publicKeyFilePath;
	}

	public String getPrivateKeyFilePath() {
		return privateKeyFilePath;
	}

	public void setPrivateKeyFilePath(String privateKeyFilePath) {
		this.privateKeyFilePath = privateKeyFilePath;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (!username.contains("@gmail.com"))
			username += "@gmail.com";
		
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
