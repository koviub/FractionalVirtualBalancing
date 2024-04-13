package objects;

public class Binomial {


	public static double[] binomialcoeff(double alpha) {

		double[] coeff = new double[30000];

		coeff[0]=1;
		
		for (int k = 1; k < 30000; k++) {
			coeff[k] = (1-(alpha+1)/k)*coeff[k-1];
		}
		
		return coeff;
	}
}