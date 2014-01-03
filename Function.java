

public class Function
{
	private String exp;
	private String input;
	private char[] vars;

	private static String[] constants = {"e", "pi", "eulergamma", "phi", "basel", "apery"};
	private static String[] constvals = {"2.718281828459045", "3.141592653589793", "0.577215664901532",
										"1.618033988749894", "1.644934066848226", "1.202056903159594"};

	public Function(String s, char x)
	{
		exp = s;
		input = s;
		vars = new char[1];
		vars[0] = x;
		cleanUp();
	}

	public Function(String s, char x, char y)
	{
		exp = s;
		input = s;
		vars = new char[2];
		vars[0] = x;
		vars[1] = y;
		cleanUp();
	}

	public Function(String s, char[] vs)
	{
		exp = s;
		input = s;
		vars = new char[vs.length];
		for(int i = 0; i < vars.length; vars[i] = vs[i++]);
		cleanUp();
	}

	public String toString()
	{
		return input;
	}

	public String cleanedString()
	{
		return exp;
	}

	public int vars()
	{
		return vars.length;
	}

	private void cleanUp()
	{
		char c = '\0';

		//remove unnecessary outer parenthases and outer spaces
		for(;;)
		{
			if(exp.charAt(0) == '(' && Expression.corParen(exp, 0) == exp.length() - 1)
				exp = exp.substring(1, exp.length() - 1);
			else if(exp.charAt(0) == ' ')
				exp = exp.substring(1, exp.length());
			else if(exp.charAt(exp.length() - 1) == ' ')
				exp = exp.substring(0, exp.length() - 1);
			else
				break;
		}

		//remove multiple spaces
		for(int i = 0; i < exp.length() - 1; i++)
		{
			if(exp.charAt(i) == ' ' && exp.charAt(i - 1) == ' ')
				exp = Expression.remove(exp, i);
		}

		//change pi to 3.14159....
		for(int i = 0; i < constants.length; i++)
		{
			int place;
			if((place = exp.indexOf(constants[i])) != -1 && isConstant(exp, place) == i)
			{
				if(place > 0 && (""+exp.charAt(place - 1)).matches("( )|(\\.)|(\\d)"))
				{
					exp = exp.substring(0, place) + '*' + exp.substring(place, exp.length());
					place++;
				}
				exp = exp.substring(0, place) + constvals[i] + exp.substring(place + constants[i].length(), exp.length());
				i--;
			}
		}

		//change 3x to 3*x and x3 to x^3
		for(int i = 0; i < exp.length(); i++)
		{
			if(isVar(exp, i))
			{
				if(i != 0)
				{
					if(isVar(exp, i - 1))
						exp = Expression.insert(exp, '*', i);
					else if((""+exp.charAt(i - 1)).matches("(\\d)|(\\.)"))
						exp = Expression.insert(exp, '*', i);
					else if(exp.charAt(i - 1) == ')')
						exp = Expression.insert(exp, '*', i);
					else if(exp.charAt(i - 1) == ' ')
						if((""+exp.charAt(i - 2)).matches("(\\d)|(\\.)"))
							exp = Expression.insert(exp, '*', i);
				}
				if(isVar(exp, i) && i != exp.length() - 1)
				{
					if(isVar(exp, i + 1))
						exp = Expression.insert(exp, '*', i + 1);
					else if((""+exp.charAt(i + 1)).matches("(\\d)|(\\-)|(\\.)") && (exp.charAt(i + 1) != '-' || !isMinus(exp, i + 1)))
						exp = Expression.insert(exp, '^', i + 1);
					else if(exp.charAt(i + 1) == '(')
						exp = Expression.insert(exp, '*', i + 1);
					else if(exp.charAt(i + 1) == ' ')
						if((""+exp.charAt(i + 2)).matches("(\\d)|(\\-)|(\\.)") && (exp.charAt(i + 2) != '-' || !isMinus(exp, i + 2)))
							exp = Expression.insert(exp, '^', i + 1);
				}
			}
		}

		//change all minuses to ~
		for(int i = 0; i < exp.length(); i++)
		{
			if(exp.charAt(i) == '-' && isMinus(exp, i))
				exp = Expression.replace(exp, i, '~');
		}

		//change things like 3(2 - 3) to 3*(2 - 3)
		for(int i = 0; i < exp.length(); i++)
		{
			if(i != 0 && exp.charAt(i) == '(')
			{
				if((""+exp.charAt(i - 1)).matches("\\.|\\d") || exp.charAt(i - 1) == ')')
					exp = Expression.insert(exp, '*', i);
			}
			else if(i != exp.length() - 1 && exp.charAt(i) == ')')
			{
				if((""+exp.charAt(i + 1)).matches("\\.|\\d"))
					exp = Expression.insert(exp, '*', i + 1);
			}
		}

		//change 2^x to 2^(x) or 2^-x to 2^(-x)
		for(int i = 1; i < exp.length(); i++)
		{
			if(isVar(exp, i))
			{
				if(exp.charAt(i - 1) == '^' || (exp.charAt(i - 1) == ' ' && exp.charAt(i - 1) == '^'))
				{
					int end;
					for(end = i; (end < exp.length()) && !Regex.operator.matcher(""+exp.charAt(end)).matches(); end++);
					exp = exp.substring(0, i) + "(" + exp.substring(i, end) + ")" + exp.substring(end, exp.length());
				}
			}
		}

		//change -x to -1*x
		for(int i = 0; i < exp.length(); i++)
		{
			if(i != 0 && isVar(exp, i))
				if(exp.charAt(i - 1) == '-')// && !isMinus(exp, i - 1))
					exp = exp.substring(0, i - 1) + "-1*" + exp.charAt(i) + exp.substring(i + 1, exp.length());
				else if(exp.charAt(i - 1) == ' ' && exp.charAt(i - 1) == '-')
					exp = exp.substring(0, i - 2) + "1*" + exp.charAt(i) + exp.substring(i + 1, exp.length());
		}

		//change -cos(x) to -1*cos(x)
		for(int i = 0; i < exp.length(); i++)
		{
			if(i != 0 && (""+exp.charAt(i)).matches("[a-zA-Z]"))
			{
				if(exp.charAt(i - 1) == '-')
					exp = exp.substring(0, i - 1) + "-1*" + exp.charAt(i) + exp.substring(i + 1, exp.length());
				else if(exp.charAt(i - 1) == ' ' && exp.charAt(i - 1) == '-')
					exp = exp.substring(0, i - 2) + "-1*" + exp.charAt(i) + exp.substring(i + 1, exp.length());

			}

		}

		//change things like -(3 - 2) to -1 * (3 - 2)
		for(int i = 0; i < exp.length(); i++)
		{
			if(i != 0 && exp.charAt(i) == '(')
				if(exp.charAt(i - 1) == '-')
					exp = exp.substring(0, i - 1) + "-1*" + exp.substring(i, exp.length());
		}

		//checks for spaces next to *, +, /, and ^
		for(int i = 0; i < exp.length(); i++)
		{
			c = exp.charAt(i);
			if(Regex.operator.matcher(""+c).matches())
			{
				if(i == 0)
				{
					throw new IllegalArgumentException("illegal operator placement: " + exp);
				}
				else if(exp.charAt(i - 1) != ' ')
				{
					exp = Expression.insert(exp, ' ', i);
					i++;
				}

				if(exp.charAt(i + 1) != ' ')
				{
					exp = Expression.insert(exp, ' ', i + 1);
				}
			}
		}

		//replace 5 ~ - 6 with 5 ~ -6
		for(int i = 0; i < exp.length(); i++)
			if(exp.charAt(i) == '-')
				if(exp.charAt(i + 1) == ' ')
					exp = Expression.remove(exp, i + 1);
	}

