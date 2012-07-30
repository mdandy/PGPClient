package edu.gatech.cs4235.PGPClient.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import edu.gatech.cs4235.PGPClient.DAL.ObjectRepository;
import edu.gatech.cs4235.PGPClient.DAL.Preferences;
import edu.gatech.cs4235.PGPClient.email.EmailClient;
import edu.gatech.cs4235.PGPClient.email.PGPEmail;

/**
 * This class represent a dialog menu for Compose Email
 * @author mdandy
 */
public class ComposeDialog extends JDialog 
{
	private JTextField txtTo;
	private JTextField txtSubject;
	private JTextField txtFrom;
	private JTextArea txtBody;

	private JButton sendButton;
	private JButton cancelButton;

	private EmailClient ec;
	private ObjectRepository or;
	private Preferences pref;

	/**
	 * Create the dialog.
	 */
	public ComposeDialog(JFrame frame) 
	{
		/* Set Properties */
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("PGPClient - Compose");
		Point frameLocation = frame.getLocation();
		this.setLocation(frameLocation.x + frame.getWidth() / 8, frameLocation.y + frame.getHeight() / 8);
		this.setPreferredSize(new Dimension(1000, 700));

		/* Set Components */
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel panelHeader = new JPanel();
			getContentPane().add(panelHeader, BorderLayout.NORTH);
			panelHeader.setLayout(new MigLayout("", "[80px][850px]", "[30px][30px]"));
			{
				JLabel lblTo = new JLabel("To:");
				panelHeader.add(lblTo, "cell 0 0,alignx right,growy");
			}
			{
				txtTo = new JTextField();
				panelHeader.add(txtTo, "cell 1 0,grow");
				txtTo.setColumns(10);
			}
			{
				JLabel lblSubject = new JLabel("Subject:");
				panelHeader.add(lblSubject, "cell 0 1,alignx right,growy");
			}
			{
				txtSubject = new JTextField();
				panelHeader.add(txtSubject, "cell 1 1,grow");
				txtSubject.setColumns(10);
			}
			{
				JLabel lblFrom = new JLabel("From:");
				panelHeader.add(lblFrom, "cell 0 2,alignx right,growy");
			}
			{
				txtFrom = new JTextField();
				txtFrom.setEditable(false);
				panelHeader.add(txtFrom, "cell 1 2,grow");
				txtFrom.setColumns(10);
			}
		}
		{
			JPanel panelCompose = new JPanel();
			getContentPane().add(panelCompose, BorderLayout.CENTER);
			{
				txtBody = new JTextArea();
				txtBody.setRows(30);
				txtBody.setColumns(80);
				panelCompose.add(txtBody);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				sendButton = new JButton("Send");
				sendButton.setActionCommand("OK");
				buttonPane.add(sendButton);
				getRootPane().setDefaultButton(sendButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		/* Preloading event */
		ec = EmailClient.getInstance();
		or = ObjectRepository.getInstance();
		pref = or.getPreferences();
		txtFrom.setText(pref.getUsername());

		/* Set event handler */
		ButtonListener listener = new ButtonListener();
		sendButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

		/* Set visibility */
		pack();
		setVisible(true);
	}

	/**
	 * Set the email for REPLY and FORWARD protocol
	 * @param email the email
	 */
	public void setEmail(PGPEmail email, PGPEmail.Type type)
	{
		if (type == PGPEmail.Type.REPLY)
		{
			this.txtTo.setText(email.from);
			this.txtSubject.setText("RE: " + email.subject);
		}
		else if (type == PGPEmail.Type.FORWARD)
		{
			this.txtSubject.setText("FW: " + email.subject);
			this.txtBody.setText(email.payload);
		}
	}

	private class ButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Object source = e.getSource();

			if (source == sendButton)
			{
				boolean successful = false;

				String to = txtTo.getText();
				String subject = txtSubject.getText();
				String from = txtFrom.getText();
				String body = txtBody.getText();

				/* Validation */
				if (to.isEmpty())
				{
					JOptionPane.showMessageDialog(ComposeDialog.this, "Error: Recipient cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					PGPEmail email = new PGPEmail(to, subject, from, body);
					successful = ec.send(email);
					if (successful)
					{
						JOptionPane.showMessageDialog(ComposeDialog.this, "Message has been delivered successfully.");
					}
					else
					{
						JOptionPane.showMessageDialog(ComposeDialog.this, "Error: Unable to send message.", "Error", JOptionPane.ERROR_MESSAGE);
					}

				}

				/* Close the dialog */
				if (successful)
					ComposeDialog.this.dispose();
			}
			else if (source == cancelButton)
			{
				/* Close the dialog */
				ComposeDialog.this.dispose();
			}
		}
	}
}
