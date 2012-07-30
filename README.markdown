# PGPClient

PGPClient is an email client that secures all emails based on 
Pretty Good Privacy(PGP) specification. It uses 1024-bit RSA
encryption and 138-bit AES encryption. As for connection, it
uses SMTP for outbound connection and POP3S for inbound 
connection.

### Authors

* Michael Dandy
* Corin Dwyer
* Diana Lim
* Jack Scisco

### Table of Contents

1. Settings
	1.1 User Login
	1.2 Exchange Settings
2. Crypto-Config
	2.1 General
	2.2 Key Management
	2.3 Key Store
3. Email Client

### Settings

1. Settings
	Settings contain basic information about Exchange Account 
	credentials (username and password) and Exchange Account 
	connection settings.

1.1 User Login
	In order to receive and send email, a valid Exchange Account 
	credentials have to be provided.

1.2 Exchange Settings
	You can configure your Exchange Account here. Currently, 
	PGPClient only supports GMail Account.

### Crypto-Config

2. Crypto-Config
	Crypto-Config contain basic information about Key Store. It 
	also provides a service to generate a pair of RSA keys.

2.1 General
	Key Directory serves as ROOT directory of the Key Store. Private 
	Key setting will determine which key that will be used to decrypt
	and sign an email.

2.2 Key Management
	Key Management provides a service to generate a pair of RSA keys.
	Both private and public key will be written into two separate 
	files, specified in the General setting. The public key will have 
	.pub extension.

	Passphrase has to be provided in order for AES encryption to work 
	properly.

2.3 Key Store
	Key Store lists all public keys that are registered to PGPClient. 
	To add a new public key, a username (email address) and public 
	key file must be provided.

### Email Client

3. Email Client
	<strong>Get new messages</strong>
	Retrieve all new messages from the Exchange Account Server. List 
	of messages will be displayed on the right Navigation Panel.

	<strong>Compose new message</strong>
	Create a new message.

	<strong>Reply to sender of selected message</strong>
	Reply the selected message.

	<strong>Forward selected message</strong>
	Forward the selected message.

	<strong>Delete selected message</strong>
	Due to limitation of POP3, this function has not been implemented 
	yet.