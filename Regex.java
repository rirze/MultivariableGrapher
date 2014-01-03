import java.util.regex.*;

public class Regex
{
	public static final Pattern number = Pattern.compile("(-?\\d+(\\.\\d*)?)(E-?\\d+)?");
	public static final Pattern operator = Pattern.compile("((\\+)|(~)|(/)|(\\*)|(\\^)|(%))");
	public static final Pattern unaryOperation = Pattern.compile("((productlog)|(zeta)|(log)|(sqrt)|(cos)|(sin)|(tan)|(sec)|(csc)|(csc)|(abs)|(arccos)|(arcsin)|(arctan)|(arcsec)|(arccsc)|(arccot)|(gamma)|(exp))\\(.*\\)");
	public static final Pattern unaryName = Pattern.compile("((productlog)|(zeta)|(log)|(sqrt)|(cos)|(sin)|(tan)|(sec)|(csc)|(csc)|(abs)|(arccos)|(arcsin)|(arctan)|(arcsec)|(arccsc)|(arccot)|(gamma)|(exp))");
	public static final Pattern unaryOperation2 = Pattern.compile("((productlog)|(zeta)|(log)|(sqrt)|(cos)|(sin)|(tan)|(sec)|(csc)|(csc)|(abs)|(arccos)|(arcsin)|(arctan)|(arcsec)|(arccsc)|(arccot)|(gamma)|(exp))\\(([^\\(\\)]*(\\(.*?\\))*[^\\(\\)]*)+\\)");
	public static final Pattern minExpression = Pattern.compile(number + "( " + operator + " " + number + ")*");
	private static final Pattern highExp = Pattern.compile("(("+ unaryName + "?\\(.*\\))|" + number + ")");
	public static final Pattern expression = Pattern.compile(highExp + "( " + operator + " " + highExp + ")*");

}