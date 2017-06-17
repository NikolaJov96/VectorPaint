package paint;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.BevelBorder;

@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class MainWindow extends JFrame {
	
	public static final int WINDOW_WIDTH = 745;
	public static final int WINDOW_HEIGHT = 480;
	public static final int MIN_WINDOW_WIDTH = 745;
	public static final int MIN_WINDOW_HEIGHT = 480;
	
	public static MainWindow mainWindow;

	private JMenuItem newFileItem = new JMenuItem("New File");
	private JMenuItem openFileItem = new JMenuItem("Open File");
	private JMenuItem saveItem = new JMenuItem("Save");
	private JMenuItem saveAsItem = new JMenuItem("Save As");
	private JMenuItem closeItem = new JMenuItem("Close");
	private JMenuItem exitItem = new JMenuItem("Exit");
	private JMenuItem authorItem = new JMenuItem("Author");
	
	private JButton bSelectMode = new JButton(new ImageIcon("res//SELECT.png"));
	private JButton bDeleteMode = new JButton(new ImageIcon("res//DELETE.png"));
	private JButton bLineMode = new JButton(new ImageIcon("res//LINE.png"));
	private JButton bMultiLineMode = new JButton(new ImageIcon("res//MULTI.png"));
	private JButton bClosedLineMode = new JButton(new ImageIcon("res//CLOSED.png"));
	private JButton bRect = new JButton(new ImageIcon("res//RECT.png"));
	private JButton bCenter = new JButton(new ImageIcon("res//CENTER.png"));
	private JButton bSelColor = new JButton();
	private JButton [] bColors = new JButton [16];
	private JComboBox comboLine = new JComboBox();
	private JButton bUndo = new JButton(new ImageIcon("res//UNDO.png"));
	private JButton bRedo = new JButton(new ImageIcon("res//REDO.png"));

	private Canvas canvas = new Canvas();
	private JLabel statusLabelL = new JLabel("status");
	private JLabel statusLabelR = new JLabel("status");
	
	Thread mousePositionThread;
	private String filePath;
	private int mods;
	private JButton selectedButton;
	private Stack<Modification> modStack = new Stack<Modification>();
	private Stack<Modification> modUndoneStack = new Stack<Modification>();
	
	{
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		Dimension prefDimension = new Dimension(40, 40);
		bSelectMode.setPreferredSize(prefDimension);
		bSelectMode.setDisabledIcon(new ImageIcon("res//SELECTs.png"));
		bDeleteMode.setPreferredSize(prefDimension);
		bDeleteMode.setDisabledIcon(new ImageIcon("res//DELETEs.png"));
		bLineMode.setPreferredSize(prefDimension);
		bLineMode.setDisabledIcon(new ImageIcon("res//LINEs.png"));
		bMultiLineMode.setPreferredSize(prefDimension);
		bMultiLineMode.setDisabledIcon(new ImageIcon("res//MULTIs.png"));
		bClosedLineMode.setPreferredSize(prefDimension);
		bClosedLineMode.setDisabledIcon(new ImageIcon("res//CLOSEDs.png"));
		bRect.setPreferredSize(prefDimension);
		bRect.setDisabledIcon(new ImageIcon("res//RECTs.png"));
		bCenter.setPreferredSize(prefDimension);
		
		bSelColor.setPreferredSize(prefDimension);
		bSelColor.setEnabled(false);
		for (int i = 0; i < 16; i++) {
			bColors[i] = new JButton();
			bColors[i].setPreferredSize(new Dimension(20, 20));
		}
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++)
			bColors[i + 2*j + 4*k].setBackground(new Color(i * 255, j * 255, k * 255));
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++)
			bColors[i + 2*j + 4*k + 8].setBackground(new Color(i * 200, j * 200, k * 200));
		
		comboLine.setPreferredSize(new Dimension(100, 30));
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		for (int i = 0; i < 4; i++) {
			final int ind = i;
			JPanel panel = new JPanel(){
			    public void paintComponent(Graphics g){
			    	super.paintComponent(g);
			    	Graphics2D g2 = (Graphics2D) g;
			    	g2.setColor(Color.BLACK);
			    	g2.setStroke(new BasicStroke((ind + 1) * Shape.STROKE_MUL));
			    	g2.drawLine(10, 15, 90, 15);
			    }
			};
			//panel.setPreferredSize(new Dimension(30, 10));
			panel.setSize(new Dimension(100, 30));
	        BufferedImage bi = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = bi.createGraphics();
	        panel.paint(g);
			dcbm.addElement(new ImageIcon(bi));
		}
		comboLine.setModel(dcbm);
		comboLine.setRenderer(new ComboBoxRenderer());
		
			
		bUndo.setPreferredSize(prefDimension);
		bRedo.setPreferredSize(prefDimension);
		canvas.setBackground(Color.WHITE);
	}
	
	public MainWindow() {
		super ("Paint");
		mainWindow = this;
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
		addComponents();
		resetContext();
		addListeners();
		
		mousePositionThread = new Thread(){
			public void run() {
				try {
					while (!interrupted()) {
						sleep(40);
						Point pos = ContextState.getWorldPosition(new Point(
								MouseInfo.getPointerInfo().getLocation().x - canvas.getLocationOnScreen().x,
								MouseInfo.getPointerInfo().getLocation().y - canvas.getLocationOnScreen().y));
						canvas.handleMouseMotion(pos);
						canvas.repaint();
						updateStatusR(pos.getX(), pos.getY());
					}
				} catch (InterruptedException e) {}
			}
		};
		
		setVisible(true);
		
		mousePositionThread.start();
	}
	
	private void addComponents() {
		addMenu();
		addToolbar();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(canvas);
		add(panel, "Center");
		
		addStatusBar();
	}
	
	private void addMenu() {
		JMenuBar menuBar;
		JMenu menu;

		menuBar = new JMenuBar();

		// File menu
		menu = new JMenu("File");
		menu.add(newFileItem);
		menu.add(openFileItem);
		menu.add(saveItem);
		menu.add(saveAsItem);
		menu.add(closeItem);
		menu.add(exitItem);
		menuBar.add(menu);

		// Help menu
		menu = new JMenu("Help");
		menu.add(authorItem);
		menuBar.add(menu);

		setJMenuBar(menuBar);
	}
	
	private void addToolbar() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(bSelectMode);
		panel.add(bDeleteMode);
		panel.add(bLineMode);
		panel.add(bMultiLineMode);
		panel.add(bClosedLineMode);
		panel.add(bRect);
		panel.add(bCenter);
		panel.add(bSelColor);
		
		JPanel cPanel = new JPanel(new GridLayout(2, 8));
		for (int i = 0; i < 16; i++) cPanel.add(bColors[i]);
		panel.add(cPanel);
		
		panel.add(comboLine);
		panel.add(bUndo);
		panel.add(bRedo);
		add(panel, "North");
	}

	private void addStatusBar() {
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		statusLabelR.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
		statusPanel.add(statusLabelL, BorderLayout.WEST);
		statusPanel.add(statusLabelR, BorderLayout.EAST);
		
		add(statusPanel, BorderLayout.SOUTH);
	}
	
	private void resetContext() {
		ContextState.resetContext();
		canvas.reset();
		filePath = null;
		mods = 0;
		bSelColor.setBackground(bColors[0].getBackground());
		comboLine.setSelectedIndex(0);
		if (selectedButton != null) selectedButton.setSelected(false);
		selectedButton = bSelectMode;
		selectedButton.setEnabled(false);
		modStack.removeAllElements();
		modUndoneStack.removeAllElements();
		updateStatusL();
		updateStatusR(0, 0);
		updateTitle();
	}
	
	private void addListeners() {
		// menu items:
		newFileItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (promptToClose()) resetContext();
			}
		});
		
		openFileItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (promptToClose()) resetContext();
				openFile();
			}
		});
		
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsFile();
			}
		});
		
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (promptToClose()) resetContext();
			}
		});
		
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (promptToClose()) { 
					mousePositionThread.interrupt();
					resetContext();
					dispose();
				}
			}
		});
		
		authorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainWindow, "Author: Nkola Jovanović", "Author", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		// Tool bar items:
		bSelectMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bSelectMode;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.SELECT);
				updateStatusL();
			}
		});
		bDeleteMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bDeleteMode;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.DELETE);
				updateStatusL();
			}
		});
		bLineMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bLineMode;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.LINE);
				updateStatusL();
			}
		});
		
		bMultiLineMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bMultiLineMode;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.MULTI_LINE);
				updateStatusL();
			}
		});
		
		bClosedLineMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bClosedLineMode;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.CLOSED_LINE);
				updateStatusL();
			}
		});
		
		bRect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton.setEnabled(true);
				selectedButton = bRect;
				selectedButton.setEnabled(false);
				canvas.setMode(Canvas.Mode.RECTANGLE);
				updateStatusL();
			}
		});
		
		bCenter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ContextState.worldCenter = new Point(0, 0);
				canvas.repaint();
			}
		});
		
		ActionListener colorListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color newColor = ((JButton) (event.getSource())).getBackground();
				if (!ContextState.selectedColor.equals(newColor)) {
					ContextState.selectedColor = newColor;
					bSelColor.setBackground(ContextState.selectedColor);
					canvas.updateColor();
					updateStatusL();
				}
			} 
		};
		for (int i = 0; i < 16; i++) bColors[i].addActionListener(colorListener);
		
		comboLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newLineWidthId = comboLine.getSelectedIndex() + 1;
				if (ContextState.lineWidthId != newLineWidthId) {
					ContextState.lineWidthId = newLineWidthId;
					canvas.updateLineWidth();
					updateStatusL();
				}
			}
		});
		
		bUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!modStack.isEmpty()) {
					Modification mod = modStack.pop();
					mod.undo(canvas);
					modUndoneStack.push(mod);
					mods--;
					if (mods == 0 || mods == -1) updateTitle();
				}
			}
		});
		
		bRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!modUndoneStack.isEmpty()) {
					Modification mod = modUndoneStack.pop();
					mod.redo(canvas);
					modStack.push(mod);
					mods++;
					if (mods == 0 || mods == 1) updateTitle();
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (promptToClose()) {
					mousePositionThread.interrupt();
					resetContext();
					dispose();					
				};
			}
		});
	}
	
	
	private boolean promptToClose() {
		if (mods != 0) {
			int option = JOptionPane.showConfirmDialog(mainWindow,
				    "Do you waint to save changes?",
				    "Closing image",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) return false;
			if (option == JOptionPane.YES_OPTION) if (!saveFile()) return false;
		}
		return true;
	}
	
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open file");
		if (fileChooser.showSaveDialog(mainWindow) != JFileChooser.APPROVE_OPTION) return;
		try {
			File inFile = fileChooser.getSelectedFile();
			filePath = inFile.getAbsolutePath();
			Scanner scanner = new Scanner(inFile);
			ContextState.unpackContext(scanner);
			canvas.unpackImage(scanner);
			scanner.close();
			bSelColor.setBackground(ContextState.selectedColor);
			comboLine.setSelectedIndex(ContextState.lineWidthId - 1);
			updateTitle();
		} catch (Exception e) { resetContext(); }
	}
	
	private boolean saveFile() {
		String path = mainWindow.filePath;
		if (path == null) return saveAsFile();
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(ContextState.packContext() + "\n" + canvas.packImage());
			writer.close();
			mods = 0;
			updateTitle();
		} catch  (Exception e) { return false; }
		return true;
	}
	
	private boolean saveAsFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save file");
		if (fileChooser.showSaveDialog(mainWindow) != JFileChooser.APPROVE_OPTION) return false;
		
		try {
			File outFile = fileChooser.getSelectedFile();
			filePath = outFile.getAbsolutePath();
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			writer.write(ContextState.packContext() + "\n" + canvas.packImage());
			writer.close();
			mods = 0;
			updateTitle();
		} catch (Exception e) { return false; }
		return true;
	}
	
	private void updateStatusL() {
		StringBuilder sb = new StringBuilder("Tool: ");
		switch (canvas.getMode()) {
		case SELECT: sb.append("SELECT"); break;
		case DELETE: sb.append("DELETE"); break;
		case LINE: sb.append("LINE"); break;
		case MULTI_LINE: sb.append("MULTI LINE"); break;
		case CLOSED_LINE: sb.append("CLOSED LINE"); break;
		case RECTANGLE: sb.append("RECTANGLE"); break;
		default: break;
		}
		sb.append(", Color: ").append(ContextState.selectedColor.getRed()).
		append(":").append(ContextState.selectedColor.getGreen()).
		append(":").append(ContextState.selectedColor.getBlue());
		sb.append(", Line width: ").append(ContextState.lineWidthId);
		
		statusLabelL.setText(sb.toString());
	}
	
	public void updateStatusR(double X, double Y) {
		statusLabelR.setText(String.format("%8.2f :%8.2f", X, Y));
	}
	
	public void addModification(Modification mod) {
		modStack.push(mod);
		modUndoneStack.removeAllElements();
		mods++;
		updateTitle();
	}
	
	private void updateTitle() {
		String title = "Paint | ";
		if (filePath != null) title += filePath;
		if (mods != 0) title += "*";
		super.setTitle(title);
	}
	
	public static void main(String [] arg) {
		new MainWindow();
	}

}

@SuppressWarnings({ "serial", "rawtypes" })
class ComboBoxRenderer extends JLabel implements ListCellRenderer {
	
	public ComboBoxRenderer() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	@SuppressWarnings("rawtypes")
	public Component getListCellRendererComponent(JList list, Object value, 
			int index, boolean isSelected, boolean cellHasFocus) {
		
		Icon icon = (Icon) value;
		
		setIcon(icon);
		setText("");
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
	
		return this;
	}

}