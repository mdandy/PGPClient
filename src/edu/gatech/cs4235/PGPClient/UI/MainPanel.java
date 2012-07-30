package edu.gatech.cs4235.PGPClient.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.gatech.cs4235.PGPClient.email.PGPEmail;

/**
 * This class represents a main panel where email will be displayed.
 * @author mdandy
 */
public class MainPanel extends JPanel 
{
	private JTextArea txtEmail;
	private PGPClientWindow window;
	private PGPEmail email;
	
	/**
	 * Constructor
	 */
	public MainPanel(JFrame frame, PGPClientWindow window)
	{
		this.setPreferredSize(new Dimension (900, 750));
		this.setBackground (Color.WHITE);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		initializeComponent();
		
		this.window = window;
	}
	
	/**
	 * Initialize components
	 */
	private void initializeComponent()
	{
		/* Initialize components */
		txtEmail = new JTextArea();
		txtEmail.setText("No message.");
		txtEmail.setPreferredSize(new Dimension (886, 737));
		txtEmail.setLineWrap(true);
		txtEmail.setEditable(false);
		txtEmail.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scrollEmail = new JScrollPane(txtEmail);
		
		/* Register event handlers */
		
		/* Add components */
		this.add(scrollEmail, BorderLayout.CENTER);
	}
	
	/**
	 * Display email content.
	 * @param email the email
	 */
	public void show(PGPEmail email)
	{
		this.email = email;
		
		String display = "Sender:\t" + email.from + "\n";
		display += "Subject:\t" + email.subject + "\n\n";
		
		if (!email.isAunthentic)
			display += "WARNING: Unable to verify the signature!" + "\n\n";
		
		display += email.payload;
		
		txtEmail.setText(display);
		this.repaint();
	}
	
	/**
	 * Get the currently displayed email.
	 * @return the currently displayed email
	 */
	public PGPEmail getEmail()
	{
		return email;
	}
}
