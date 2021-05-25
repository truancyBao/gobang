package com.bss.AIFive;

public class Evaluate {
	private static final int CHANG_LIAN = 500001;// 长连
	private static final int FIVE = 500000;// 连五
	private static final int HUO_FOUR = 10000;// 活四
	private static final int CHONG_FOUR = 1000;// 冲四
	private static final int HUO_THREE = 500;// 活三
	private static final int MIAN_THREE = 100;// 眠三
	private static final int HUO_TWO = 50;// 活二
	private ChessBoard cb;
	private int[][] blackValue;// 空位下黑子的价值
	private int[][] whiteValue;// 空位下白子的价值
	private int[][] staticValue;// 每点的棋盘价值

	private static final int LARGE_NUMBER = 10000000;// 一个很大的数
	private static final int SEARCH_DEPTH = 6;// 搜索深度
	private static final int SAMPLE_NUMBER = 10;// 搜索的样本数

	public Evaluate(ChessBoard cb) {
		this.cb = cb;
		blackValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		whiteValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		staticValue = new int[ChessBoard.COLS + 1][ChessBoard.ROWS + 1];
		// 黑白价值置零
		for (int i = 0; i <= ChessBoard.COLS; i++) {
			for (int j = 0; j <= ChessBoard.ROWS; j++) {
				blackValue[i][j] = 0;
				whiteValue[i][j] = 0;
			}
		}
		// 棋盘价值设置
		for (int i = 0; i <= ChessBoard.COLS / 2; i++) {
			for (int j = 0; j <= ChessBoard.ROWS / 2; j++) {
				staticValue[i][j] = i < j ? i : j;
				// 分成四块，分别复制第一块
				staticValue[ChessBoard.COLS - i][j] = staticValue[i][j];
				staticValue[i][ChessBoard.ROWS - j] = staticValue[i][j];
				staticValue[ChessBoard.COLS - i][ChessBoard.ROWS - j] = staticValue[i][j];
			}
		}
	}

