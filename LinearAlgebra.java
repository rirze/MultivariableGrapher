public class LinearAlgebra
{
	private LinearAlgebra() {}

	public static String vectorToString(double[] u)
	{
		String s = "{";
		for(int i = 0; i < u.length; s += u[i] + ", ", i++);
		return s.substring(0, s.length() - 2) + "}";
	}

	public static String matrixToString(double[][] A)
	{
		String s = "[";
		for(int row = 0; row < A.length; row++)
		{
			for(int col = 0; col < A[0].length; col++)
			{
				s += A[row][col] + ", ";
			}
			s = s.substring(0, s.length() - 2) + "\n";
		}
		return s.substring(0, s.length() - 1) + "]";
	}

	public static double[] sum(double[] u, double[] v)
	{
		if(u.length != v.length)
			throw new IllegalArgumentException("Mismatched dimensions");
		double[] x = new double[u.length];
		for(int i = 0; i < x.length; i++)
			x[i] = u[i] + v[i];
		return x;
	}

	public static double[] difference(double[] u, double[] v)
	{
		if(u.length != v.length)
			throw new IllegalArgumentException("Mismatched dimensions");
		double[] x = new double[u.length];
		for(int i = 0; i < x.length; i++)
			x[i] = u[i] - v[i];
		return x;
	}

	public static double dotproduct(double[] u, double[] v)
	{
		if(u.length != v.length)
			throw new IllegalArgumentException("Dimension mismatch");

		double sum = 0;
		for(int i = 0; i < u.length; sum += u[i]*v[i], i++);
		return sum;
	}

	public static double[] crossproduct(double[] u, double[] v)
	{
		double i = u[1]*v[2] - u[2]*v[1];
		double j = u[2]*v[0] - u[0]*v[2];
		double k = u[0]*v[1] - u[1]*v[0];
		double[] ans = {i, j, k};
		return ans;
	}

	public static double[][] product(double[][] A, double[][] B)
	{
		if(A[0].length != B.length)
			throw new IllegalArgumentException("Dimension mismatch");

		double[][] ans = new double[A.length][B[0].length];

		for(int col = 0; col < ans[0].length; col++)
		{
			double[] column = new double[B.length];
			for(int i = 0; i < column.length; i++)
				column[i] = B[i][col];
			for(int row = 0; row < ans.length; row++)
			{
				ans[row][col] = dotproduct(A[row], column);
			}
		}

		return ans;
	}

	public static double[] product(double n, double[] u)
	{
		double[] ans = new double[u.length];
		for(int i = 0; i < ans.length; i++)
			ans[i] = n*u[i];
		return ans;
	}

	public static double length(double[] u)
	{
		double sum = 0;
		for(int i = 0; i < u.length; i++)
			sum += u[i]*u[i];
		return Math.sqrt(sum);
	}

	public static double[] normalize(double[] u)
	{
		return product(1/length(u), u);
	}
}