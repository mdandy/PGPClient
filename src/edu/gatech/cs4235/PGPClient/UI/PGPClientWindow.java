package edu.gatech.cs4235.PGPClient.UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * This class represents the UI for PGPClient application.
 * @author mdandy
 */
public class PGPClientWindow 
{
	public final String TITLE = "PGPClient";
	private int width;
	private int height;
	
	/* Swing Components */
	private JFrame frame;
	private Container container;
	private ToolbarPanel toolbarPanel;
	private NavigationPanel navigationPanel;
	private MainPanel mainPanel;
	
	/**
	 * Default constructor. Create a 1200 x 1000 window
	 */
	public PGPClientWindow ()
	{
		this (1200, 800);
	}
	
	/**
	 * Constructor
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public PGPClientWindow (int width, int height)
	{
		this.width = width;
		this.height = height;
		initializeWindow();
	}
	
	/**
	 * Initialize window
	 */
	private void initializeWindow()
	{
		/* Set window's basic properties */
		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(width, height));
		
		/* Set window's layout */
		container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		
		/* Add components to the window */
		toolbarPanel = new ToolbarPanel(frame, this);
		navigationPanel = new NavigationPanel(frame, this);
		mainPanel = new MainPanel(frame, this);
		
		container.add(toolbarPanel, BorderLayout.NORTH);
		container.add(navigationPanel, BorderLayout.WEST);
		container.add(mainPanel, BorderLayout.CENTER);
		
		/* View window */
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * @return the toolbarPanel
	 */
	public ToolbarPanel getToolbarPanel() 
	{
		return toolbarPanel;
	}

	/**
	 * @return the navigationPanel
	 */
	public NavigationPanel getNavigationPanel() 
	{
		return navigationPanel;
	}

	/**
	 * @return the mainPanel
	 */
	public MainPanel getMainPanel() 
	{
		return mainPanel;
	}

	/**
	 * @return the width
	 */
	public int getWidth() 
	{
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() 
	{
		return height;
	}

	/**
	 * Run the program
	 */
	public void run()
	{
		
	}
}
