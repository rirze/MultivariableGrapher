public class Calculus
{
	public static final double diffacc = 0.001;
	public static final int derivplaces = 3;
	public static final double intacc = 0.0001;
	public static final int intplaces = 4;

	private Calculus() {}

	public static double threedderivative(Function f, String s) // "x, y, xdirection, ydirection"
	{
		double[] args = parseArgs(s, 4);

		return derivative(f, args[0], args[1], args[2], args[3]);
	}

	public static double volumeintegral(Function f, String s)
	{
		double[] args = parseArgs(s, 4);

		return integral(f, args[0], args[1], args[2], args[3]);
	}

	private static double[] parseArgs(String s, int qty)
	{
		double[] ans = new double[qty];
		if(s.indexOf(',') == -1)
		{
			for(int i = 0; i < qty - 1; i++)
			{
				ans[i] = Double.parseDouble(s.substring(0, s.indexOf(' ')));
				s = s.substring(s.indexOf(' '), s.length()).trim();
			}
			ans[qty - 1] = Double.parseDouble(s.trim());
		}
		else
		{
			for(int i = 0; i < qty - 1; i++)
			{
				ans[i] = Double.parseDouble(s.substring(0, s.indexOf(',')));
				s = s.substring(s.indexOf(',')+1, s.length()).trim();
			}
			ans[qty - 1] = Double.parseDouble(s.trim());
		}
		return ans;
	}

	public static double derivative(Function f, double p, double direction)
	{
		if(f.vars() != 1)
			throw new IllegalArgumentException("Invalid derivative parameters");

		if(direction >= 0)
			direction = 1;
		else
			direction = -1;

		double deriv0 = 0, deriv1 = 1;
		double point = f.evalAt(p);

		while(Math.abs(deriv0 - deriv1) > diffacc)
		{
			deriv0 = deriv1;
			direction /= 8f;
			deriv1 = (f.evalAt(p + direction) - point)/(direction);
		}

		return deriv1;
	}

	public static double derivative(Function f, double x, double y, double xd, double yd)
	{
		if(f.vars() != 2)
			throw new IllegalArgumentException("Invalid derivative parameters");

		double abs = Math.sqrt(xd*xd+yd*yd);
		xd /= abs*100;
		yd /= abs*100;
		abs = 0.01;

		double deriv0 = 0, deriv1 = 1;
		double point = f.evalAt(x, y);

		while(Math.abs(deriv0 - deriv1) > diffacc)
		{
			deriv0 = deriv1;
			xd /= 8;
			yd /= 8;
			abs /= 8;
			deriv1 = (f.evalAt(x + xd, y + yd) - point)/(abs);
		}

		return deriv1;
	}

	public static double derivative(Function f, double[] p, double[] direction)
	{
		if(p.length != f.vars() || direction.length != f.vars())
			throw new IllegalArgumentException("Invalid derivative parameters");

		double[] d = LinearAlgebra.normalize(direction);
		double deriv0 = 0, deriv1 = 1;
		double[] p1;
		double point = f.evalAt(p);

		for(p1 = LinearAlgebra.sum(p, d); Math.abs(deriv0 - deriv1) > diffacc;)
		{
			deriv0 = deriv1;
			d = LinearAlgebra.product(0.125f, d);
			p1 = LinearAlgebra.sum(p, d);
			deriv1 = (f.evalAt(p1) - point)/LinearAlgebra.length(d);
		}
		return deriv1;
	}

	public static double integral(Function f, double l, double h)
	{
		if(f.vars() != 1)
			throw new IllegalArgumentException("Invalid integral parameters");

		int its = 2*(int)(h - l);
		double int0 = 0, int1 = 1;

		double fa = f.evalAt(l);

		while(true)
		{
			int0 = int1;

			double[] xvals = new double[its + 1];
			double[] yvals = new double[its + 1];

			double width = (h - l)/its;

			xvals[0] = l;
			for(int i = 1; i <= its; i++)
				xvals[i] = xvals[i - 1] + width;
			yvals[0] = fa;
			for(int i = 1; i <= its; i++)
				yvals[i] = f.evalAt(xvals[i]);

			int1 = 0;
			for(int i = 0; i < its; i++)
				int1 += ((xvals[i+1]-xvals[i])/6)*(yvals[i] + 4*f.evalAt((xvals[i]+xvals[i+1])/2) + yvals[i + 1]);

			if(Math.abs(int1 - int0) <= intacc)
				return int1;

			its*=2;
		}
	}

	public static double integral(Function f, double xl, double xh, double yl, double yh)
	{
		if(f.vars() != 2)
			throw new IllegalArgumentException("Invalid integral parameters");

		double a, b, c, d;
		double i0, i1 = 1;
		int its = (int)Math.sqrt((xh-xl)*(yh-yl));
		double xw = (xh-xl)/its;
		double yw = (yh-yl)/its;
		double sum;

		do
		{
			i0 = i1;
			sum = 0;
			xw = (xh-xl)/its;
			yw = (yh-yl)/its;

			for(a = xl, b = xl+xw; a < xh; a = b, b += xw)
				for(c = yl, d = yl+yw; c < yh; c = d, d += yw)
				{
					double xm = (a+b)/2;
					double ym = (c+d)/2;
					double addend = 0;
					addend += f.evalAt(a, c) + f.evalAt(a, d) + f.evalAt(b, d) + f.evalAt(b, c);
					addend += 4*(f.evalAt(xm, c) + f.evalAt(xm, d) + f.evalAt(a, ym) + f.evalAt(b, ym));
					addend += 16*f.evalAt(xm, ym);
					addend *= (1.0/36.0)*(b-a)*(d-c);
					sum += addend;
				}

			i1 = sum;
			its *= 2;
		}
		while(Math.abs(i1 - i0) > intacc);

		return i1;
	}

	public static double surfacearea(Function f, double xl, double xh, double yl, double yh)
	{
		if(f.vars() != 2)
			throw new IllegalArgumentException("Invalid integral parameters");

		double a, b, c, d;
		double mx, my, M, A, B, C, D;
		double i0, i1 = 1;
		double its = 1;
		double xw = (xh-xl)/its;
		double yw = (yh-yl)/its;
		double sum;

		do
		{
			i0 = i1;
			sum = 0;
			xw = (xh-xl)/its;
			yw = (yh-yl)/its;

			for(a = xl, b = xl+xw; a < xh; a = b, b += xw)
			{
				for(c = yl, d = yl+yw; c < yh; c = d, d += yw)
				{
					mx = 0.5*(a+b);
					my = 0.5*(c+d);
					M = f.evalAt(mx, my);
					A = f.evalAt(a, c);
					B = f.evalAt(b, c);
					C = f.evalAt(b, d);
					D = f.evalAt(a, d);

					sum += trianglearea(a, mx, b, c, my, c, A, M, B);
					sum += trianglearea(b, mx, b, c, my, d, B, M, C);
					sum += trianglearea(b, mx, a, d, my, d, C, M, D);
					sum += trianglearea(a, mx, a, d, my, c, D, M, A);
				}
			}

			i1 = sum;
			System.out.println(its + ", " + i1);
			its *= 2;
		}
		while(Math.abs(i1 - i0) > intacc);

		return i1;
	}

	private static double trianglearea(double ax, double bx, double cx, double ay, double by, double cy, double az, double bz, double cz)
	{
		double side1 = Math.sqrt((ax - bx)*(ax - bx) + (ay - by)*(ay - by) + (az - bz)*(az - bz));
		double side2 = Math.sqrt((cx - bx)*(cx - bx) + (cy - by)*(cy - by) + (cz - bz)*(cz - bz));
		double side3 = Math.sqrt((cx - ax)*(cx - ax) + (cy - ay)*(cy - ay) + (cz - az)*(cz - az));
		double s = 0.5*(side1 + side2 + side3);

		return Math.sqrt(s*(s - side1)*(s - side2)*(s - side3));
	}
}