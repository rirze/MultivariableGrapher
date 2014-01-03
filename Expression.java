public class Expression
{
	private String exp;

	public Expression(String s) throws IllegalArgumentException
	{
		exp = s;
		checkParen();
		exp = cleanUp(exp);
		if(!isValidExpression(exp))
			throw new IllegalArgumentException("invalid syntax: " + exp);
	}

	public Expression(String s, boolean shortCheck)
	{
		exp = s;
		checkParen();
		if(shortCheck)
		{
			exp = shortCleanUp(exp);
		}
		else
		{
			exp = cleanUp(exp);
			if(!isValidExpression(exp))
				throw new IllegalArgumentException("invalid syntax: " + exp);
		}
	}

	public String toString()
	{
		return exp;
	}

	private void checkParen() throws IllegalArgumentException
	{
		int paren = 0;
		for(int i = 0; i < exp.length(); i++)
		{
			if(exp.charAt(i) == '(')
				paren++;
			else if(exp.charAt(i) == ')')
			{
				paren--;
				if(paren < 0)
					throw new IllegalArgumentException("mismatched parenthases: " + exp);
			}
		}

		if(paren != 0)
			throw new IllegalArgumentException("mismatched parenthases: " + exp);
	}

	public static boolean isValidExpression(String str)
	{
		if(Regex.minExpression.matcher(str).matches())
			return true;
		else
		{
			for(int i = 0; i < str.length(); i++)
			{
				if(str.charAt(i) == '(')
				{
					int j = corParen(str, i);
					String subexp = shortCleanUp(str.substring(i + 1, j));
					if(!isValidExpression(subexp))
						return false;
				}
			}
			return Regex.expression.matcher(str).matches();
		}
	}

	private static String shortCleanUp(String s)
	{
		for(;;)
		{
			if(s.charAt(0) == '(' && corParen(s, 0) == s.length() - 1)
				s = s.substring(1, s.length() - 1);
			else if(s.charAt(0) == ' ')
				s = s.substring(1, s.length());
			else if(s.charAt(s.length() - 1) == ' ')
				s = s.substring(0, s.length() - 1);
			else
				break;
		}

		return s;
	}

	private String cleanUp(String s) throws IllegalArgumentException
	{
		char c = '\0';

		//remove unnecessary outer parenthases and outer spaces
		for(;;)
		{
			if(s.charAt(0) == '(' && corParen(s, 0) == s.length() - 1)
				s = s.substring(1, s.length() - 1);
			else if(s.charAt(0) == ' ')
				s = s.substring(1, s.length());
			else if(s.charAt(s.length() - 1) == ' ')
				s = s.substring(0, s.length() - 1);
			else
				break;
		}

		//remove multiple spaces
		for(int i = 0; i < s.length() - 1; i++)
		{
			if(s.charAt(i) == ' ' && s.charAt(i - 1) == ' ')
				s = remove(s, i);
		}

		//change all minuses to ~
		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == '-' && isMinus(s, i))
				s = replace(s, i, '~');
		}

		//change things like 3(2 - 3) to 3 * (2 - 3)
		for(int i = 0; i < s.length(); i++)
		{
			if(i != 0 && s.charAt(i) == '(')
			{
				if((""+s.charAt(i - 1)).matches("\\.|\\d") || s.charAt(i - 1) == ')')
					s = insert(s, '*', i);
			}
			else if(i != s.length() - 1 && s.charAt(i) == ')')
			{
				if((""+s.charAt(i + 1)).matches("\\.|\\d"))
					s = insert(s, '*', i + 1);
			}
		}

		//change -cos(x) to -1*cos(x)
		for(int i = 0; i < s.length(); i++)
		{
			if(i != 0 && (""+s.charAt(i)).matches("[a-zA-Z]"))
			{
				if(s.charAt(i - 1) == '-')
					s = s.substring(0, i - 1) + "-1*" + s.charAt(i) + s.substring(i + 1, s.length());
				else if(s.charAt(i - 1) == ' ' && s.charAt(i - 1) == '-')
					s = s.substring(0, i - 2) + "-1*" + s.charAt(i) + s.substring(i + 1, s.length());

			}

		}

		//change things like -(3 - 2) to -1 * (3 - 2)
		for(int i = 0; i < s.length(); i++)
		{
			if(i != 0 && s.charAt(i) == '(')
				if(s.charAt(i - 1) == '-')
					s = s.substring(0, i - 1) + "-1*" + s.substring(i, s.length());
		}

		//checks for spaces next to *, +, /, and ^
		for(int i = 0; i < s.length(); i++)
		{
			c = s.charAt(i);
			if(Regex.operator.matcher(""+c).matches())
			{
				if(i == 0)
				{
					throw new IllegalArgumentException("illegal operator placement: " + s);
				}
				else if(s.charAt(i - 1) != ' ')
				{
					s = insert(s, ' ', i);
					i++;
				}

				if(s.charAt(i + 1) != ' ')
				{
					s = insert(s, ' ', i + 1);
				}
			}
		}

		//replace 5 ~ - 6 with 5 ~ -6
		for(int i = 0; i < s.length(); i++)
			if(s.charAt(i) == '-')
				if(s.charAt(i + 1) == ' ')
					s = remove(s, i + 1);

		return s;
	}

	public static String insert(String s, char c, int place) //returns s with c as the placeth character
	{
		return s.substring(0, place) + c + s.substring(place, s.length());
	}

	public static String remove(String s, int place) //removes charAt(place)
	{
		return s.substring(0, place) + s.substring(place + 1, s.length());
	}

	public static String replace(String s, int place, char c)
	{
		return s.substring(0, place) + c + s.substring(place + 1, s.length());
	}

	public static int corParen(String s, int place) //returns index of corresponding parenthasis; -1 if there is none
	{
		int paren = 1;
		for(int i = place + 1; i < s.length(); i++)
		{
			if(s.charAt(i) == '(')
				paren++;
			else if(s.charAt(i) == ')')
			{
				paren--;
				if(paren == 0)
					return i;
			}
		}
		return -1;
	}

	private static boolean isMinus(String s, int place) //determines if the - at place is a negative, or a minus sign
	{
		if(s.charAt(place + 1) == '-')
			return true;
		else if(s.charAt(place + 1) == ' ' && s.charAt(place + 2) == '-')
			return true;
		if(place == 0) //-5 + 3
			return false;
		else if(place == 1) //5-5
		{
			return true;
		}
		else //5 -5, 55.5-5
		{
			char c;
			if((c = s.charAt(place - 1)) == ' ')
			{
				if(Regex.operator.matcher(""+s.charAt(place - 2)).matches())
					return false;
				if(s.charAt(place - 2) == '(')
					return false;
			}
			else if(s.charAt(place - 1) == 'E')
				return false;
			else
			{
				if(Regex.operator.matcher(""+c).matches())
					return false;
				if(c == '(')
					return false;
			}
			return true;
		}
	}

	public double evaluate()
	{
		if(Regex.number.matcher(exp).matches())
			return Double.parseDouble(exp);

		if(isUnaryOperation(exp))
		{
			double operand = (new Expression(getOperand(exp), true)).evaluate();
			String operator = getOperator(exp);
			switch(operator)
			{
				case "log":
					return Math.log(operand);
				case "sqrt":
					return Math.sqrt(operand);
				case "exp":
					return Math.exp(operand);
				case "abs":
					return Math.abs(operand);
				case "gamma":
					return gamma(operand);
				case "sin":
					return Math.sin(operand);
				case "cos":
					return Math.cos(operand);
				case "tan":
					return Math.tan(operand);
				case "sec":
					return 1/Math.cos(operand);
				case "csc":
					return 1/Math.sin(operand);
				case "cot":
					return 1/Math.tan(operand);
				case "arccos":
					return Math.acos(operand);
				case "arcsin":
					return Math.asin(operand);
				case "arctan":
					return Math.atan(operand);
				case "arcsec":
					return Math.acos(1/operand);
				case "arccsc":
					return Math.asin(1/operand);
				case "arccot":
					return Math.atan(1/operand);
				case "productlog":
					return productlog(operand);
				case "zeta":
					return zeta(operand);
			}

		}

		int place = getOuterOperator(exp);
		char operator = exp.charAt(place);
		double left = (new Expression(exp.substring(0, place), true)).evaluate();
		double right = (new Expression(exp.substring(place + 1, exp.length()), true)).evaluate();
		if(operator == '+')
			return left + right;
		else if(operator == '~')
			return left - right;
		else if(operator == '*')
			return left*right;
		else if(operator == '/')
			return left/right;
		else if(operator == '^')
			return Math.pow(left, right);
		else if(operator == '%')
			return left % right;

		throw new IllegalArgumentException("No suitible operator found");
	}

	private boolean isUnaryOperation(String s)
	{
		if(Regex.unaryOperation.matcher(s).matches())
		{
			int begin = s.indexOf('(');
			int end = corParen(s, begin);
			String stringWithoutInside = s.substring(0, begin) + s.substring(end + 1, s.length());
			if(Regex.unaryName.matcher(stringWithoutInside.trim()).matches())
				return true;
		}
		return false;
	}


	private static int getOuterOperator(String s)
	{
		int paren = 0;
		for(int i = s.length() - 1; i >= 0; i--)
		{
			if(s.charAt(i) == '(')
				paren++;
			else if(s.charAt(i) == ')')
				paren--;
			else if(paren == 0 && (s.charAt(i) == '+' || s.charAt(i) == '~'))
				return i;
		}
		paren = 0;
		for(int i = s.length() - 1; i >= 0; i--)
		{
			if(s.charAt(i) == '(')
				paren++;
			else if(s.charAt(i) == ')')
				paren--;
			else if(paren == 0 && (s.charAt(i) == '*' || s.charAt(i) == '/' || s.charAt(i) == '%'))
				return i;
		}
		paren = 0;
		for(int i = s.length() - 1; i >= 0; i--)
		{
			if(s.charAt(i) == '(')
				paren++;
			else if(s.charAt(i) == ')')
				paren--;
			else if(paren == 0 && s.charAt(i) == '^')
				return i;
		}
		throw new IllegalArgumentException("unable to evaluate expression: " + s);
	}

	private static String getOperand(String s) //gets operand of unary operation
	{
		int start = s.indexOf('(');
		return s.substring(start + 1, s.length() - 1);
	}

	private static String getOperator(String s) //gets operator of unary operation
	{
		return s.substring(0, s.indexOf('('));
	}

	public static double gamma(double n)
	{
		if((new Double(n)).equals(Double.NaN))
			return Double.NaN;
		if(n < 10)
			return gamma(n + 1)/n;
		else if(n < 0)
			return Math.PI / (Math.sin(Math.PI*n)*gamma(1 - n));

		double ans = Math.sqrt(2*Math.PI / n);
		double ans2 = 1/Math.E;
		ans2 *= n + 1/(12*n - 1/(10*n));
		return ans*Math.pow(ans2, n);
	}

	public static double productlog(double n) //lambert W function, inverse of x*e^x
	{
		if(n < -1/Math.E || (new Double(n)).equals(Double.NaN))
			return Double.NaN;
		double w1, w0 = 1, pow = Math.pow(Math.E, w0);
		w0 = w1 = w0 - (w0*pow - n)/(pow*(1 + w0) - ((w0 + 2)*(pow - n))/(2*w0 + 2));
		do
		{
			pow = Math.pow(Math.E, w0);
			w1 = w0;
			w0 -= (w0*pow - n) / (pow + w0*pow);
		}
		while(Math.abs(w0 - w1) >= 0.000001);
		return w0;
	}

	public static double eta(double z) //Diriclet eta function/''alternating zeta function''
	{                                  //used to calculate the Riemann zeta function for 0 < x
		if(z <= 0 || (new Double(z)).equals(Double.NaN))
			return Double.NaN;

		double z0 = 1 - 1/Math.pow(2, z), z1 = 0, addend;
		for(int i = 3; Math.abs(z1 - z0 + (addend = 0.5*Math.pow(i + 1, -z))) >= 0.000001; i+=2)
		{
			z1 = z0 - addend;
			z0 += Math.pow(i, -z) - Math.pow(i + 1, -z);
		}
		return z0;
	}

	public static double zeta(double z) //Riemann zeta function
	{
		if(z == 1.0 || (new Double(z)).equals(Double.NaN))
			return Double.NaN;
		else if(z == 0)
			return -0.5;

		if(z > 0)
			return 1/(1 - Math.pow(2, 1 - z)) * eta(z);
		else
			return Math.pow(2, z)*Math.pow(Math.PI, z - 1)*Math.sin(Math.PI*z/2)*gamma(1 - z)*zeta(1 - z);
	}
}