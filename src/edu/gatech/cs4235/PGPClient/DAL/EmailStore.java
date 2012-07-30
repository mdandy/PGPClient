package edu.gatech.cs4235.PGPClient.DAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.cs4235.PGPClient.email.PGPEmail;

/**
 * This class represents a data store for PGPEmail
 * 
 * @author mdandy
 */
public class EmailStore {
	private static EmailStore emailStore;
	private List<PGPEmail> emails;
	public static final int MAX_COUNT = 50;

	/**
	 * Constructor
	 */
	public EmailStore() {
		emails = new ArrayList<PGPEmail>(MAX_COUNT);
	}

	public void add(PGPEmail email) {
		this.emails.add(0, email);

		while (this.emails.size() > MAX_COUNT) {
			this.emails.remove(this.emails.size() - 1);
		}
	}

	/**
	 * Add a PGPEmail to the store. The newer
	 * 
	 * @param email
	 *            the PGPEmails in chronological order
	 */
	public void add(PGPEmail[] emails) {
		List<PGPEmail> list = Arrays.asList(emails);
		this.emails.addAll(0, list);

		/* Truncate emails once the list exceed max count */
		while (this.emails.size() > MAX_COUNT) {
			this.emails.remove(this.emails.size() - 1);
		}
	}

	/**
	 * Get PGPEmail.
	 * 
	 * @param index
	 *            the email index
	 * @return the PGPEmail
	 */
	public PGPEmail getEmail(int index) {
		if (index > emails.size())
			return null;
		return emails.get(index);
	}

	/**
	 * Get all PGPEmail in the store
	 * 
	 * @return the PGPEmail
	 */
	public PGPEmail[] getEmails() {
		PGPEmail[] array = new PGPEmail[emails.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = emails.get(i);
		}
		return array;
	}

	public List<PGPEmail> getEmailsList() {
		return emails;
	}

	public void setEmailList(List<PGPEmail> emails) {
		this.emails = emails;
	}
}
