/**
	Author of this Application: Dr Gareth Olubunmi Hughes (14 February 2017 - Present)
	Copyright (c) : Dr Gareth Olubunmi Hughes (14 February 2017 - Present)

	Gareth Hughes
	Application for the BBC Software Engineering Graduate Trainee Scheme
	Technical Test
	An application to implement the rules of Conway's Game of Life
*/

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.text.JTextComponent;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;


public class GameOfLife extends JFrame implements MouseListener
{
	// Instance Variables:
	public boolean gameStarted = false;
	public boolean play        = false;
	public boolean forward     = true;
	public boolean auto        = true;
	public boolean readingFromBackwardsBuffer = false;
	public boolean[][] gridFlag      = new boolean[10][10];
	public boolean[][] gridBuffer    = new boolean[10][10];
	public boolean[][] startGridFlag = new boolean[10][10];

	public int backwardsBufferSize = 64;
	// 3D array buffer to allow the cells to evolve backwards by up to 64 steps...
	public boolean[][][] backwardsBuffer = new boolean[backwardsBufferSize][10][10];
	public int maxBackwardsIndex = 0;
	public int backwardsIndex    = 0;

	public int cellCount = 0;
	public double stepTime = 0.5;

	// Instance Objects:
	public static Font  font2_mono_b = new Font("Monospaced",Font.BOLD, 14);
	public static Font  font2_b      = new Font("TimesRoman",Font.BOLD, 14);
	public static Font  font3_mono_b = new Font("Monospaced",Font.BOLD, 17);
	public static Font  font3_b      = new Font("TimesRoman",Font.BOLD, 17);
	public static Font  font4_mono   = new Font("Monospaced",Font.PLAIN,20);
	public static Font  font4_mono_b = new Font("Monospaced",Font.BOLD ,20);
	public static Font  font5_mono_b = new Font("Monospaced",Font.BOLD ,26);
	public static Color darkOrange   = new Color(255,110,0      );
	public static Color purple       = new Color(190,0  ,255    );
	public static Color brightGreen  = new Color(0  ,255,0  ,127);
	private GraphicsEnvironment env;
	private Rectangle bounds;
	public Evolver evolver = new Evolver(this, stepTime, forward);

	// Instance Components:
	private Container cPane;
	private JPanel contentPane = new JPanel();
	private JScrollPane  cScrollPane = new JScrollPane(contentPane);

	private JMenuItem newMi = new JMenuItem("New Game");

	private JLabel colourLabel = new JLabel("Colour Key:");
	private JPanel[] colourPanel   = new JPanel[4];
	private JLabel[] cellTypeLabel = new JLabel[4];
	private JLabel title = new JLabel("The Game of Life");
	private JPanel[][] grid = new JPanel[10][10]; // 10 x 10 game grid
	private JButton playButton = new JButton("Play");
	private JButton stopButton = new JButton("Stop");
	private JButton newButton  = new JButton("New Game");
	private JButton openButton = new JButton("Open Preset");
	private JButton saveButton = new JButton("Save As Preset");
	private JLabel shiftLabel  = new JLabel("Shift Cells:");
	private JButton[] shiftButton = new JButton[8];
	private JPanel shiftPanel = new JPanel();
	private JLabel infoLabel  = new JLabel("Information Window:");
	private JButton clearButton = new JButton("Clear");
	private JTextArea info = new JTextArea(
		"Select up to 9 grid cells using the mouse and then press the play button...\n"
	);
	private JScrollPane infoScrollPane = new JScrollPane(info);
	private JPanel infoPanel = new JPanel();
	private JLabel settingsLabel = new JLabel("Settings:");
	private JTextField evolveField  = new JTextField(" Evolve: ");
	private JButton forwardsButton  = new JButton("Forwards");
	private JButton backwardsButton = new JButton("Backwards");
	private JTextField modeField = new JTextField(" Mode = AUTO   ");
	private JButton modeButton = new JButton("Change");
	private JTextArea timeArea = new JTextArea(" Step Time = \n 0.5 seconds ");
	private JButton timeUpButton   = new JButton("+");
	private JButton timeDownButton = new JButton("-");
	private JPanel timePanel = new JPanel();

	private JDialog quitDialog         = new JDialog(this,"Quit Game?",true);
	private JButton[] quitDialogOption = new JButton[2];
	private JDialog newDialog          = new JDialog(this,"New Game?",true);
	private JButton[] newDialogOption  = new JButton[2];
	// Still Life presets:
	private JDialog blockDialog     = new JDialog(this,"'Block' preset?",true);
	private JButton[] blockOption   = new JButton[2];
	private JDialog beehiveDialog   = new JDialog(this,"'Beehive' preset?",true);
	private JButton[] beehiveOption = new JButton[2];
	private JDialog loafDialog      = new JDialog(this,"'Loaf' preset?",true);
	private JButton[] loafOption    = new JButton[2];
	private JDialog boatDialog      = new JDialog(this,"'Boat' preset?",true);
	private JButton[] boatOption    = new JButton[2];
	private JDialog tubDialog       = new JDialog(this,"'Tub' preset?",true);
	private JButton[] tubOption     = new JButton[2];
	// Period 2 Oscillator Presets:
	private JDialog blinkerDialog   = new JDialog(this,"'Blinker' preset?",true);
	private JButton[] blinkerOption = new JButton[2];
	private JDialog toadDialog      = new JDialog(this,"'Toad' preset?",true);
	private JButton[] toadOption    = new JButton[2];
	private JDialog beaconDialog    = new JDialog(this,"'Beacon' preset?",true);
	private JButton[] beaconOption  = new JButton[2];
	// Spaceship presets:
	private JDialog gliderDialog    = new JDialog(this,"'Glider' preset?",true);
	private JButton[] gliderOption  = new JButton[2];
	private JDialog lwssDialog    = new JDialog(this,"'LWSS' preset?",true);
	private JButton[] lwssOption  = new JButton[2];
	// Choatic presets:
	private JDialog   rPentDialog   = new JDialog(this,"'R-pentomino' preset?",true);
	private JButton[] rPentOption   = new JButton[2];
	private JDialog   diehardDialog = new JDialog(this,"'Diehard' preset?",true);
	private JButton[] diehardOption = new JButton[2];
	private JDialog   acornDialog   = new JDialog(this, "'Acorn' preset",true);
	private JButton[] acornOption   = new JButton[2];

