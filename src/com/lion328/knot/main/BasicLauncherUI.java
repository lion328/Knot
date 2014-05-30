/*
   Copyright 2014 Waritnan Sookbuntherng

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.lion328.knot.main;

import java.awt.EventQueue;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.lion328.knot.Downloader;
import com.lion328.knot.ILauncherUI;
import com.lion328.knot.MainController;
import com.lion328.knot.MinecraftServerStatus;
import com.lion328.knot.util.ImagePanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTextField;
import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class BasicLauncherUI implements ILauncherUI{

	private JFrame frame;
	private MainController mc;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private Point point;

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public BasicLauncherUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/com/lion328/knot/res/supermarket.ttf")).deriveFont(Font.PLAIN);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			font = font.deriveFont(24F);
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 800, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.getContentPane().setLayout(null);
		frame.setBackground(new Color(1.0f,1.0f,1.0f,0f));

		JPanel minimizeButton = new JPanel();
		minimizeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frame.setExtendedState(JFrame.ICONIFIED);
			}
		});
		
		JLabel titleTxt = new JLabel("CommKnot Launcher 1.0");
		titleTxt.setBounds(50, 12, 680, 26);
		titleTxt.setFont(font);
		frame.getContentPane().add(titleTxt);
		minimizeButton.setBounds(736, 5, 26, 38);
		minimizeButton.setOpaque(false);
		minimizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.getContentPane().add(minimizeButton);
		
		JPanel exitButton = new JPanel();
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mc.shutdown();
			}
		});
		exitButton.setBounds(762, 4, 28, 39);
		exitButton.setOpaque(false);
		exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.getContentPane().add(exitButton);
		
		JPanel titlebar = new JPanel();
		titlebar.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
            }

            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - point.x, currCoords.y - point.y);
            }
		});
		titlebar.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
                point = null;
            }
            public void mousePressed(MouseEvent e) {
                point = e.getPoint();
            }
            public void mouseExited(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseClicked(MouseEvent e) {
            }
		});
		titlebar.setBounds(7, 3, 788, 41);
		titlebar.setOpaque(false);

		
		frame.getContentPane().add(titlebar);
		
		JPanel playButton = new JPanel();
		playButton.setBounds(108, 189, 80, 35);
		playButton.setOpaque(false);
		playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.getContentPane().add(playButton);
		
		usernameField = new JTextField();
		usernameField.setBounds(30, 94, 152, 20);
		usernameField.setOpaque(false);
		usernameField.setBorder(BorderFactory.createEmptyBorder());
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(30, 159, 152, 20);
		passwordField.setOpaque(false);
		passwordField.setBorder(BorderFactory.createEmptyBorder());
		frame.getContentPane().add(passwordField);
		passwordField.setColumns(10);
		
		try {
			ImagePanel imagePanel = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("/com/lion328/knot/res/ui.png")));
			imagePanel.setBounds(0, 0, 800, 428);
			imagePanel.setOpaque(false);
			frame.getContentPane().add(imagePanel);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		frame.setLocationRelativeTo(null);
	}

	@Override
	public void updateController(MainController mc) {
		this.mc = mc;
	}

	@Override
	public void updateDownloadStatus(Downloader dl) {
		StringBuilder sb = new StringBuilder();
		sb.append("°”≈—ß¥“«πÏ‚À≈¥ ");
		sb.append(dl.getFile().getName());
		sb.append(dl);
		
	}

	@Override
	public void updateAuthenticationStatus(boolean vaild, String errorMessage) {
		JOptionPane.showMessageDialog(frame, errorMessage, "‡°‘¥¢ÈÕº‘¥æ≈“¥ - CommKnot", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void updateNews(String text) {
		//newsTxt.setText(text);
	}

	@Override
	public void close() {
		frame.dispose();
	}

	@Override
	public void updateServerStatus(MinecraftServerStatus mcss) {
		//statusLabel.setText("ºŸÈ‡≈ËπÕÕπ‰≈πÏ: " + mcss.getCurrentPlayer() + "/" + mcss.getMaxPlayer());
	}
}
