package com.bss.AIFive;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessBoard extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MARGIN = 15;// 边距
	public static final int SPAN = 40;// 网格宽度
	public static final int ROWS = 14;// 棋盘行数
	public static final int COLS = 14;// 棋盘列数
	Image img;
	Chess[] chessList;// 记录已经下在棋盘上的棋子
	int chessCount;// 当前棋子个数
	boolean isBlack;// 下一步是否是黑棋下
	boolean isGamming = false;// 是否正在游戏

	int computerColor;// 计算机棋子颜色
	boolean isComputerGo;// 是否计算机下棋
	private Five five;

	int[][] boardStatus;// 0无棋子1黑棋2白棋

	Evaluate e;// 估值类

	public ChessBoard(Five five) {
		this.five = five;
		boardStatus = new int[COLS + 1][ROWS + 1];
		for (int i = 0; i <= COLS; i++) {
			for (int j = 0; j <= ROWS; j++) {
				boardStatus[i][j] = 0;// 棋盘初始状态为空
			}
		}
		chessList = new Chess[(COLS + 1) * (ROWS + 1)];
		chessCount = 0;
		img = Toolkit.getDefaultToolkit().getImage("img/board.jpg");
		this.addMouseListener(new MouseMonitor());
		this.addMouseMotionListener(new MouseMotionMonitor());

		e = new Evaluate(this);
	}

	// 画棋盘
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this);
		for (int i = 0; i <= ROWS; i++) {// 画横线
			g.drawLine(MARGIN, MARGIN + i * SPAN, MARGIN + COLS * SPAN, MARGIN + i * SPAN);
		}
		for (int i = 0; i <= COLS; i++) {// 画竖线
			g.drawLine(MARGIN + i * SPAN, MARGIN, MARGIN + i * SPAN, MARGIN + ROWS * SPAN);
		}
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);// 1
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);// 2
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);// 3
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);// 4
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);// 5
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);// 6
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);// 7
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);// 8
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);// 9

		// 绘制棋子
		for (int i = 0; i < chessCount; i++) {
			chessList[i].draw(g);
			if (i == chessCount - 1) {
				int xPos = chessList[i].getCol() * SPAN + MARGIN;
				int yPos = chessList[i].getRow() * SPAN + MARGIN;
				g.setColor(Color.red);
				g.drawRect(xPos - Chess.DIAMETER / 2, yPos - Chess.DIAMETER / 2, Chess.DIAMETER, Chess.DIAMETER);
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(MARGIN * 2 + SPAN * COLS, MARGIN * 2 + SPAN * ROWS);
	}

	// 该位置是否有棋子
	private boolean hasChess(int col, int row) {
		for (int i = 0; i < chessCount; i++) {
			Chess ch = chessList[i];
			if (ch != null && ch.getCol() == col && ch.getRow() == row)
				return true;
		}
		return false;
	}

	// 鼠标动作
	class MouseMonitor extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (!isGamming)
				return;
			if (isComputerGo)
				return;
			// 转化为网格搜索
			int col = (e.getX() - MARGIN + SPAN / 2) / SPAN;
			int row = (e.getY() - MARGIN + SPAN / 2) / SPAN;
			// 落在棋盘外不能落子
			if (col < 0 || col > COLS || row < 0 || row > ROWS) {
				return;
			}
			// 已经有棋子，不能落子
			if (hasChess(col, row))
				return;
			manGo(col, row);// 人下棋，下完isBlack和isComputerGo变化
			if (!isGamming)// 如果赢了
				return;
			computerGo();
		}
	}

	// 改变鼠标形状
	class MouseMotionMonitor extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent e) {
			int col = (e.getX() - MARGIN + SPAN / 2) / SPAN;
			int row = (e.getY() - MARGIN + SPAN / 2) / SPAN;
			if (col < 0 || col > COLS || row < 0 || row > ROWS || !isGamming || hasChess(col, row))
				ChessBoard.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			else
				ChessBoard.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	// 重新开始游戏
	public void restartGame() {
		for (int i = 0; i < chessList.length; i++) {
			chessList[i] = null;
		}
		for (int i = 0; i <= COLS; i++) {
			for (int j = 0; j <= ROWS; j++) {
				boardStatus[i][j] = 0;// 棋盘初始状态为空
			}
		}
		isBlack = true;
		isGamming = true;
		isComputerGo = five.computerFirst.isSelected();// 选中复选框，表示计算机先走
		computerColor = isComputerGo ? 1 : 2;
		chessCount = 0;
		if (isComputerGo) {
			computerGo();
		}
		paintComponent(this.getGraphics());// repaint()有时不能及时更新界面
	}

	// 悔棋
	public void goback() {
		//电脑走棋过程中按悔棋可能造成错误
		if (chessCount < 2)
			return;
		if (!isGamming) {
			isGamming = true;
		}
		if (!isComputerGo) {
			int i = chessList[chessCount - 1].getCol();
			int j = chessList[chessCount - 1].getRow();
			boardStatus[i][j] = 0;
			i = chessList[chessCount - 2].getCol();
			j = chessList[chessCount - 2].getRow();
			boardStatus[i][j] = 0;
			chessList[chessCount - 1] = null;
			chessList[chessCount - 2] = null;
			chessCount -= 2;
		} else {//如果是计算机走，则表明是禁手或者人胜利了，只需要拿掉一个棋子
			int i = chessList[chessCount - 1].getCol();
			int j = chessList[chessCount - 1].getRow();
			boardStatus[i][j] = 0;
			chessList[chessCount - 1] = null;
			chessCount -= 1;
			isBlack = !isBlack;
			isComputerGo = !isComputerGo;
		}

		paintComponent(this.getGraphics());
	}

	// 计算机下棋控制
	private void computerGo() {
		int pos[] = e.getTheBestPosition();//计算出最佳下棋位置
		putChess(pos[0], pos[1], isBlack ? Color.black : Color.white);
	}

	// 人下棋
	private void manGo(int col, int row) {
		putChess(col, row, isBlack ? Color.black : Color.white);

	}

	// 下棋
	private void putChess(int col, int row, Color color) {
		Chess ch = new Chess(ChessBoard.this, col, row, color);
		chessList[chessCount++] = ch;
		boardStatus[col][row] = (color == Color.BLACK) ? 1 : 2;
		paintComponent(this.getGraphics());
		// 是否有人胜利
		if (e.isWin(col, row, (color == Color.BLACK) ? 1 : 2)) {
			String user = isComputerGo ? "很遗憾，电脑" : "恭喜！你";
			String colorName = isBlack ? "执黑棋" : "执白棋";
			String msg = String.format("%s%s赢了", user, colorName);
			JOptionPane.showMessageDialog(ChessBoard.this, msg);
			isGamming = false;
		}
		// 黑棋是否禁手
		if (e.isForbidden(col, row) && isBlack) {
			String msg = String.format("禁手！你输了");
			JOptionPane.showMessageDialog(ChessBoard.this, msg);
			isGamming = false;
		}

		isBlack = !isBlack;
		isComputerGo = !isComputerGo;
	}
}