	private JFrame aboutWindow = new JFrame("About this program...");
	private Container aboutPane;
	private JTextArea aboutTextArea = new JTextArea();
	private JScrollPane  aboutScrollPane = new JScrollPane(aboutTextArea);

	public void set_constraints(Container pane, GridBagConstraints c, Component component,
								int x, int y, int width, int height)
	{
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.weightx = 0.0;
        c.weighty = 0.0;
        pane.add(component, c);
	}

	// Overides the method with a java.awt.Insets setting
	public void set_constraints(Container pane, GridBagConstraints c, Component component,
								int x, int y, int width, int height, Insets insets)
	{
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = insets;
        pane.add(component, c);
	}

	public void addOptionDialog(final JDialog d, JButton[] button, String msg)
	{
		String[] string = { "Yes","No" };

		for(int i=0; i<button.length; i++)
		{
			button[i] = new JButton(string[i]);
			button[i].setFocusable(false);
		}

		// the final button in the array will always cancel the command and hide of the dialog...
		button[button.length - 1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ d.setVisible(false); }
		});

		final JTextArea textArea = new JTextArea(msg);
		textArea.setEditable (false);
		textArea.setFocusable(false);
		final JOptionPane oPane = new JOptionPane(textArea,JOptionPane.QUESTION_MESSAGE,
								 JOptionPane.YES_NO_OPTION, null, button, button[0]);
		textArea.setFont(font3_b);
		textArea.setBackground(oPane.getBackground());

		d.setContentPane(oPane);
		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.pack();
		d.setResizable(false);
	}

	public void postInfo(String string, boolean line_feed)
	{
		String suffix = "\n";

		if(!line_feed) { suffix = ""; }

		info.append(string + suffix);
		info.setCaretPosition( info.getDocument().getLength() ); // sends the scroll bar to the bottom

		//JScrollBar scrollBar = infoScrollPane.getVerticalScrollBar();
		//scrollBar.setValue( scrollBar.getMaximum() );
	}

	public void resetTimeArea(double time)
	{
		String string = " Step Time = \n ";

		time = Math.round(time * 10);
		time /= 10; // rounds to 1 decimal place
		string += time + " seconds ";
		timeArea.setText(string);
	}

	public Point getObjectPoint(Object object)
	{
		Point point = null;

		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				if( grid[i][j].equals(object) )
				{
					point = new Point(i, j);
				}
			}
		}
		System.out.printf(point+"\n");

		return point;
	}

	// Debug dump method for the 2D Array of grid flags:
	public void dumpGridFlagArray()
	{
		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				char flag = gridFlag[j][i] == false ? '-' : 'X';
				System.out.printf(flag + " ");
			}
			System.out.printf("\n");
		}
		System.out.printf("\n");
	}

	public void clearFlagArray(boolean[][] flag)
	{
		for(int i=0; i<flag.length; i++)
		{
			for(int j=0; j<flag[i].length; j++)
			{ flag[i][j] = false; }
		}
	}

	public void copyFromBuffer(boolean[][] buffer, boolean[][] target)
	{
		for(int i=0; i<target.length; i++)
		{
			for(int j=0; j<target[i].length; j++)
			{ target[i][j] = buffer[i][j]; }
		}
	}

	public void storeStartFlags()
	{
		// Store the game's start position in memory...
		copyFromBuffer(gridFlag, startGridFlag);

		// Also store the game's start position at the start of the backwards buffer...
		copyFromBuffer(gridFlag, backwardsBuffer[0]);
		backwardsIndex++;
		maxBackwardsIndex++;

		newButton.setEnabled(true);
		newMi.setEnabled(true);

		gameStarted = true;
	}

	public void clearPanelArray(JPanel[][] panel)
	{
		for(int i=0; i<panel.length; i++)
		{
			for(int j=0; j<panel[i].length; j++)
			{ panel[i][j].setBackground(Color.white); }
		}
	}

	/*
	   Method stiches the left/right & top/bottom grid coordinates together
	   to prevent cells from 'falling of the edges' of the grid...
	*/
	public int wrapCellCoordinate(int c)
	{
		//if     (c == -1) { c = 9; }
		//else if(c == 10) { c = 0; }

		if      (c < 0) { c += 10; }
		else if (c > 9) { c %= 10; }

		return c;
	}

	public Color colorizeCell(Point point, boolean opaqe)
	{
		int x, y;
		int adjacent = 0; // counts adjacent cells
		Color color = null;

		for(int i=-1; i<2; i++)
		{
			for(int j=-1; j<2; j++)
			{
				x = point.x + i;
				y = point.y + j;
				x = wrapCellCoordinate(x);
				y = wrapCellCoordinate(y);

				if(x != point.x || y != point.y)
				{
					if(gridFlag[x][y]) { adjacent++; }
				}
			}
		}

		if     (adjacent < 2) { color = Color.green; }
		else if(adjacent > 3) { color = Color.red;   }
		else                  { color = Color.blue;  }

		return color;
	}

	public void recolorizeLiveCells()
	{
		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				if(gridFlag[i][j])
				{ grid[i][j].setBackground( colorizeCell(new Point(i,j),true ) ); }
			}
		}
	}

	/**
	    Method determines whether a cell survives or is reproduced
	    in the next generation and returns a boolean flag.

	    This works by comparing its coordinates to its adjacent
	    cells in the gridFlag[][] boolean array...
	*/
	public boolean isNextGenerationCell(int pointX, int pointY, boolean oldFlag)
	{
		boolean newFlag = false;
		int x, y;
		int adjacent = 0; // counts adjacent cells

		for(int i=-1; i<2; i++)
		{
			for(int j=-1; j<2; j++)
			{
				x = pointX + i;
				y = pointY + j;
				x = wrapCellCoordinate(x);
				y = wrapCellCoordinate(y);

				if(x != pointX || y != pointY)
				{
					if(gridFlag[x][y]) { adjacent++; }
				}
			}
		}

		// first test whether an exisitng cell survives...
		if(oldFlag)
		{
			if(adjacent == 2 || adjacent == 3) { newFlag = true; }
		}
		// then test whether a new cell is reproduced...
		else
		{
			if(adjacent == 3) { newFlag = true; }
		}

		return newFlag;
	}

	/**
	    Method uses the gridBuffer[][] boolean array to store all coordinate flags for
	    the next generation of cells, by comparing them to the values in gridFlag[][]...

	    The values are then copied from gridBuffer[][] to gridFlag[][] and then the flag
	    buffer values are reset to 'false' gridBuffer[][] in advance of future use...
	*/
	public void createNextGeneration()
	{
		if( backwardsIndex >= (maxBackwardsIndex -1) )
		{
			readingFromBackwardsBuffer = false;

			for(int i=0; i<grid.length; i++)
			{
				for(int j=0; j<grid[i].length; j++)
				{
					gridBuffer[i][j] = isNextGenerationCell(i,j,gridFlag[i][j]);
				}
			}

			copyFromBuffer(gridBuffer, gridFlag);

			// store flags to allow the cells to evolve backwards also...
			if(backwardsIndex < backwardsBufferSize)
			{
				if(backwardsIndex < 0) { backwardsIndex = 0; }
				copyFromBuffer(gridBuffer, backwardsBuffer[backwardsIndex]);
				backwardsIndex++;
				maxBackwardsIndex = backwardsIndex;
			}
			else
			{
				/**
					Code to shift the backwardsBuffer[][][] 3D boolean Array, in the following steps:
					1] Overwrite the element at the [0] index (this element will be erased from memory).
					2] Shift all other elements down one index, using a loop.
					3] Store the newest cell configuration in the highest element...
				*/

				for(int i=0; i < (backwardsBufferSize - 1); i++)
				{ copyFromBuffer(backwardsBuffer[i + 1], backwardsBuffer[i]); }

				copyFromBuffer(gridBuffer, backwardsBuffer[backwardsBufferSize - 1]);
			}
		}
		else
		{
			// read the next cell configuration from the backwardsBuffer[][][]...
			if(backwardsIndex < 1) { backwardsIndex = 1; }
			else                   { backwardsIndex++;   }
			copyFromBuffer(backwardsBuffer[backwardsIndex], gridFlag);
		}

		clearFlagArray(gridBuffer);
		clearPanelArray(grid);
		recolorizeLiveCells();
	}

	public void createLastGeneration()
	{
		if( backwardsIndex >= -1 && (!readingFromBackwardsBuffer || !forward) )
		{ backwardsIndex--; }

		if(backwardsIndex >= 0 )
		{
			copyFromBuffer(backwardsBuffer[backwardsIndex], gridFlag);
			clearPanelArray(grid);
			recolorizeLiveCells();
		}
		else
		{
			String msg = "The backwards buffer has reached its limit, "
			+"no more backwards steps have been stored by the program.";

			postInfo(msg,true);
			evolver.cancel();
			play = false;
		}

		forward = false;
		readingFromBackwardsBuffer = true;
	}

	public void shiftCells(int offsetX, int offsetY)
	{
		int x, y;

		for(int i=0; i<grid.length; i++)
		{
			for(int j=0; j<grid[i].length; j++)
			{
				if(gridFlag[i][j])
				{
					x = offsetX + i;
					y = offsetY + j;
					x = wrapCellCoordinate(x);
					y = wrapCellCoordinate(y);

					gridBuffer[x][y] = true;
				}
			}
		}

		copyFromBuffer(gridBuffer, gridFlag);
		clearFlagArray(gridBuffer);
		clearPanelArray(grid);
		recolorizeLiveCells();
	}

	public void reinitializeGame()
	{
		evolver.cancel();

		// Reinitialize instance variables and arrays:
		gameStarted = false;
		play        = false;
		forward     = true;
		auto        = true;
		readingFromBackwardsBuffer = false;
		clearFlagArray(gridFlag);
		clearFlagArray(gridBuffer);
		clearFlagArray(startGridFlag);

		for(int i=0; i<backwardsBufferSize; i++)
		{ clearFlagArray(backwardsBuffer[i]); }

		maxBackwardsIndex = 0;
		backwardsIndex    = 0;
		cellCount = 0;
		stepTime = 0.5;

		playButton.setEnabled(true);
		stopButton.setEnabled(false);
		newButton.setEnabled(false);
		newMi.setEnabled(false);
		openButton.setEnabled(false);
		saveButton.setEnabled(false);
		evolveField.setForeground(Color.green);
		forwardsButton.setEnabled(false);
		backwardsButton.setEnabled(false);
		modeField.setForeground(Color.magenta);
		modeField.setText(" Mode = AUTO   ");
		modeButton.setBackground(Color.magenta);
		resetTimeArea(stepTime);
		evolver.setStepTime(stepTime);
		evolver.setDirection(true);
		info.setText(
			"Select up to 9 grid cells using the mouse and then press the play button...\n"
		);
		clearPanelArray(grid);
	}

    public void mousePressed(MouseEvent e)
    {
		if(!gameStarted)
		{
			Point point = getObjectPoint( e.getComponent() );

			if(!gridFlag[point.x][point.y])
			{
				if(cellCount < 9)
				{
					//e.getComponent().setBackground( colorizeCell(point,true) );
					gridFlag[point.x][point.y] = true;
					cellCount++;
					recolorizeLiveCells();
				}
				else
				{ postInfo("Max cell count = 9",true); }
			}
			else
			{
				e.getComponent().setBackground(Color.white);
				gridFlag[point.x][point.y] = false;
				cellCount--;
				recolorizeLiveCells();
			}

			dumpGridFlagArray();
		}
		else
		{
			String msg = "No further cells can be added or removed once the game has started, "
			+ "start a new game to run a different cell configuration.";

			postInfo(msg,true);
		}
    }

    public void mouseReleased(MouseEvent e)
    {
        //System.out.printf("Mouse released\n");
    }

    public void mouseEntered(MouseEvent e)
    {
        //System.out.printf("Mouse entered\n");
    }

    public void mouseExited(MouseEvent e)
    {
        //System.out.printf("Mouse exited\n");
    }

    public void mouseClicked(MouseEvent e)
    {
        //System.out.printf("Mouse clicked\n");
    }

	public GameOfLife()
	{
		super("Conway's Game of Life");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Set Panel & JScrollPane on the main JFrame's Container...
      	cPane = getContentPane();
      	cPane.add(cScrollPane);
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(Color.lightGray);
        contentPane.setBorder( BorderFactory.createEmptyBorder(8,15,15,15) );

		// Create an option dialog to close the program...
		addOptionDialog(quitDialog, quitDialogOption,
			"    Are you sure that\n    you wish to quit?"
		);
		// Create an option dialog to start a new game...
		addOptionDialog(newDialog, newDialogOption, "    Start a new game?...");
		// Create option dialogs for the system presets...
		addOptionDialog(blockDialog, blockOption, "    Load 'Block' preset?...");
		addOptionDialog(beehiveDialog, beehiveOption, "    Load 'Beehive' preset?...");
		addOptionDialog(loafDialog, loafOption, "    Load 'Loaf' preset?...");
		addOptionDialog(boatDialog, boatOption, "    Load 'Boat' preset?...");
		addOptionDialog(tubDialog, tubOption, "    Load 'Tub' preset?...");
		addOptionDialog(blinkerDialog, blinkerOption, "    Load 'Blinker' preset?...");
		addOptionDialog(toadDialog, toadOption, "    Load 'Toad' preset?...");
		addOptionDialog(beaconDialog, beaconOption, "    Load 'Beacon' preset?...");
		addOptionDialog(gliderDialog, gliderOption, "    Load 'Glider' preset?...");
		addOptionDialog(lwssDialog, lwssOption, "    Load 'LWSS' preset?...");
		addOptionDialog(rPentDialog, rPentOption, "    Load 'R-pentomino' preset?...");
		addOptionDialog(diehardDialog, diehardOption, "    Load 'Diehard' preset?...");
		addOptionDialog(acornDialog, acornOption, "    Load 'Acorn' preset?...");

        JMenuBar menubar = new JMenuBar();

		// File Menu:
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        //JMenuItem newMi  = new JMenuItem("New Game");
        newMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newMi.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent event)
			{
				newDialog.setLocationRelativeTo(contentPane);
				newDialog.setVisible(true);
			}
        });

        JMenuItem openMi = new JMenuItem("Open Preset");
        openMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        JMenuItem saveMi = new JMenuItem("Save As Preset");
        saveMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        JMenuItem quitMi = new JMenuItem("Quit");

		quitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quitMi.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent event)
			{
				quitDialog.setLocationRelativeTo(contentPane);
				quitDialog.setVisible(true);
			}
        });
        fileMenu.add(newMi);
        fileMenu.add(openMi);
        fileMenu.add(saveMi);
        fileMenu.addSeparator();
        fileMenu.add(quitMi);
        menubar.add(fileMenu);

        // Presets Menu:
        JMenu presetsMenu = new JMenu("Presets");
        presetsMenu.setMnemonic(KeyEvent.VK_P);
        // Still Lifes Submenu:
        JMenu stillMenu = new JMenu("Still Lifes");
        JMenuItem blockMi = new JMenuItem("Block");
		blockMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				blockDialog.setLocationRelativeTo(contentPane);
				blockDialog.setVisible(true);
			}
        });
        JMenuItem beehiveMi = new JMenuItem("Beehive");
		beehiveMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				beehiveDialog.setLocationRelativeTo(contentPane);
				beehiveDialog.setVisible(true);
			}
        });
        JMenuItem loafMi = new JMenuItem("Loaf");
		loafMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				loafDialog.setLocationRelativeTo(contentPane);
				loafDialog.setVisible(true);
			}
        });
        JMenuItem boatMi = new JMenuItem("Boat");
		boatMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				boatDialog.setLocationRelativeTo(contentPane);
				boatDialog.setVisible(true);
			}
        });
        JMenuItem tubMi  = new JMenuItem("Tub");
		tubMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				tubDialog.setLocationRelativeTo(contentPane);
				tubDialog.setVisible(true);
			}
        });
        stillMenu.add(blockMi);
        stillMenu.add(beehiveMi);
        stillMenu.add(loafMi);
        stillMenu.add(boatMi);
        stillMenu.add(tubMi);

        // Oscillators Submenu:
        JMenu oscMenu = new JMenu("Oscillators" );
        // Period 2 Submenu:
        JMenu period2Menu = new JMenu("Period 2");
        JMenuItem blinkerMi = new JMenuItem("Blinker");
		blinkerMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				blinkerDialog.setLocationRelativeTo(contentPane);
				blinkerDialog.setVisible(true);
			}
        });
        JMenuItem toadMi    = new JMenuItem("Toad");
		toadMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				toadDialog.setLocationRelativeTo(contentPane);
				toadDialog.setVisible(true);
			}
        });
        JMenuItem beaconMi  = new JMenuItem("Beacon");
		beaconMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				beaconDialog.setLocationRelativeTo(contentPane);
				beaconDialog.setVisible(true);
			}
        });
        period2Menu.add(blinkerMi);
        period2Menu.add(toadMi);
        period2Menu.add(beaconMi);
        // Period 3 Submenu
        JMenu period3Menu = new JMenu("Period 3");
        JMenuItem pulsarMi     = new JMenuItem("Pulsar");
        JMenuItem starMi       = new JMenuItem("Star");
        JMenuItem crossMi       = new JMenuItem("Cross");
        JMenuItem frenchKissMi = new JMenuItem("French kiss");

        period3Menu.add(pulsarMi);
        period3Menu.add(starMi);
        period3Menu.add(crossMi);
        period3Menu.add(frenchKissMi);
        pulsarMi.setEnabled(false);
        starMi.setEnabled(false);
        crossMi.setEnabled(false);
        frenchKissMi.setEnabled(false);

        oscMenu.add(period2Menu);
        oscMenu.add(period3Menu);

        // Spaceships Submenu:
        JMenu spaceMenu = new JMenu("Spaceships");
        JMenuItem gliderMi = new JMenuItem("Glider");
		gliderMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				gliderDialog.setLocationRelativeTo(contentPane);
				gliderDialog.setVisible(true);
			}
        });
        JMenuItem lwssMi   = new JMenuItem("LWSS");
		lwssMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				lwssDialog.setLocationRelativeTo(contentPane);
				lwssDialog.setVisible(true);
			}
        });
        spaceMenu.add(gliderMi);
        spaceMenu.add(lwssMi);

        // Chaotic Submenu:
        JMenu chaosMenu = new JMenu("Chaotic");
        JMenuItem rPentMi   = new JMenuItem("R-pentomino");
		rPentMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				rPentDialog.setLocationRelativeTo(contentPane);
				rPentDialog.setVisible(true);
			}
        });
        JMenuItem diehardMi = new JMenuItem("Diehard");
		diehardMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				diehardDialog.setLocationRelativeTo(contentPane);
				diehardDialog.setVisible(true);
			}
        });
        JMenuItem acornMi   = new JMenuItem("Acorn");
		acornMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				acornDialog.setLocationRelativeTo(contentPane);
				acornDialog.setVisible(true);
			}
        });
        chaosMenu.add(rPentMi);
        chaosMenu.add(diehardMi);
        chaosMenu.add(acornMi);

        // User Defined Submenu:
        JMenu userMenu = new JMenu("User Defined");
        JMenuItem emptyMi = new JMenuItem("Empty");
        userMenu.add(emptyMi);
        emptyMi.setEnabled(false);

        presetsMenu.add(stillMenu);
        presetsMenu.add(oscMenu);
        presetsMenu.add(spaceMenu);
        presetsMenu.add(chaosMenu);
        presetsMenu.addSeparator();
        presetsMenu.add(userMenu);
        menubar.add(presetsMenu);

        // Help Menu:
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem htmlGuideMi = new JMenuItem("User Guide (HTML Tree)");
        JMenuItem pdfGuideMi = new JMenuItem("User Guide (PDF)");
        JMenuItem colourKeyMi = new JMenuItem("Extended Colour Key");
        JMenuItem aboutMi     = new JMenuItem("About");
		aboutMi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				aboutWindow.setLocationRelativeTo(contentPane);
				aboutWindow.setVisible(true);
			}
        });
        helpMenu.add(htmlGuideMi);
        helpMenu.add(pdfGuideMi);
        helpMenu.addSeparator();
        helpMenu.add(colourKeyMi);
        helpMenu.addSeparator();
        helpMenu.add(aboutMi);
        menubar.add(helpMenu);

        setJMenuBar(menubar);

		// Set constraints and add components:
		colourLabel.setFont(font4_mono_b);
		set_constraints( contentPane, new GridBagConstraints(), colourLabel, 0,1,4,1, new Insets(20,0,0,0) );

		Color[] colourArray = { Color.white, Color.green, Color.blue, Color.red };
		String[] cellTypeString = {
			" = Empty Cell "," = Underpopulated "," = Next Generation "," = Overpopulated "
		};
		for(int i=0; i<4; i++)
		{
			colourPanel[i] = new JPanel();
			colourPanel[i].setBackground(colourArray[i]);
			colourPanel[i].setPreferredSize(new Dimension(35,35) );
			set_constraints( contentPane, new GridBagConstraints(),
				colourPanel[i], 0,(i+3),1,1, new Insets(2,2,2,2)
			);

			cellTypeLabel[i] = new JLabel(cellTypeString[i]);
			cellTypeLabel[i].setFont(font3_mono_b);
			set_constraints( contentPane, new GridBagConstraints(), cellTypeLabel[i], 1,(i+3),3,1 );
		}

		title.setForeground(Color.black);
		title.setFont(font5_mono_b);
		title.setHorizontalAlignment(JLabel.CENTER);
		set_constraints(contentPane, new GridBagConstraints(), title, 4,0,10,1);

		// Add game grid:
		for(int i=0; i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				gridFlag[i][j] = false;
				grid[i][j] = new JPanel();
				grid[i][j].setBackground(Color.white);
				grid[i][j].setPreferredSize( new Dimension(35,35));
				grid[i][j].addMouseListener(this);
				if(j == 0)
				{
					set_constraints( contentPane, new GridBagConstraints(),
						grid[i][j], (i+4),(j+1),1,1, new Insets(21,1,1,1)
					);
				}
				else
				{
					set_constraints( contentPane, new GridBagConstraints(),
						grid[i][j], (i+4),(j+1),1,1, new Insets(1,1,1,1)
					);
				}
			}
		}

		// Add buttons on the left column:
		playButton.setFont(font4_mono_b);
		playButton.setBackground(Color.green);
		playButton.setForeground(Color.blue );
		playButton.setFocusable(false);
		set_constraints( contentPane, new GridBagConstraints(), playButton, 6,12,3,1, new Insets(8,0,0,0) );

		stopButton.setFont(font4_mono_b);
		stopButton.setBackground(darkOrange);
		stopButton.setForeground(Color.blue);
		stopButton.setFocusable(false);
		stopButton.setEnabled(false);
		set_constraints( contentPane, new GridBagConstraints(), stopButton, 9,12,3,1, new Insets(8,0,0,0) );

		newButton.setFont(font4_mono_b);
		newButton.setBackground(Color.magenta);
		newButton.setFocusable(false);
		newButton.setEnabled(false);
		newMi.setEnabled(false);
		set_constraints( contentPane, new GridBagConstraints(), newButton, 6,13,6,1 );

		openButton.setFont(font4_mono_b);
		openButton.setBackground(Color.yellow);
		openButton.setFocusable(false);
		openButton.setEnabled(false);
		set_constraints( contentPane, new GridBagConstraints(), openButton, 6,14,6,1 );

		saveButton.setFont(font4_mono_b);
		saveButton.setBackground(Color.cyan);
		saveButton.setFocusable(false);
		saveButton.setEnabled(false);
		set_constraints( contentPane, new GridBagConstraints(), saveButton, 6,15,6,1 );

		shiftLabel.setFont(font4_mono_b);
		shiftLabel.setHorizontalAlignment(JLabel.LEFT);
		set_constraints( contentPane, new GridBagConstraints(), shiftLabel, 1,11,3,1 );

		String[] shiftString = { "Up","Down"," Left","Right","\\","\\","/","/" };
		for(int i=0; i<8; i++)
		{
			shiftButton[i] = new JButton(shiftString[i]);
			shiftButton[i].setFont(font3_mono_b);
			shiftButton[i].setForeground(Color.blue);
			if(i<4) { shiftButton[i].setBackground(Color.green); }
			else    { shiftButton[i].setBackground(Color.cyan ); }
			shiftButton[i].setFocusable(false);
		}
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[0], 2,12,1,1, new Insets(8,0,0,0) );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[1], 2,14,1,1 );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[2], 1,13,1,1 );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[3], 3,13,1,1 );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[4], 1,12,1,1, new Insets(8,0,0,0) );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[5], 3,14,1,1 );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[6], 3,12,1,1, new Insets(8,0,0,0) );
		set_constraints( contentPane, new GridBagConstraints(), shiftButton[7], 1,14,1,1 );

		shiftPanel.setBackground(Color.cyan);
		set_constraints( contentPane, new GridBagConstraints(), shiftPanel, 2,13,1,1, new Insets(2,2,2,2) );

		// Add components on the right column:
		infoLabel.setFont(font4_mono_b);
		set_constraints( contentPane, new GridBagConstraints(), infoLabel, 14,1,5,1, new Insets(0,35,0,0) );

		clearButton.setBackground(Color.green);
		//clearButton.setForeground(Color.blue );
		clearButton.setFont(font3_mono_b);
		clearButton.setFocusable(false);
		//clearButton.setPreferredSize(new Dimension(10,10) );
		set_constraints( contentPane, new GridBagConstraints(), clearButton, 19,1,1,1, new Insets(20,0,0,0 ) );

		//infoScrollPane = new JScrollPane(info);
		info.setBackground(Color.black);
		info.setForeground(Color.green);
		info.setFont(font2_mono_b);
		info.setEditable(false);
		info.setFocusable(false);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setBorder( BorderFactory.createEmptyBorder(1,7,1,7) );
		infoScrollPane.setPreferredSize(new Dimension(400,280) );
		set_constraints( contentPane, new GridBagConstraints(), infoScrollPane, 14,2,6,8, new Insets(0,35,0,0) );

		settingsLabel.setFont(font4_mono_b);
		set_constraints( contentPane, new GridBagConstraints(), settingsLabel, 14,11,5,1, new Insets(0,12,0,0) );

		evolveField.setBackground(Color.black);
		evolveField.setForeground(Color.green);
		evolveField.setFont(font4_mono_b);
		evolveField.setEditable(false);
		evolveField.setFocusable(false);
		set_constraints( contentPane, new GridBagConstraints(), evolveField, 14,12,1,1, new Insets(8,0,0,0) );

		forwardsButton.setBackground(Color.green);
		forwardsButton.setForeground(Color.blue);
		forwardsButton.setFont(font4_mono_b);
		forwardsButton.setFocusable(false);
		forwardsButton.setEnabled(false);
		forwardsButton.setPreferredSize( new Dimension(60,35) );
		set_constraints( contentPane, new GridBagConstraints(), forwardsButton, 15,12,2,1, new Insets(8,0,0,0) );

		backwardsButton.setBackground(darkOrange);
		backwardsButton.setForeground(Color.blue);
		backwardsButton.setFont(font4_mono_b);
		backwardsButton.setFocusable(false);
		backwardsButton.setEnabled(false);
		set_constraints( contentPane, new GridBagConstraints(), backwardsButton, 17,12,3,1, new Insets(8,0,0,70) );

		modeField.setBackground(Color.black);
		modeField.setForeground(Color.magenta);
		modeField.setFont(font4_mono_b);
		modeField.setEditable(false);
		modeField.setFocusable(false);
		set_constraints( contentPane, new GridBagConstraints(), modeField, 14,13,2,1 );

		modeButton.setBackground(Color.magenta);
		modeButton.setFont(font4_mono_b);
		modeButton.setFocusable(false);
		set_constraints( contentPane, new GridBagConstraints(), modeButton, 16,13,2,1, new Insets(0,0,0,50) );

		timeArea.setBackground(Color.black);
		timeArea.setForeground(Color.cyan);
		timeArea.setFont(font4_mono_b);
		timeArea.setEditable(false);
		timeArea.setFocusable(false);
		timeArea.setLineWrap(true);
		timeArea.setWrapStyleWord(true);
		timeArea.setBorder( BorderFactory.createEmptyBorder(8,11,0,0) );
		set_constraints( contentPane, new GridBagConstraints(), timeArea, 14,14,2,2, new Insets(1,1,1,1) );

		timeUpButton.setBackground(Color.cyan);
		timeUpButton.setForeground(Color.red);
		timeUpButton.setFont(font4_mono_b);
		timeUpButton.setFocusable(false);
		timeUpButton.setPreferredSize( new Dimension(35,35) );
		set_constraints( contentPane, new GridBagConstraints(), timeUpButton, 16,14,1,1 );

		timeDownButton.setBackground(Color.cyan);
		timeDownButton.setForeground(Color.red);
		timeDownButton.setFont(font4_mono_b);
		timeDownButton.setFocusable(false);
		timeDownButton.setPreferredSize( new Dimension(35,35) );
		set_constraints( contentPane, new GridBagConstraints(), timeDownButton, 16,15,1,1 );

		timePanel.setBackground(Color.lightGray);
		timePanel.setPreferredSize( new Dimension(100,35) );
		set_constraints( contentPane, new GridBagConstraints(), timePanel, 17,14,1,1 );

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				quitDialog.setLocationRelativeTo(contentPane);
				quitDialog.setVisible(true);
			}
		});

		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cellCount != 0)
				{
					if(!gameStarted) { storeStartFlags(); }

					evolver.start();

					if(forward)
					{
						forwardsButton.setEnabled(false);
						backwardsButton.setEnabled(true);
					}
					else
					{
						forwardsButton.setEnabled(true);
						backwardsButton.setEnabled(false);
					}
					playButton.setEnabled(false);
					stopButton.setEnabled(true );
					play = true;
				}
				else
				{
					String msg = "Cells must be added before the starting the game...";

					postInfo(msg,true);
				}
			}
		});
		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				evolver.cancel();
				playButton.setEnabled(true );
				stopButton.setEnabled(false);
				forwardsButton .setEnabled(false);
				backwardsButton.setEnabled(false);
				play = false;
			}
		});
		clearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ info.setText(""); }
		});
		forwardsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cellCount != 0)
				{
					if(!gameStarted) { storeStartFlags(); }

					if(auto)
					{
						if(!play)
						{
							evolver.start();
							play = true;
						}
						evolver.setDirection(true);
						evolveField.setForeground(Color.green);
						forwardsButton.setEnabled(false);
						backwardsButton.setEnabled(true);
					}
					else { createNextGeneration(); }

					forward = true;

					// Debug...
					// System.out.printf("backwardsIndex = "+backwardsIndex+"\n");
					// System.out.printf("maxBackwardsIndex = "+maxBackwardsIndex+"\n");
				}
				else
				{
					String msg = "Cells must be added before the starting the game...";

					postInfo(msg,true);
				}
			}
		});
		backwardsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(forward) { backwardsIndex--; }

				if(auto)
				{
					if(!play)
					{
						evolver.start();
						play = true;
					}
					evolver.setDirection(false);
					evolveField.setForeground(Color.red);
					forwardsButton.setEnabled(true);
					backwardsButton.setEnabled(false);
				}
				else { createLastGeneration(); }

				// Debug...
				// System.out.printf("backwardsIndex = "+backwardsIndex+"\n");
				// System.out.printf("maxBackwardsIndex = "+maxBackwardsIndex+"\n");
			}
		});
		modeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cellCount != 0)
				{
					if(!gameStarted) { storeStartFlags(); }
				}

				if(auto)
				{
					evolver.cancel();
					playButton.setEnabled(false);
					stopButton.setEnabled(false);
					evolveField.setForeground(Color.green);
					forwardsButton .setEnabled(true);
					backwardsButton.setEnabled(true);
					modeField.setForeground(Color.yellow);
					modeField.setText(" Mode = MANUAL ");
					modeButton.setBackground(Color.yellow);
					auto = false;
					play = false;
				}
				else
				{
					evolver.setDirection(true);
					if(cellCount != 0)
					{
						evolver.start();
						playButton.setEnabled(false);
						stopButton.setEnabled(true );
					}
					else
					{
						playButton.setEnabled(true );
						stopButton.setEnabled(false);
					}
					forwardsButton.setEnabled(false);
					backwardsButton.setEnabled(true);

					modeField.setForeground(Color.magenta);
					modeField.setText(" Mode = AUTO   ");
					modeButton.setBackground(Color.magenta);
					auto = true;
					play = true;
					forward = true;
				}
			}
		});
		timeUpButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(stepTime < 1.91)
				{
					stepTime += 0.1;
					resetTimeArea(stepTime);
					evolver.setStepTime(stepTime);
				}
			}
		});
		timeDownButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(stepTime > 0.11)
				{
					stepTime -= 0.1;
					resetTimeArea(stepTime);
					evolver.setStepTime(stepTime);
				}
			}
		});
		// Shift Up Button...
		shiftButton[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(0,-1); }
		});
		// Shift Down Button...
		shiftButton[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(0,1); }
		});
		// Shift Left Button...
		shiftButton[2].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(-1,0); }
		});
		// Shift Right Button...
		shiftButton[3].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(1,0); }
		});
		// Shift Diagonally Up & Left
		shiftButton[4].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(-1,-1); }
		});
		// Shift Diagonally Down & Right
		shiftButton[5].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(1,1); }
		});
		// Shift Diagonally Up & Right
		shiftButton[6].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(1,-1); }
		});
		// Shift Diagonally Down & Left
		shiftButton[7].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ shiftCells(-1,1); }
		});
		newButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newDialog.setLocationRelativeTo(contentPane);
				newDialog.setVisible(true);
			}
		});

		quitDialogOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				quitDialog.dispose();
				System.exit(0);
			}
		});
		newDialogOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newDialog.setVisible(false);
				reinitializeGame();
			}
		});
		blockOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				blockDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][4] = true;
				gridFlag[4][5] = true;
				gridFlag[5][4] = true;
				gridFlag[5][5] = true;
				cellCount = 4;
				recolorizeLiveCells();
				info.setText("System Preset:\nStill Life: 'Block'\n");
			}
		});
		beehiveOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				beehiveDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][4] = true;
				gridFlag[4][5] = true;
				gridFlag[5][3] = true;
				gridFlag[5][6] = true;
				gridFlag[6][4] = true;
				gridFlag[6][5] = true;
				cellCount = 6;
				recolorizeLiveCells();
				info.setText("System Preset:\nStill Life: 'Beehive'\n");
			}
		});
		loafOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				loafDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][3] = true;
				gridFlag[5][3] = true;
				gridFlag[3][4] = true;
				gridFlag[4][5] = true;
				gridFlag[6][4] = true;
				gridFlag[6][5] = true;
				gridFlag[5][6] = true;
				cellCount = 7;
				recolorizeLiveCells();
				info.setText("System Preset:\nStill Life: 'Loaf'\n");
			}
		});
		boatOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boatDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][3] = true;
				gridFlag[5][3] = true;
				gridFlag[4][4] = true;
				gridFlag[5][5] = true;
				gridFlag[6][4] = true;
				cellCount = 5;
				recolorizeLiveCells();
				info.setText("System Preset:\nStill Life: 'Boat'\n");
			}
		});
		tubOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tubDialog.setVisible(false);
				reinitializeGame();
				gridFlag[5][3] = true;
				gridFlag[4][4] = true;
				gridFlag[5][5] = true;
				gridFlag[6][4] = true;
				cellCount = 4;
				recolorizeLiveCells();
				info.setText("System Preset:\nStill Life: 'Tub'\n");
			}
		});
		blinkerOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				blinkerDialog.setVisible(false);
				reinitializeGame();
				gridFlag[3][4] = true;
				gridFlag[4][4] = true;
				gridFlag[5][4] = true;
				cellCount = 3;
				recolorizeLiveCells();
				info.setText("System Preset:\nPeriod 2 Oscillator: 'Blinker'\n");
			}
		});
		toadOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				toadDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][4] = true;
				gridFlag[5][4] = true;
				gridFlag[6][4] = true;
				gridFlag[3][5] = true;
				gridFlag[4][5] = true;
				gridFlag[5][5] = true;
				cellCount = 6;
				recolorizeLiveCells();
				info.setText("System Preset:\nPeriod 2 Oscillator: 'Toad'\n");
			}
		});
		beaconOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				beaconDialog.setVisible(false);
				reinitializeGame();
				gridFlag[3][3] = true;
				gridFlag[4][3] = true;
				gridFlag[3][4] = true;
				gridFlag[6][5] = true;
				gridFlag[5][6] = true;
				gridFlag[6][6] = true;
				cellCount = 6;
				recolorizeLiveCells();
				info.setText("System Preset:\nPeriod 2 Oscillator: 'Beacon'\n");
			}
		});
		gliderOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gliderDialog.setVisible(false);
				reinitializeGame();
				gridFlag[0][2] = true;
				gridFlag[1][2] = true;
				gridFlag[2][2] = true;
				gridFlag[2][1] = true;
				gridFlag[1][0] = true;
				cellCount = 5;
				recolorizeLiveCells();
				info.setText("System Preset:\nSpaceship: 'Glider'\n");
			}
		});
		lwssOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				lwssDialog.setVisible(false);
				reinitializeGame();
				gridFlag[1][5] = true;
				gridFlag[2][5] = true;
				gridFlag[3][5] = true;
				gridFlag[4][5] = true;
				gridFlag[4][4] = true;
				gridFlag[4][3] = true;
				gridFlag[3][2] = true;
				gridFlag[0][2] = true;
				gridFlag[0][4] = true;
				cellCount = 9;
				recolorizeLiveCells();
				info.setText("System Preset:\nSpaceship: 'LWSS' (Lightweight Spaceship)\n");
			}
		});
		rPentOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				rPentDialog.setVisible(false);
				reinitializeGame();
				gridFlag[4][4] = true;
				gridFlag[5][4] = true;
				gridFlag[4][5] = true;
				gridFlag[4][6] = true;
				gridFlag[3][5] = true;
				cellCount = 5;
				recolorizeLiveCells();
				info.setText("System Preset:\nChoas: 'R-pentomino'\n");
			}
		});
		diehardOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				diehardDialog.setVisible(false);
				reinitializeGame();
				gridFlag[1][4] = true;
				gridFlag[2][4] = true;
				gridFlag[2][5] = true;
				gridFlag[6][5] = true;
				gridFlag[7][5] = true;
				gridFlag[8][5] = true;
				gridFlag[7][3] = true;
				cellCount = 7;
				recolorizeLiveCells();
				info.setText("System Preset:\nChaos: 'Diehard'\n");
			}
		});
		acornOption[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				acornDialog.setVisible(false);
				reinitializeGame();
				gridFlag[2][3] = true;
				gridFlag[1][5] = true;
				gridFlag[2][5] = true;
				gridFlag[4][4] = true;
				gridFlag[5][5] = true;
				gridFlag[6][5] = true;
				gridFlag[7][5] = true;
				cellCount = 7;
				recolorizeLiveCells();
				info.setText("System Preset:\nChoas: 'Acorn'\n");
			}
		});

		pack();
		setVisible(true);

        // Set Panel & JScrollPane on the about window JFrame's Container...
      	aboutPane = aboutWindow.getContentPane();
      	aboutPane.add(aboutScrollPane);
      	aboutTextArea.setText(
			 "Author of this Application: Dr Gareth Olubunmi Hughes (14 February 2017 - Present)\n"
			+"Copyright (c) : Dr Gareth Olubunmi Hughes (14 February 2017 - Present)\n\n"

			+"Gareth Hughes\n"
			+"Application for the BBC Software Engineering Graduate Trainee Scheme\n"
			+"Technical Test\n"
			+"An application to implement the rules of Conway's Game of Life"
		);
      	aboutTextArea.setEditable(false);
      	aboutTextArea.setFocusable(false);
      	aboutTextArea.setBackground(Color.black);
      	aboutTextArea.setForeground(Color.green);
      	aboutTextArea.setFont(font2_mono_b);
      	aboutTextArea.setBorder( BorderFactory.createEmptyBorder(10,15,15,15) );
      	aboutWindow.setResizable(false);
      	aboutWindow.pack();
	}

	public static void main( String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{ new GameOfLife(); }
		});
	}
}

