package edu.gatech.cs4235.PGPClient.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.gatech.cs4235.PGPClient.email.EmailClient;
import edu.gatech.cs4235.PGPClient.email.PGPEmail;

/**
 * This class represents a toolbar panel.
 * @author mdandy
 */
public class ToolbarPanel extends JPanel 
{
	/* Swing components */
	private JFrame frame;
	private JButton btnSetting;
	private JButton btnCryptoConfig;
	private JButton btnEmailSync;
	private JButton btnEmailCompose;
	private JButton btnEmailReply;
	private JButton btnEmailDelete;
	private JButton btnEmailForward;

	private PGPClientWindow window;
	private EmailClient ec;

	/**
	 * Constructor
	 */
	public ToolbarPanel(JFrame frame, PGPClientWindow window)
	{
		this.frame = frame;
		this.setPreferredSize(new Dimension (1200, 48));
		this.setBackground (new Color (198, 193, 198));
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
		ButtonListener bl = new ButtonListener();

		ImageIcon imgSetting = new ImageIcon ("images/setting.png");
		btnSetting = new JButton (imgSetting);
		btnSetting.setToolTipText("Settings");

		ImageIcon imgCryptoConfig = new ImageIcon ("images/key.png");
		btnCryptoConfig = new JButton (imgCryptoConfig);
		btnCryptoConfig.setToolTipText("Crypto Config");

		ImageIcon imgEmailSync = new ImageIcon ("images/email_download.png");
		btnEmailSync = new JButton (imgEmailSync);
		btnEmailSync.setToolTipText("Get new messages");

		ImageIcon imgEmailCompose = new ImageIcon ("images/email_new.png");
		btnEmailCompose = new JButton (imgEmailCompose);
		btnEmailCompose.setToolTipText("Compose new message");

		ImageIcon imgEmailReply = new ImageIcon ("images/email_reply.png");
		btnEmailReply = new JButton (imgEmailReply);
		btnEmailReply.setToolTipText("Reply to sender of selected message");

		ImageIcon imgEmailForward = new ImageIcon ("images/email_forward.png");
		btnEmailForward = new JButton (imgEmailForward);
		btnEmailForward.setToolTipText("Forward selected message");

		ImageIcon imgEmailDelete = new ImageIcon ("images/email_delete.png");
		btnEmailDelete = new JButton (imgEmailDelete);
		btnEmailDelete.setToolTipText("Delete selected message");

		/* Register event handlers */
		btnSetting.addActionListener(bl);
		btnCryptoConfig.addActionListener(bl);
		btnEmailSync.addActionListener(bl);
		btnEmailCompose.addActionListener(bl);
		btnEmailReply.addActionListener(bl);
		btnEmailForward.addActionListener(bl);
		btnEmailDelete.addActionListener(bl);

		/* Add components */
		this.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(btnSetting);
		this.add(btnCryptoConfig);
		this.add(Box.createRigidArea(new Dimension(50, 0)));
		this.add(btnEmailSync);
		this.add(btnEmailCompose);
		this.add(btnEmailReply);
		this.add(btnEmailForward);
		this.add(btnEmailDelete);

		/* Preloading Event */
		ec = EmailClient.getInstance();
	}

	/**
	 * Event handler for JButtons
	 * @author
	 */
	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Object source = e.getSource();

			if (source == btnSetting)
			{
				new SettingDialog(frame);
			}
			else if (source == btnCryptoConfig)
			{
				new CryptoConfigDialog(frame);
			}
			else if (source == btnEmailSync)
			{
				PGPEmail[] emails  = ec.getMessages();
				if (emails == null)
				{
					JOptionPane.showMessageDialog(ToolbarPanel.this, "Error: Unable to retrieve emails!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else if (emails.length == 0)
				{
					JOptionPane.showMessageDialog(ToolbarPanel.this, "No new messages in your inbox.");
				}
				else
				{
					NavigationPanel np = window.getNavigationPanel();
					np.show(emails);
				}
			}
			else if (source == btnEmailCompose)
			{
				new ComposeDialog(frame);
			}
			else if (source == btnEmailReply)
			{
				MainPanel mp = window.getMainPanel();
				PGPEmail email = mp.getEmail();

				if (email == null)
				{
					JOptionPane.showMessageDialog(ToolbarPanel.this, "Select an email to reply first.");
				}
				else
				{
					ComposeDialog cd = new ComposeDialog(frame);
					cd.setEmail(email, PGPEmail.Type.REPLY);
				}
			}
			else if (source == btnEmailForward)
			{
				MainPanel mp = window.getMainPanel();
				PGPEmail email = mp.getEmail();

				if (email == null)
				{
					JOptionPane.showMessageDialog(ToolbarPanel.this, "Select an email to forward first.");
				}
				else
				{
					ComposeDialog cd = new ComposeDialog(frame);
					cd.setEmail(email, PGPEmail.Type.FORWARD);
				}
			}
			else if (source == btnEmailDelete)
			{
				JOptionPane.showMessageDialog(frame, "Unsupported command.");
			}
		}
	}
}
