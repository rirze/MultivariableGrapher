public class Viewer //can be three-dimensional only
{
	private double[] loc = new double[3]; //x, y, z coordinates
	private double fov; //field of view
	private double width, height; //width and height of the screen
	private double[][] basis = new double[3][]; //orthonormal basis with which up, right, and foward are determined

	public Viewer(double x, double y, double z, double f, double w, double h)
	{
		if(w <= 0 || h <= 0)
			throw new IllegalArgumentException("Width and height must be positive");
		else if(f <= 0 || f >= 180)
			throw new IllegalArgumentException("Field of view must be between 0 and 180");

		loc[0] = x;
		loc[1] = y;
		loc[2] = z;

		fov = (double)Math.PI*f/180;
		width = w;
		height = h;

		getBasis();
	}

	public void setLocation(double x, double y, double z)
	{
		loc[0] = x;
		loc[1] = y;
		loc[2] = z;
		getBasis();
	}

	public void setHeight(double h)
	{
		height = h;
	}

	public void setWidth(double w)
	{
		width = w;
	}

	public void setfov(double d)
	{
		fov = d;
	}

	public String toString()
	{
		return "viewer at " + loc[0] + ", " + loc[1] + ", " + loc[2] + ", fov = " + fov + ", " + width + " by " + height;
	}

	private void getBasis() //makes orthonormal x, y, and z basis such that y points towards 0, ans z is coplanar to z-axis
	{
		double[] y = LinearAlgebra.normalize(LinearAlgebra.product(-1, loc));
		double[] zaxis = {0, 0, 1};
		double[] x = LinearAlgebra.normalize(LinearAlgebra.crossproduct(y, zaxis));
		double[] z = LinearAlgebra.normalize(LinearAlgebra.crossproduct(x, y));

		basis[0] = x;
		basis[1] = y;
		basis[2] = z;
	}

	private double[] getRelativeLocation(double[] p) 	//find p in terms of the orthonormal basis B with this viewer v at the origin,
	{												//this is equivalent to solving the system Bx = p - v for x
		if(p.length != 3)
			throw new IllegalArgumentException("Dimension mismatch");
		double[] b = LinearAlgebra.difference(p, loc);
		double[] ans = new double[3];

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				ans[i] += basis[i][j]*b[j];
			}
		}

		return ans;
	}

	private double[] perspectivePoint(double[] p)
	{
		if(p.length != 3)
			throw new IllegalArgumentException("Dimension mismatch");
		double[] n = getRelativeLocation(p);
		double[] ans = new double[2];
		ans[0] = getPerspective(n[0], width, n[1]);
		ans[1] = getPerspective(n[2], height, n[1]);
		return ans;
	}

	public int[] pointOnScreen(double[] p, int screenWidth, int screenHeight)
	{
		double[] n = perspectivePoint(p);
		int[] ans = new int[2];
		if(isValid(n[0]) && isValid(n[1]))
		{
			ans[0] = (int)(screenWidth/2. + (screenWidth/width)*n[0]);
			ans[1] = (int)(screenHeight/2. - (screenHeight/height)*n[1]);
			return ans;
		}
		else
		{
			throw new UnsupportedOperationException("One or more coordinates undefined: " + n[0] + ", " + n[1]);
		}
	}

	private double getPerspective(double l, double w, double dis)
	{
		return (l*w)/((double)Math.tan(fov/2)*dis + w);
	}

	private boolean isValid(double f)
	{
		Float g = new Float(f);
		if(g.compareTo(Float.NaN) == 0)
			return false;
		else if(g.compareTo(Float.POSITIVE_INFINITY) == 0)
			return false;
		else if(g.compareTo(Float.NEGATIVE_INFINITY) == 0)
			return false;
		return true;
	}
}