	// 寻找棋型
	private int evaluateValue(int color, int col, int row, int dir) {
		int k, m;
		int value = 0;
		int chessCount1 = 1;// 指定颜色的棋子数
		int chessCount2 = 0;
		int chessCount3 = 0;
		int spaceCount1 = 0;// 一端空位数
		int spaceCount2 = 0;
		int spaceCount3 = 0;
		int spaceCount4 = 0;

		switch (dir) {
		case 1:// 水平方向
				// 向增加的方向查找相同颜色连续的棋子
			for (k = col + 1; k <= ChessBoard.COLS; k++) {
				if (cb.boardStatus[k][row] == color) {
					chessCount1++;// 计算连续棋子数1
				} else {
					break;
				}
			}
			// 在棋子尽头查找连续的空位
			while ((k <= ChessBoard.COLS) && (cb.boardStatus[k][row] == 0)) {
				spaceCount1++;// 碰到有棋子的格子就不进来,计算连续空位1
				k++;
			}
			if (spaceCount1 == 1) {// 只有一个空位的情况下
				while ((k <= ChessBoard.COLS) && (cb.boardStatus[k][row] == color)) {
					chessCount2++;// 同色则再计算第二段连续棋子数
					k++;
				}
				while ((k <= ChessBoard.COLS) && (cb.boardStatus[k][row] == 0)) {
					spaceCount2++;// 计算连续空位2
					k++;
				}
			}
			// 向相反方向查找相同颜色棋子
			for (k = col - 1; k >= 0; k--) {
				if (cb.boardStatus[k][row] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 向相反方向的棋子尽头查找连续的空格数
			while (k >= 0 && (cb.boardStatus[k][row] == 0)) {
				spaceCount3++;// 最多计算三段棋子即可
				k--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (cb.boardStatus[k][row] == color)) {
					chessCount3++;
					k--;
				}
				while ((k >= 0) && (cb.boardStatus[k][row] == 0)) {
					spaceCount4++;// 三段棋子四段空位
					k--;
				}
			}
			break;
		case 2:// 纵向
				// 向增加的方向查找相同颜色连续棋子
			for (k = row + 1; k <= ChessBoard.ROWS; k++) {
				if (cb.boardStatus[col][k] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 棋子尽头找连续空格
			while ((k <= ChessBoard.ROWS) && (cb.boardStatus[col][k] == 0)) {
				spaceCount1++;
				k++;
			}
			if (spaceCount1 == 1) {
				while ((k <= ChessBoard.ROWS) && (cb.boardStatus[col][k] == color)) {
					chessCount2++;
					k++;
				}
				while ((k <= ChessBoard.ROWS) && (cb.boardStatus[col][k] == 0)) {
					spaceCount2++;
					k++;
				}
			}
			// 向相反方向查找连续棋子
			for (k = row - 1; k >= 0; k--) {
				if (cb.boardStatus[col][k] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 反方向找连续空位
			while ((k >= 0) && (cb.boardStatus[col][k] == 0)) {
				spaceCount3++;
				k--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (cb.boardStatus[col][k] == color)) {
					chessCount3++;
					k--;
				}
				while ((k >= 0) && (cb.boardStatus[col][k] == 0)) {
					spaceCount4++;
					k--;
				}
			}
			break;
		case 3:// 左上到右下
				// 向增加的方向查找同色连续棋子
			for (k = col + 1, m = row + 1; (k <= ChessBoard.COLS) && (m <= ChessBoard.ROWS); k++, m++) {
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 在棋子尽头找连续空位
			while ((k <= ChessBoard.COLS) && (m <= ChessBoard.ROWS) && (cb.boardStatus[k][m] == 0)) {
				spaceCount1++;
				k++;
				m++;
			}
			if (spaceCount1 == 1) {
				while ((k <= ChessBoard.COLS) && (m <= ChessBoard.ROWS) && (cb.boardStatus[k][m] == color)) {
					chessCount2++;
					k++;
					m++;
				}
				while ((k <= ChessBoard.COLS) && (m <= ChessBoard.ROWS) && (cb.boardStatus[k][m] == 0)) {
					spaceCount2++;
					k++;
					m++;
				}
			}
			// 向相反方向找同色连续棋子
			for (k = col - 1, m = row - 1; (k >= 0) && (m >= 0); k--, m--) {
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 反方向找连续空位
			while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == 0)) {
				spaceCount3++;
				k--;
				m--;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == color)) {
					chessCount3++;
					k--;
					m--;
				}
				while ((k >= 0) && (m >= 0) && (cb.boardStatus[k][m] == 0)) {
					spaceCount4++;
					k--;
					m--;
				}
			}
			break;
		case 4:// 右上到左下
				// 查找连续的同色棋子
			for (k = col + 1, m = row - 1; k <= ChessBoard.COLS && m >= 0; k++, m--) {
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 查找连续空位
			while ((k <= ChessBoard.COLS && m >= 0) && (cb.boardStatus[k][m] == 0)) {
				spaceCount1++;
				k++;
				m--;
			}
			if (spaceCount1 == 1) {
				while ((k <= ChessBoard.COLS && m >= 0) && (cb.boardStatus[k][m] == color)) {
					chessCount2++;
					k++;
					m--;
				}
				while ((k <= ChessBoard.COLS && m >= 0) && (cb.boardStatus[k][m] == 0)) {
					spaceCount2++;
					k++;
					m--;
				}
			}
			// 反方向找连续同色棋子
			for (k = col - 1, m = row + 1; k >= 0 && m <= ChessBoard.ROWS; k--, m++) {
				if (cb.boardStatus[k][m] == color) {
					chessCount1++;
				} else {
					break;
				}
			}
			// 反方向找连续空位
			while (k >= 0 && m <= ChessBoard.ROWS && (cb.boardStatus[k][m] == 0)) {
				spaceCount3++;
				k--;
				m++;
			}
			if (spaceCount3 == 1) {
				while ((k >= 0) && (m <= ChessBoard.ROWS) && (cb.boardStatus[k][m] == color)) {
					chessCount3++;
					k--;
					m++;
				}
				while ((k >= 0) && (m <= ChessBoard.ROWS) && (cb.boardStatus[k][m] == 0)) {
					spaceCount4++;
					k--;
					m++;
				}
			}
			break;
		}
		// 只有同色棋子超过5才有价值
		if (chessCount1 + chessCount2 + chessCount3 + spaceCount1 + spaceCount2 + spaceCount3 + spaceCount4 >= 5) {
			value = getValue(chessCount1, chessCount2, chessCount3, spaceCount1, spaceCount2, spaceCount3, spaceCount4);
		}

		return value;
	}

	// 根据棋型计算价值
	private int getValue(int chessCount1, int chessCount2, int chessCount3, int spaceCount1, int spaceCount2,
			int spaceCount3, int spaceCount4) {
		int value = 0;
		switch (chessCount1) {
		case 6:
			value = CHANG_LIAN;
			break;
		case 5:
			value = FIVE;
			break;
		case 4:
			if ((spaceCount1 >= 1) && (spaceCount3 >= 1)) {// 活四
				value = HUO_FOUR;
			} else {
				value = CHONG_FOUR;
			}
			break;
		case 3:
			if (((spaceCount1 == 1) && (chessCount2 >= 1)) && ((spaceCount3 == 1) && (chessCount3 >= 1))) {
				value = HUO_FOUR;// AOAAAOA
			} else if ((spaceCount1 == 1 && chessCount2 >= 1) || (spaceCount3 == 1 && chessCount3 >= 1)) {
				value = CHONG_FOUR;// OOAAAOA或AOAAAOO
			} else if (((spaceCount1 >= 1) && (spaceCount3 >= 2)) || ((spaceCount1 >= 2) && (spaceCount3 >= 1))) {
				value = HUO_THREE;// OOAAAO
			} else {
				value = MIAN_THREE;// 可以细分
			}
			break;
		case 2:
			if ((spaceCount1 == 1) && (chessCount2 >= 2) && (spaceCount3 == 1) && (chessCount3 >= 2)) {
				value = HUO_FOUR;// AAOAAOAA
			} else if (((spaceCount1 == 1) && (chessCount2 >= 2)) || ((spaceCount3 == 1) && (chessCount3 >= 2))) {
				value = CHONG_FOUR;// AAOAA
			} else if (((spaceCount1 == 1) && (chessCount2 == 1) && (spaceCount2 >= 1) && (spaceCount3 >= 1))
					|| ((spaceCount1 >= 1) && (spaceCount3 == 1) && (chessCount3 == 1) && spaceCount4 >= 1)) {
				value = HUO_THREE;// AAOA
			} else if (spaceCount1 >= 2 && spaceCount3 >= 2) {
				value = HUO_TWO;
			}
			break;
		case 1:
			if ((spaceCount1 == 1 && chessCount2 >= 3) || (spaceCount3 == 1 && chessCount3 >= 3)) {
				value = CHONG_FOUR;// AOAAA
			} else if ((spaceCount1 == 1 && chessCount2 == 2 && spaceCount2 >= 1 && spaceCount3 >= 1)
					|| (spaceCount1 >= 1 && spaceCount3 == 1 && chessCount3 == 2 && spaceCount4 >= 1)) {
				value = HUO_THREE;// OAOAAO
			} else if ((spaceCount1 == 1 && chessCount2 == 2 && spaceCount2 >= 1 && spaceCount3 == 0)
					|| (spaceCount1 == 0 && spaceCount3 == 1 && chessCount3 == 2 && spaceCount4 >= 1)) {
				value = MIAN_THREE;// AOAA
			} else if ((spaceCount1 == 1 && chessCount2 == 1 && spaceCount2 >= 2 && spaceCount3 >= 1)
					|| (spaceCount1 >= 1 && spaceCount3 == 1 && chessCount3 == 1 && spaceCount4 >= 2)) {
				value = HUO_TWO;// OOAOAO
			}
			break;
		default:
			value = 0;
			break;
		}
		return value;
	}

	// 判断是否禁手
	public boolean isForbidden(int col, int row) {
		int m = 0;// 活三棋型个数
		int n = 0;// 四的个数
		int l = 0;
		for (int k = 1; k <= 4; k++) {
			if (evaluateValue(1, col, row, k) == HUO_THREE) {
				m++;
			}
			if ((evaluateValue(1, col, row, k) == HUO_FOUR) || (evaluateValue(1, col, row, k) == CHONG_FOUR)) {
				n++;
			}
			if (evaluateValue(1, col, row, k) == CHANG_LIAN) {
				l++;
			}
		}
		if (m >= 2 || n >= 2 || l >= 1) {
			return true;
		}

		return false;
	}

	// 是否连五
	public boolean isWin(int col, int row, int color) {
		for (int k = 1; k <= 4; k++) {
			if (color == 1 && evaluateValue(1, col, row, k) == FIVE) {
				return true;
			} else if (color == 2 && evaluateValue(2, col, row, k) >= FIVE) {
				return true;
			}
		}
		return false;
	}

	// 对当前整个局面评估
	private int evaluateGame() {// 根据boardStatus计算局面价值
		int value = 0;
		int i, j, k;
		int[] line = new int[ChessBoard.COLS + 1];// 取了最大值，不一定能填满
		// 水平估值
		for (j = 0; j <= ChessBoard.ROWS; j++) {
			for (i = 0; i <= ChessBoard.COLS; i++) {
				line[i] = cb.boardStatus[i][j];// 存下了一条线的棋盘状态
			}
			value += evaluateLine(line, ChessBoard.COLS + 1, 1);// 叠加计算一条线的黑子价值
			value -= evaluateLine(line, ChessBoard.COLS + 1, 2);// 负叠加计算一条线的白子价值
		}
		// 垂直
		for (i = 0; i <= ChessBoard.COLS; i++) {
			for (j = 0; j <= ChessBoard.ROWS; j++) {
				line[j] = cb.boardStatus[i][j];
			}
			value += evaluateLine(line, ChessBoard.ROWS + 1, 1);
			value -= evaluateLine(line, ChessBoard.ROWS + 1, 2);
		}
		// 左下到右上
		for (j = 4; j <= ChessBoard.ROWS; j++) {
			for (k = 0; k <= j; k++) {
				line[k] = cb.boardStatus[k][j - k];
			}
			value += evaluateLine(line, j + 1, 1);
			value -= evaluateLine(line, j + 1, 2);
		}
		for (j = 1; j <= ChessBoard.ROWS - 4; j++) {
			for (k = 0; k <= ChessBoard.COLS - j; k++) {
				line[k] = cb.boardStatus[k + j][ChessBoard.ROWS - k];
			}
			value += evaluateLine(line, ChessBoard.ROWS + 1 - j, 1);
			value -= evaluateLine(line, ChessBoard.ROWS + 1 - j, 2);
		}
		// 左上到右下估值
		for (j = 0; j <= ChessBoard.ROWS - 4; j++) {
			for (k = 0; k <= ChessBoard.ROWS - j; k++) {
				line[k] = cb.boardStatus[k][k + j];
			}
			value += evaluateLine(line, ChessBoard.ROWS + 1 - j, 1);
			value -= evaluateLine(line, ChessBoard.ROWS + 1 - j, 2);
		}
		for (i = 1; i <= ChessBoard.COLS - 4; i++) {
			for (k = 0; k <= ChessBoard.ROWS - i; k++) {
				line[k] = cb.boardStatus[k + i][k];
			}
			value += evaluateLine(line, ChessBoard.ROWS + 1 - i, 1);
			value -= evaluateLine(line, ChessBoard.ROWS + 1 - i, 2);
		}

		if (cb.computerColor == 1) {// 自己棋子的价值减掉别人棋子的价值就是这条线的价值
			return value;
		} else {
			return -value;
		}
	}

	// 对不同方向计算价值
	private int evaluateLine(int[] lineState, int num, int color) {// 一条线的棋盘状态，线的长度，棋子颜色
		int chess, space1, space2;
		int i, j;
		int value = 0;
		int begin, end;
		for (i = 0; i < num; i++) {
			if (lineState[i] == color) {
				chess = 1;// 需要查找的棋子数量
				begin = i;// 开始下标
				for (j = begin + 1; (j < num) && (lineState[j] == color); j++) {// 找连续棋子
					chess++;
				}
				if (chess < 2) {// 只有一个棋子的线位置没有价值
					continue;
				}
				end = j - 1;
				space1 = 0;
				space2 = 0;
				// 计算棋子前面空位
				for (j = begin - 1; (j >= 0) && ((lineState[j] == 0) || (lineState[j] == color)); j--) {
					space1++;
				}
				// 计算棋子后面空位
				for (j = end + 1; (j < num) && ((lineState[j] == 0) || (lineState[j] == color)); j++) {
					space2++;
				}
				if (chess + space1 + space2 >= 5) {
					value += getValue(chess, space1, space2);// 一条线同色所有棋型价值叠加
				}
				i = end + 1;
			}
		}
		return value;
	}

	// 根据价值判断棋型（重载）(这个地方并不合理,不过多步搜索时可以减小这种不合理)
	private int getValue(int chessCount, int spaceCount1, int spaceCount2) {
		int value = 0;
		switch (chessCount) {
		case 6:// 连七以上概率太低，不考虑
			value = CHANG_LIAN;
			break;
		case 5:
			value = FIVE;
			break;
		case 4:
			if ((spaceCount1 > 0) && (spaceCount2 > 0)) {// 活四，因为chess+space1+space2>=5,所以不会出现死四
				value = HUO_FOUR;
			} else {
				value = CHONG_FOUR;
			}
			break;
		case 3:
			if ((spaceCount1 > 0) && (spaceCount2 > 0)) {
				value = HUO_THREE;
			} else {
				value = MIAN_THREE;
			}
			break;
		case 2:
			if ((spaceCount1 > 0) && (spaceCount2 > 0)) {
				value = HUO_TWO;
			}
			break;
		default:
			value = 0;
			break;
		}
		return value;
	}

	// 找出价值最大的几个点（依靠对点的静态估计）
	private int[][] getTheMostValuablePositions() {
		int i, j, k = 0;
		int[][] allValue = new int[(ChessBoard.COLS + 1) * (ChessBoard.ROWS + 1)][3];// 保存横纵坐标及该点价值
		for (i = 0; i <= ChessBoard.COLS; i++) {
			for (j = 0; j <= ChessBoard.ROWS; j++) {
				if (cb.boardStatus[i][j] == 0) {
					allValue[k][0] = i;// 列坐标
					allValue[k][1] = j;// 横坐标
					allValue[k][2] = blackValue[i][j] + whiteValue[i][j] + staticValue[i][j];// 价值
					k++;
				}
			}
		}
		sort(allValue);// 直接对allValue排序
		int size = k < SAMPLE_NUMBER ? k : SAMPLE_NUMBER;// 空白位数和样本数对比
		int valuablePositons[][] = new int[size][3];
		// 将前size个位置赋给bestPositions
		for (i = 0; i < size; i++) {
			valuablePositons[i][0] = allValue[i][0];
			valuablePositons[i][1] = allValue[i][1];
			valuablePositons[i][2] = allValue[i][2];
		}
		return valuablePositons;
	}

	// 价值降序排序
	private void sort(int[][] allValue) {
		for (int i = 0; i < allValue.length - 1; i++) {
			for (int j = 0; j < allValue.length - 1 - i; j++) {
				int ti, tj, tvalue;
				if (allValue[j][2] < allValue[j + 1][2]) {
					tvalue = allValue[j][2];
					allValue[j][2] = allValue[j + 1][2];
					allValue[j + 1][2] = tvalue;
					ti = allValue[j][0];
					allValue[j][0] = allValue[j + 1][0];
					allValue[j + 1][0] = ti;
					tj = allValue[j][1];
					allValue[j][1] = allValue[j + 1][1];
					allValue[j + 1][1] = tj;
				}
			}
		}
	}

	// 计算最佳下棋位置
	int[] getTheBestPosition() {

		renewBWvalue();// 刷新blackValue和whiteValue

		if (cb.chessCount <= 1) {// 2个棋子以内只需要简单估计
			int k = 0;
			int[][] totalValue = new int[(ChessBoard.COLS + 1) * (ChessBoard.ROWS + 1)][3];
			for (int i = 0; i <= ChessBoard.COLS; i++) {
				for (int j = 0; j <= ChessBoard.ROWS; j++) {
					if (cb.boardStatus[i][j] == 0) {
						totalValue[k][0] = i;// 保存坐标
						totalValue[k][1] = j;
						totalValue[k][2] = blackValue[i][j] + whiteValue[i][j] + staticValue[i][j];// 保存价值
						k++;
					}
				}
			}
			sort(totalValue);
			k = 1;
			int maxValue = totalValue[0][2];// 第一个一定是最大值
			while (totalValue[k][2] == maxValue) {
				k++;
			}
			int r = (int) (Math.random() * k);// 从几个最大值中随机选一个
			int[] position = new int[2];
			position[0] = totalValue[r][0];
			position[1] = totalValue[r][1];

			return position;
		}

		int maxValue = -LARGE_NUMBER;
		int value = maxValue;
		int[] position = new int[2];
		int valuablePositions[][] = getTheMostValuablePositions();// 对空白点bvalue+wvalue+svalue获取最有价值的样本点
		for (int i = 0; i < valuablePositions.length; i++) {
			if (isWin(valuablePositions[i][0], valuablePositions[i][1], cb.computerColor)) {// 连五,则不再继续
				position[0] = valuablePositions[i][0];
				position[1] = valuablePositions[i][1];
				break;
			}

			if (cb.computerColor == 1 && isForbidden(valuablePositions[i][0], valuablePositions[i][1])) {// 禁手
				value = -LARGE_NUMBER;
			} else {
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = cb.computerColor;
				value = min(SEARCH_DEPTH, -LARGE_NUMBER, LARGE_NUMBER);
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
			}

			if (value > maxValue) {
				if (value - maxValue < 500) {// 局面相差不大可以增加随机性
					int r = (int) (Math.random() * 2);
					if (r == 1) {
						maxValue = value;
						position[0] = valuablePositions[i][0];
						position[1] = valuablePositions[i][1];
					}
				} else {
					maxValue = value;
					position[0] = valuablePositions[i][0];
					position[1] = valuablePositions[i][1];
				}
			}
		}
		return position;
	}

	// 最小值方法
	private int min(int depth, int alpha, int beta) {
		if (depth == 0) {// 搜索到最底层，直接返回当前估值
			return evaluateGame();// 计算棋型
		}
		renewBWvalue();// 刷新blackValue和whiteValue
		int value;
		int valuablePositions[][] = getTheMostValuablePositions();// 获得最有价值的若干空位
		for (int i = 0; i < valuablePositions.length; i++) {

			// 如果人下棋达到连五,则不必再搜索，返回一个很大的负值
			// i增加时这个地方wvalue和bvalue已经变化了
			if (cb.computerColor == 1 && isWin(valuablePositions[i][0], valuablePositions[i][1], 2)) {// 人是白棋
				// 该空位白棋能连五
				return -10 * FIVE;
			} else if (cb.computerColor == 2 && isWin(valuablePositions[i][0], valuablePositions[i][1], 1)) {// 人是黑棋
				// 该空位黑棋能连五
				return -10 * FIVE;
			}

			if (cb.computerColor == 2 && isForbidden(valuablePositions[i][0], valuablePositions[i][1])) {// 禁手
				value = LARGE_NUMBER;
			} else {
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = (cb.computerColor == 1 ? 2 : 1);// 颜色和计算机颜色相反
				value = max(depth - 1, alpha, beta);
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
				renewBWvalue();// 刷新blackValue和whiteValue
			}

			if (value < beta) {// beta保存当前最小估值
				if (beta - value < 500) {
					int r = (int) (Math.random() * 2);
					if (r == 1) {
						beta = value;
					}
				} else {
					beta = value;
				}
				if (alpha >= beta) {
					return alpha;
				}
			}
		}
		return beta;
	}

	// 最大值方法
	private int max(int depth, int alpha, int beta) {
		if (depth == 0) {
			return evaluateGame();
		}
		renewBWvalue();// 刷新blackValue和whiteValue
		int value;
		int valuablePositions[][] = getTheMostValuablePositions();
		for (int i = 0; i < valuablePositions.length; i++) {

			// 如果计算机下棋达到连五,则不必再搜索，返回一个很大的值
			if (cb.computerColor == 1 && isWin(valuablePositions[i][0], valuablePositions[i][1], cb.computerColor)) {// 计算机为黑棋
				return 10 * FIVE;
			} else if (cb.computerColor == 2
					&& isWin(valuablePositions[i][0], valuablePositions[i][1], cb.computerColor)) {// 计算机为白棋
				return 10 * FIVE;
			}

			if (cb.computerColor == 1 && isForbidden(valuablePositions[i][0], valuablePositions[i][1])) {// 禁手
				value = -LARGE_NUMBER;
			} else {
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = cb.computerColor;
				value = min(depth - 1, alpha, beta);
				cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
				renewBWvalue();// 刷新blackValue和whiteValue
			}

			if (value > alpha) {// alpha保存最大值
				if (value - alpha < 500) {
					int r = (int) (Math.random() * 2);
					if (r == 1) {
						alpha = value;
					}
				} else {
					alpha = value;
				}
				if (alpha >= beta) {
					return beta;
				}
			}
		}
		return alpha;
	}

	// 刷新blackValue和whiteValue
	private void renewBWvalue() {
		for (int k = 0; k <= ChessBoard.COLS; k++) {
			for (int j = 0; j <= ChessBoard.ROWS; j++) {
				blackValue[k][j] = 0;
				whiteValue[k][j] = 0;
				if (cb.boardStatus[k][j] == 0) {// 空位则估值
					for (int m = 1; m <= 4; m++) {
						blackValue[k][j] += evaluateValue(1, k, j, m);
						whiteValue[k][j] += evaluateValue(2, k, j, m);
					}
				}
			}
		}
	}
	
}
