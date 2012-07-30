package edu.gatech.cs4235.PGPClient.DAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;

import edu.gatech.cs4235.PGPClient.email.PGPEmail;

public class ObjectRepository {

	public static String DB_NAME = "PGPDatabase";
	private static ObjectRepository instance;

	private EmbeddedObjectContainer db;
	private Map<String, RemoteClientKey> publicKeys;
	private Preferences preferences;
	private EmailStore emailStore;

	private ObjectRepository() {
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB_NAME);
		refreshModels();
	}

	public static ObjectRepository getInstance() {
		if (instance == null) {
			instance = new ObjectRepository();
		}

		return instance;
	}

	public void saveEmailStore(EmailStore emailStore) {
		db.store(emailStore.getEmailsList());
		db.store(emailStore);
		db.commit();
		refreshModels();
	}

	public EmailStore getEmailStore() {
		return emailStore;
	}

	public void savePreferences(Preferences preferences) {
		db.store(preferences);
		db.commit();
		refreshModels();
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void savePublicKey(String username, String publicKey) {

		RemoteClientKey key = publicKeys.get(username);
		if (key != null) {
			key.setKey(publicKey);
		} else {
			key = new RemoteClientKey(username, publicKey);
		}

		db.store(key);
		db.commit();
		refreshModels();
	}

	public String getPublicKey(String username) {
		RemoteClientKey key = publicKeys.get(username);
		if (key != null) {
			return key.getKey();
		}

		return null;
	}

	public void deletePublicKey(String username) {
		RemoteClientKey key = publicKeys.get(username);
		if (key != null) {
			db.delete(key.getKey());
			db.commit();
			refreshModels();
		}
	}

	public List<RemoteClientKey> getKeyStore() {
		return new ArrayList<RemoteClientKey>(publicKeys.values());
	}

	private void refreshModels() {

		// refresh Preferences
		ObjectSet<Preferences> result = db.queryByExample(Preferences.class);
		if (result.hasNext()) {
			preferences = (Preferences) result.next();
		} else {
			preferences = new Preferences();
			db.store(preferences);
			db.commit();
		}

		// refresh Public Keys;

		publicKeys = new HashMap<String, RemoteClientKey>();

		List<RemoteClientKey> keys = db.queryByExample(RemoteClientKey.class);
		if (keys.size() > 0) {
			for (RemoteClientKey k : keys) {
				publicKeys.put(k.getUsername(), k);
			}
		}

		// refresh EmailStore
		emailStore = new EmailStore();
		List<PGPEmail> emails = new ArrayList<PGPEmail>();
		emailStore.setEmailList(emails);
		
		ObjectSet<EmailStore> result2 = db.queryByExample(emailStore);
		if (result2.hasNext()) {
			emailStore = result2.next();
		} else {
			
			db.store(emailStore);
			db.commit();
		}
	}

	public static void main(String[] args) {
		ObjectRepository or = ObjectRepository.getInstance();
		EmailStore es = or.getEmailStore();
		//es.add(new PGPEmail("TestTo", "TestFrom", "Test", "Test"));
		//or.saveEmailStore(es);
		//es = or.getEmailStore();
	}
}