class Evolver extends Thread
{
	private boolean forward;
	private int stepTime;
  	private Thread t;
  	public GameOfLife game;

  	public Evolver(GameOfLife g, double time, boolean f)
  	{
		game = g;
		forward = f;
		setStepTime(time);
	}

	public void setStepTime(double time)
	{
		time = Math.round(time * 10);
		time /= 10;           // rounds to 1 decimal place
		time = time * 1000;   // converts seconds to miliseconds
		stepTime = (int)time; // casts a double to an int

		System.out.printf("Step Time = " + stepTime + "ms\n");
	}

	public void setDirection(boolean f) { forward = f; }

	public synchronized void start()
	{
		if (t == null)
		{
			t = new Thread(this);
			t.start();
		}
	}

	public void cancel()
	{
		if (t != null)
		{
			t.interrupt(); // Assuming the thread has sufficient time to be interrupted
			t = null;	   // and stop drawing before destroy() is called
		}
	}

  	public synchronized void run()
  	{
		try
		{
			while (!Thread.currentThread().isInterrupted())
			{
				if(forward) { game.createNextGeneration(); }
				else        { game.createLastGeneration(); }
				t.sleep(stepTime);
			}
		}
		catch(InterruptedException e){} // Allows the thread to exit
	}									// throw new RuntimeException(e);
}										// would print any intercepted blocking methods
