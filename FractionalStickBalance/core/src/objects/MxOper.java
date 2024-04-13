package objects;

public class MxOper {



	/**
	 * @param Matrix 1 with size: n*m
	 * @param Matrix 2 with size: n*m
	 * @return Addition of two matrixes with size: n*m
	 */
	public static double[][] addMx(double[][] mx1, double[][] mx2) {

		int row = mx1.length;
		int col = mx1[0].length;
		int row2 = mx2.length;
		int col2 = mx2[0].length;

		double[][] mx = new double[row][col];

		if (col == col2 & row == row2) {
			for (int j = 0; j < col; j++) {
				for (int i = 0; i < row; i++) {
					mx[i][j] = (mx1[i][j] + mx2[i][j]);

				}
			}
		} else {
			System.out.print("Matrix dimensions don't match");
		}

		return mx;
	}
	
	/**
	 * @param Matrix 1 with size: n*m
	 * @param Matrix 2 with size: n*m
	 * @return Addition of two matrixes with size: n*m
	 */
	public static double[] addVec(double[] mx1, double[] mx2) {

		int row = mx1.length;
		int row2 = mx2.length;

		double[] mx = new double[row];

		if (row == row2) {
				for (int i = 0; i < row; i++) {
					mx[i] = (mx1[i]+ mx2[i]);

				}
			
		} else {
			System.out.print("Matrix dimensions don't match");
		}

		return mx;
	}

	/**
	 * @param n
	 * @param m
	 * @return Unit matrix with size: n*m
	 */
	public static double[][] eyeMx(int row, int col) {
		

		double[][] mx = new double[row][col];

		for (int j = 0; j < col; j++) {
			for (int i = 0; i < row; i++) {
				if (i == j) {
					mx[i][j] = 1;
				} else {
					mx[i][j] = 0;
				}
			}
		}

		return mx;
	}

	/**
	 * @param Matrix 1 with size: n*m
	 * @param Matrix 2 with size: m*k
	 * @return Multiplication of two matrixes with size: n*k
	 */
	public static double[][] multMx(double[][] mx1, double[][] mx2) {

		int row = mx1.length;
		int col = mx1[0].length;
		int row2 = mx2.length;
		int col2 = mx2[0].length;

		double[][] mx = new double[row][col2];

		if (col == row2) {
			for (int j = 0; j < col; j++) {
				for (int i = 0; i < row; i++) {
					for (int k = 0; k < col2; k++) {
						mx[i][k] += (mx1[i][j] * mx2[j][k]);
					}
				}
			}
		} else {
			System.out.print("Matrix dimensions don't match");
		}

		return mx;
	}
	
	/**
	 * @param Matrix 1 with size: 1
	 * @param Matrix 2 with size: m
	 * @return Multiplication of two matrixes with size: m
	 */
	public static double[] multVecwS(double scalar, double[] mx1) {

		int row = mx1.length;

		double[] mx = new double[row];

				for (int i = 0; i < row; i++) {
						mx[i] = (scalar*mx1[i]);
			}

		return mx;
	}
	
	/**
	 * @param Matrix 1 with size: 1
	 * @param Matrix 2 with size: m*k
	 * @return Multiplication of two matrixes with size: m*k
	 */
	public static double[][] multMxwS(double scalar, double[][] mx1) {

		int row = mx1.length;
		int col = mx1[0].length;

		double[][] mx = new double[row][col];

			for (int j = 0; j < col; j++) {
				for (int i = 0; i < row; i++) {
					
						mx[i][j] = (scalar*mx1[i][j]);
					
				}
			
		}

		return mx;
	}


	/**
	 * @param n
	 * @param m
	 * @return Zero matrix with size: n*m
	 */
	public static double[][] zeroMx(int row, int col) {

		double[][] mx = new double[row][col];

		for (int j = 0; j < col; j++) {
			for (int i = 0; i < row; i++) {
				mx[i][j] = 0;
			}
		}

		return mx;
	}

	/**
	 * @param Matrix
	 * @return Matrix transpose
	 */
	public static double[][] transMx(double[][] mx) {

		double[][] tmx = new double[mx[0].length][mx.length];

		for (int i = 0; i < mx.length; i++) {
			for (int j = 0; j < mx[0].length; j++) {
				tmx[j][i] = mx[i][j];
			}
		}
		return tmx;
	}

	/**
	 * @param Matrix
	 * @return Matrix inverse
	 */
	public static double[][] invMx(double[][] mx) {

		double[][] imx = new double[mx[0].length][mx.length];

		if (mx.length == 2) {

			double det = mx[0][0] * mx[1][1] - mx[0][1] * mx[1][0];

			imx[0][0] = mx[1][1] / det;
			imx[0][1] = -mx[0][1] / det;
			imx[1][0] = -mx[1][0] / det;
			imx[1][1] = mx[0][0] / det;

		} else {

		}

		return imx;

	}

	
	/**
	 * @param Matrix1 Matrix2
	 * @return Matrix subtract
	 */
	public static double[][] subMx(double[][] mx1, double[][] mx2) {

		int row = mx1.length;
		int col = mx1[0].length;
		int row2 = mx2.length;
		int col2 = mx2[0].length;

		double[][] mx = new double[row][col];

		if (col == col2 & row == row2) {
			for (int j = 0; j < col; j++) {
				for (int i = 0; i < row; i++) {
					mx[i][j] = (mx1[i][j] - mx2[i][j]);

				}
			}
		} else {
			System.out.print("Matrix dimensions don't match");
		}

		return mx;
	}

	/**
	 * @param Matrix
	 */
	public static void printMx(double[][] mx) {

		for (int i = 0; i < mx.length; i++) {
			System.out.print("[");
			for (int j = 0; j < mx[0].length; j++) {
				System.out.print(
						String.format("%f", mx[i][j])+" ");
			}
			System.out.print("]\n");
		}
	}
}
