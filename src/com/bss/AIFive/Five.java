package com.bss.AIFive;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JToolBar;

public class Five extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JToolBar toolbar;
	private JButton startButton, backButton, exitButton;
	private ChessBoard boardPanel;
	JCheckBox computerFirst;

	public Five() {
		super("人机对战五子棋");
		toolbar = new JToolBar();
		startButton = new JButton("重新开始");
		backButton = new JButton("悔棋");
		exitButton = new JButton("退出");
		boardPanel = new ChessBoard(this);
		ActionMonitor monitor = new ActionMonitor();	
		computerFirst = new JCheckBox("计算机先");
		
		toolbar.add(computerFirst);
		toolbar.add(startButton);
		toolbar.add(backButton);
		toolbar.add(exitButton);
		
		this.add(boardPanel, BorderLayout.CENTER);
		this.add(toolbar, BorderLayout.NORTH);
		
		this.setLocation(200, 200);
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		startButton.addActionListener(monitor);
		backButton.addActionListener(monitor);
		exitButton.addActionListener(monitor);
	}

	public static void main(String[] args) {
		new Five();
	}

	class ActionMonitor implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == startButton) {
				boardPanel.restartGame();
			} else if (e.getSource() == backButton) {
				boardPanel.goback();
			} else if (e.getSource() == exitButton) {
				System.exit(0);
			}
		}

	}

}
