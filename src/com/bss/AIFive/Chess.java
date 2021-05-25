package com.bss.AIFive;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

public class Chess {
	public static final int DIAMETER = ChessBoard.SPAN - 2;
	private int col;// 棋子的x索引
	private int row;// 棋子的y索引
	private Color color;
	ChessBoard cb;

	public Chess(ChessBoard cb, int col, int row, Color color) {
		super();
		this.col = col;
		this.row = row;
		this.color = color;
		this.cb = cb;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void draw(Graphics g) {
		int xPos = col * ChessBoard.SPAN + ChessBoard.MARGIN;
		int yPos = row * ChessBoard.SPAN + ChessBoard.MARGIN;
		Graphics2D g2d = (Graphics2D) g;
		RadialGradientPaint paint = null;
		int x = xPos + DIAMETER / 4;
		int y = yPos - DIAMETER / 4;
		float[] f = { 0f, 1f };
		Color[] c = { Color.WHITE, Color.BLACK };
		if (color == Color.black) {
			paint = new RadialGradientPaint(x, y, DIAMETER, f, c);
		} else if (color == Color.white) {
			paint = new RadialGradientPaint(x, y, DIAMETER * 2, f, c);
		}
		g2d.setPaint(paint);
		// 使边界更均匀
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
		Ellipse2D e = new Ellipse2D.Float(xPos - DIAMETER / 2, yPos - DIAMETER / 2, DIAMETER, DIAMETER);
		g2d.fill(e);
	}

}