	private boolean isVar(String s, int place)
	{
		for(int i = 0; i < vars.length; i++)
			if(s.charAt(place) == vars[i])
			{
				if(!Regex.unaryName.matcher(biggestStringAt(s, place)).matches())
					return true;
			}
		return false;
	}
//
//	private boolean isInOperation(String s, int place)
//	{
//		if(Regex.unaryName.matcher(biggestStringAt(s, place)).matches())
//			return true;
//		return false;
//	}

	private int isConstant(String s, int place)
	{
		String str = biggestStringAt(s, place);
		for(int i = 0; i < constants.length; i++)
			if(str.equals(constants[i]))
				return i;
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
			else if(c == 'E')
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

	private static String biggestStringAt(String s, int place) //returns biggest string about place
	{
		if(!(""+s.charAt(place)).matches("[a-zA-Z]"))
			return "";
		int beginning, end;

		for(beginning = place; (beginning >= 0) && (""+s.charAt(beginning)).matches("[a-zA-Z]"); beginning--);
		for(end = place; (end < s.length()) && (""+s.charAt(end)).matches("[a-zA-Z]"); end++);
		return s.substring(beginning + 1, (end == s.length() + 1) ? s.length() : end);
	}

	public double evalAt(double x)
	{
		if(vars.length != 1)
			throw new IllegalArgumentException("parameter amount mismatch: " + vars.length + " expected, 1 entered");

		String expression = exp;

		for(int i = 0; i < expression.length(); i++)
			if(isVar(expression, i))
				expression = replace(expression, i, Double.toString(x));

		return (new Expression(expression, true)).evaluate();
	}

	public double evalAt(double x, double y)
	{
		if(vars.length != 2)
			throw new IllegalArgumentException("parameter amount mismatch: " + vars.length + " expected, 2 entered");

		String expression = exp;

		for(int i = 0; i < expression.length(); i++)
			if(isVar(expression, i))
			{
				if(expression.charAt(i) == vars[0])
					expression = replace(expression, i, Double.toString(x));
				else
					expression = replace(expression, i, Double.toString(y));
			}

		return (new Expression(expression, true)).evaluate();
	}

	public double evalAt(double[] args)
	{
		if(vars.length != args.length)
			throw new IllegalArgumentException("parameter amount mismatch: " + vars.length + " expected, " + args.length + " entered");

		String expression = exp;

		for(int i = 0; i < expression.length(); i++)
		{
			if(isVar(expression, i))
				for(int j = 0; j < args.length; j++)
				{
					if(expression.charAt(i) == vars[j])
						expression = replace(expression, i, Double.toString(args[j]));
				}
		}

		return (new Expression(expression, true)).evaluate();
	}

	private String replace(String s, int place, String str)
	{
		return s.substring(0, place) + str + s.substring(place + 1, s.length());
	}
}