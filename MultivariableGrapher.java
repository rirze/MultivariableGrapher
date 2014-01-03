import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

/*THINGS TO FIX:
 *function input parsing?
 *cos(x)sin(y)*/

public class MultivariableGrapher implements ActionListener, KeyListener
{
    MyDrawPanel drawPanel;
    public static int WIDTH = 600;
    public static int HEIGHT = 600;
    public static final int totalwidth = 820;
    public static final int totalheight = 640;
    public static final int[] topleft = {20, 20};
    public static int fps = 30;
    public static double fov = 90;
    public static Viewer viewer = new Viewer(0, 0, 0, fov, 10, 10.0*HEIGHT/WIDTH);
    public static double graphRange = 5;
    public static double graphzrange = 10;

    public static int arraysize = 51; //should be odd
    public static double[] xcoordinates = new double[arraysize];
    public static double[] ycoordinates = new double[arraysize];
    public static double[][] functionvals = new double[arraysize][arraysize];
    public static Function function;
    public static double theta = Math.PI / 3, phi = 9*Math.PI / 28, rho = Math.sqrt(4*graphRange*graphRange + graphzrange*graphzrange), t = 0;
    public static boolean rotate = false;
    public static boolean showFunction = true;
    public static boolean isCleared = true;

	public static Color functionColor = Color.WHITE;
	public static Color functionBGColor = Color.BLACK;
	public static Color frameBGColor = new Color(0.25f, 0.25f, 0.25f);
	public static Color buttonColor = Color.WHITE;
	public static Color axesColor = new Color(0.5f, 1f, 0.5f);
	public static Color stringColor = new Color(0.5f, 0.5f, 1f);

	public static boolean isClicked = false;
	public static boolean resizeOn = false;
	public static int dragx = 0, dragy = 0;

	public static final int buttonheight = 40;
	public static final int buttonwidth = 100;
	public static final int buttonxloc = topleft[0] + WIDTH + 50;
	public static final int buttonyloc = 100;
	public static final int spacing = 50;
    public static Button funinput = new Button("Function Input", buttonColor, buttonxloc, buttonyloc, buttonwidth, buttonheight);
    public static Button calculus = new Button("Calculus", buttonColor, buttonxloc, buttonyloc+buttonheight+spacing, buttonwidth, buttonheight);
    public static Button settings = new Button("Settings", buttonColor, buttonxloc, buttonyloc+2*(buttonheight+spacing), buttonwidth, buttonheight);
    public static Button help = new Button("Help", buttonColor, buttonxloc, buttonyloc+3*(buttonheight+spacing), buttonwidth, buttonheight);

    public static void main(String[] args)
    {
    	String r = "((productlog)|(zeta)|(log)|(sqrt)|(cos)|(sin)|(tan)|(sec)|(csc)|(csc)|(abs)|(arccos)|(arcsin)|(arctan)|(arcsec)|(arccsc)|(arccot)|(gamma)|(exp))\\(([^\\(\\)]*(\\(.*?\\))*[^\\(\\)]*)+\\)";
    	String s = "cos(3 * (4 + x))";
    	System.out.println(s.matches(r));

    	MultivariableGrapher gui = new MultivariableGrapher();
		gui.go();
    }

    public static void fillVals()
    {
		for(int i = 0; i < arraysize; i++)
		{
			double x = (graphRange/(0.5*(arraysize - 1)))*i - graphRange;
			xcoordinates[i] = x;
			for(int j = 0; j < arraysize; j++)
			{
				double y = (graphRange/(0.5*(arraysize - 1)))*j - graphRange;
				ycoordinates[j] = y;

				functionvals[i][j] = function.evalAt(x, y);
			}
		}
    }

    public void go()
    {
		JFrame frame = new JFrame("Multivariable Function Grapher");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawPanel = new MyDrawPanel();
		frame.getContentPane().add(drawPanel);
		frame.setSize(totalwidth+13, totalheight+35);
    	frame.setVisible(true);
    	frame.addKeyListener(this);
    	frame.addMouseListener(new mousePressHandler());
    	frame.addMouseWheelListener(new mouseWheelHandler());
    	frame.addMouseMotionListener(new mouseMotionHandler());

    	double x, y, z;

    	while(true)
    	{
    		try
    		{
    			Thread.currentThread();
				Thread.sleep(1000/fps);
    		}
    		catch(InterruptedException ie)
    		{

    		}

			if(rotate)
			{
				t+=1.0/(7*fps);
				t %= 2*Math.PI;
				phi = 5*t;
			}

			x = (double)(rho*Math.sin(theta)*Math.cos(phi));
			y = (double)(rho*Math.sin(theta)*Math.sin(phi));
			z = (double)(rho*Math.cos(theta));

			MultivariableGrapher.viewer.setLocation(x, y, z);

			drawPanel.repaint();
    	}
    }

    public void actionPerformed(ActionEvent e)
    {

    }

    public void keyPressed(KeyEvent k)
    {
		int key = k.getKeyCode();
		if(key == KeyEvent.VK_DOWN)
			theta += (theta + 0.05 > Math.PI) ? 0 : 0.05;
		else if(key == KeyEvent.VK_UP)
			theta -= (theta - 0.05 < 0) ? 0 : 0.05;
		else if(key == KeyEvent.VK_LEFT)
			phi = (phi - 0.05);
		else if(key == KeyEvent.VK_RIGHT)
			phi = (phi + 0.05);
		else if(key == KeyEvent.VK_R)
		{
			t = phi / 5;
			rotate = !rotate;
		}
		else if(key == KeyEvent.VK_S)
			showFunction = !showFunction;
		else if(key == KeyEvent.VK_C)
		{
			showFunction = true;
			isCleared = true;
			function = null;
		}
    }

    public void keyReleased(KeyEvent k)
    {

    }

    public void keyTyped(KeyEvent k)
    {

    }

    class mousePressHandler extends MouseAdapter
   	{
		public void mousePressed(MouseEvent e)
		{
	        int x = e.getX() - 6;
	        int y = e.getY() - 27;

	        try
	        {
	        	Button b = Button.buttonAtLocation(x, y);
	        	if(b == funinput)
	        		functionClicked();
	        	else if(b == calculus)
	        		calcClicked();
	        	else if(b == settings)
	        	{
	        		try
	        		{
	        			settingsClicked();
	        		}
	        		catch(NumberFormatException n)
	        		{
	        			JOptionPane.showMessageDialog(null, "Invalid Syntax");
	        		}
	        	}
	        	else if(b == help)
		        	helpClicked();
	        }
	        catch(NoSuchFieldException n)
	        {

	        }

			if(Math.abs(topleft[0] + WIDTH - x) <= 5 && Math.abs(topleft[1] + HEIGHT - y) <= 5)
			{
				resizeOn = true;
				dragx = e.getX();
				dragy = e.getY();
			}
	        else if(x >= topleft[0] && x <= topleft[0] + WIDTH && y >= topleft[1] && y <= topleft[1] + HEIGHT)
	        {
	        	isClicked = true;
	        	dragx = e.getX();
	        	dragy = e.getY();
	        }
        }

        public void mouseReleased(MouseEvent e)
        {
        	isClicked = false;
        	resizeOn = false;
        }
   }

	class mouseMotionHandler extends MouseMotionAdapter
   	{
		public void mouseDragged(MouseEvent e)
		{
			if(isClicked)
			{
				int dx = e.getX() - dragx;
				int dy = e.getY() - dragy;
				phi -= dx/100.0;
				if(theta - dy/100.0 >= 0 && theta - dy/100.0 <= Math.PI)
					theta -= dy/100.0;
			}
			if(resizeOn)
			{
				WIDTH = e.getX() - 29;
				HEIGHT = e.getY() - 49;
				WIDTH = HEIGHT = (int)Math.max(WIDTH, HEIGHT);
			}
			dragx = e.getX();
			dragy = e.getY();
		}
   	}

   class mouseWheelHandler extends MouseAdapter
   {
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			if(e.getWheelRotation() == 1)
			{
				if(rho - 0.25 >= Math.sqrt(4*graphRange*graphRange + graphzrange*graphzrange))
					rho -= 0.25;
			}
			else
				rho += 0.25;
		}
   }

	public void helpClicked()
	{
  		String s = "";
  		s += "\tThis program is used to plot functions of two variables. The horizontal axes represent the x and y variables, as labeled, and the";
  		s += " vertical axis represents the value of the function \nevaluated at these parameters.";
  		s += "\n\n\tIn order to input a function, click the \'function input\' button, type in a syntatically correct function with x and y as the only v";
  		s += "ariables, and click \'OK\'. \n\n\tThis program supports six binary operations: + for addtion, - for subtraction, * for multiplication, / fo";
  		s += "r division, ^ for exponentiation, and % for modulus. \n\n\tThis program supports the following unary operations: the six trigonometric functio";
  		s += "ns, their inverses, the natural logarithm (log), square root (sqrt), the exponential funtion (exp), absolute value (abs), and gamma ";
  		s += "(factorial).";
  		s += "\n\n\tThis program supports calculus with functions of multiple variables. For the directional derivative, input the parameters in th";
  		s += "e following manner: x, y, x direction, y direction; w\nere the derivative is evaluated at (x, y). In order to integrate f(x, y) over the ";
  		s += "rectangular region where a <= x <= b and c <= y <= d, input the parameters as follows: a, b, c, d. In both of these inputs, the paramete";
  		s += "rs can be \nseperated by commas or by spaces. Arguments must be real numbers, not expressions.";
  		JOptionPane.showMessageDialog(null, s);
	}

	public void functionClicked()
	{
		String input = JOptionPane.showInputDialog("f(x, y) = ", ((function == null) ? "" : function.toString()));

		try
		{
			if(input != null)
			{
				Function f = new Function(input, 'x', 'y');
				function = f;
				fillVals();
				showFunction = true;
				isCleared = false;
			}
		}
		catch(IllegalArgumentException e)
		{
			JOptionPane.showMessageDialog(null, "Invalid Syntax");
		}
	}

	public void calcClicked()
	{
		String[] options = {"Directional Derivative", "Volume Integral", "Cancel"};

		int response = JOptionPane.showOptionDialog(null, "Calculus", "Calculus", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

		if(response == 0)
		{
			String input = JOptionPane.showInputDialog("Directional derivative parameters:");
			try
			{
				if(input != null)
				{
					String output = ""+Calculus.threedderivative(function, input);
					output = output.substring(0, output.indexOf('.') + Calculus.derivplaces + 1);
					JOptionPane.showMessageDialog(null, output);
				}
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, "Invalid Syntax");
			}
		}
		else if(response == 1)
		{
			String input = JOptionPane.showInputDialog("Volume integral parameters:");
			try
			{
				if(input != null)
				{
					String output = ""+Calculus.volumeintegral(function, input);
					if((output.indexOf('.') + Calculus.intplaces + 1) <= output.length())
						output = output.substring(0, output.indexOf('.') + Calculus.intplaces + 1);
					JOptionPane.showMessageDialog(null, output);
				}
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, "Invalid Syntax");
			}
		}
	}

	public void settingsClicked()
	{
		String[] options = {"Graph Domain Range", "Graph z-Range", "Field of View", "Colors", "Points Calculated", "Frames per Second", "Cancel"};
		String title = "Settings";
		int response = JOptionPane.showOptionDialog(null, title, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

		if(response == 0)
		{
			String s = JOptionPane.showInputDialog("Graph Domain Range:", ""+graphRange);
			graphRange = (s == null || s.equals("")) ? graphRange : Double.parseDouble(s);
			if(function != null)
				fillVals();
		}
		else if(response == 1)
		{
			String s = JOptionPane.showInputDialog("Graph z-Range:", ""+graphzrange);
			graphzrange = (s == null || s.equals("") ? graphzrange : Double.parseDouble(s));
		}
		else if(response == 2)
		{
			String s = JOptionPane.showInputDialog("Fiield of View (degrees):", ""+fov);
			fov = (s == null || s.equals("")) ? fov : Double.parseDouble(s);
			viewer.setfov(Math.PI*fov/180);
		}
		else if(response == 3)
		{
			pickColor();
		}
		else if(response == 4)
		{
			String s = JOptionPane.showInputDialog("Points calculated in each direction\nSuggested to be odd\nWarning: run time is proportional to the square of this quantity", ""+arraysize);
			arraysize = (s == null || s.equals("")) ? arraysize : Integer.parseInt(s);
			if(function != null)
			{
				xcoordinates = new double[arraysize];
    			ycoordinates = new double[arraysize];
				functionvals = new double[arraysize][arraysize];
				fillVals();
			}
		}
		else if(response == 5)
		{
			String s = JOptionPane.showInputDialog("Frames per second\nWarning: high fps may decrease performance:", ""+fps);
			fps = (s == null || s.equals("")) ? fps : Integer.parseInt(s);
		}
	}

	public void pickColor()
	{
		Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, new Color(0.5f, 1f, 0.5f), new Color(0.5f, 0.5f, 1f), new Color(0.75f, 0f, 0.75f), Color.BLACK, new Color(0.25f, 0.25f, 0.25f), Color.WHITE};
		String[] options = {"Function Color", "Graph Background", "Window Background", "Button Color", "Axes Color", "TextColor", "Cancel"};
		String title = "Color Settings";
		int response = JOptionPane.showOptionDialog(null, title, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

		if(response != 6)
		{
			String[] coloroptions = {"Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Black", "Gray", "White"};
			String colorresponse = ""+JOptionPane.showInputDialog(null, options[response], options[response], JOptionPane.YES_NO_OPTION, null, coloroptions, null);
			Color c = null;
			for(int i = 0; i < coloroptions.length; i++)
				if(coloroptions[i].equals(colorresponse))
				{
					c = new Color(colors[i].getRGB());
				}
			if(c != null)
			{
				if(response == 0)
				{
					functionColor = c;
				}
				else if(response == 1)
				{
					functionBGColor = c;
				}
				else if(response == 2)
				{
					frameBGColor = c;
				}
				else if(response == 3)
				{
					buttonColor = c;
				}
				else if(response == 4)
				{
					axesColor = c;
				}
				else if(response == 5)
				{
					stringColor = c;
				}
			}
		}
	}

   	@SuppressWarnings("serial")
	class MyDrawPanel extends JPanel
   	{
  		public void paintComponent(Graphics g)
		{
			drawGraphBG(g, MultivariableGrapher.functionBGColor);
			drawAxes(g, MultivariableGrapher.axesColor, MultivariableGrapher.stringColor);
			drawBox(g, MultivariableGrapher.axesColor);
			if(MultivariableGrapher.showFunction && !MultivariableGrapher.isCleared)
				drawFunction(g, MultivariableGrapher.functionColor);

			drawBG(g, MultivariableGrapher.frameBGColor);
			g.setColor(MultivariableGrapher.stringColor);
			if(!MultivariableGrapher.isCleared)
				g.drawString(MultivariableGrapher.function.toString(), MultivariableGrapher.topleft[0], MultivariableGrapher.topleft[1] - 5);
			drawButtons(g, MultivariableGrapher.buttonColor);
		}

		public void drawButtons(Graphics g, Color c)
		{
			for(int i = 0; i < Button.buttons.size(); i++)
			{
				drawButton(g, c, Button.buttons.get(i));
			}
		}

		public void drawButton(Graphics g, Color c, Button b)
		{
			g.setColor(c);
			g.fillRect(b.left(), b.top(), b.width(), b.height());
			g.setColor(Color.black);
			g.drawRect(b.left(), b.top(), b.width(), b.height());
			g.drawString(b.toString(), b.left() + 5, (b.top() + b.bottom())/2 + 3);
		}

		private void drawBG(Graphics g, Color c)
		{
			g.setColor(c);
			g.fillRect(0, 0, MultivariableGrapher.topleft[0] + MultivariableGrapher.WIDTH, MultivariableGrapher.topleft[1]);
			g.fillRect(0, 0, MultivariableGrapher.topleft[0], MultivariableGrapher.totalheight);
			g.fillRect(MultivariableGrapher.topleft[0], MultivariableGrapher.topleft[1] + MultivariableGrapher.HEIGHT, MultivariableGrapher.WIDTH, MultivariableGrapher.totalheight - MultivariableGrapher.topleft[1] - MultivariableGrapher.HEIGHT);
			g.fillRect(MultivariableGrapher.topleft[0] + MultivariableGrapher.WIDTH, 0, MultivariableGrapher.totalwidth - MultivariableGrapher.WIDTH - MultivariableGrapher.topleft[0], MultivariableGrapher.totalheight);

			g.setColor(Color.white);
			g.fillRect(MultivariableGrapher.topleft[0] + MultivariableGrapher.WIDTH - 5, -5 + MultivariableGrapher.topleft[1] + MultivariableGrapher.HEIGHT, 10, 10);
			g.setColor(Color.black);
			g.drawRect(MultivariableGrapher.topleft[0] + MultivariableGrapher.WIDTH - 5, MultivariableGrapher.topleft[1] + MultivariableGrapher.HEIGHT - 5, 10, 10);
		}

		private void drawBox(Graphics g, Color c)
		{
			double d1 = MultivariableGrapher.graphRange;
			double d2 = MultivariableGrapher.graphzrange;

			double[] p1 = {d1, d1, d2};
			double[] p2 = {d1, d1, -d2};
			double[] p3 = {d1, -d1, d2};
			double[] p4 = {d1, -d1, -d2};
			double[] p5 = {-d1, d1, d2};
			double[] p6 = {-d1, d1, -d2};
			double[] p7 = {-d1, -d1, d2};
			double[] p8 = {-d1, -d1, -d2};

			drawLine(g, c, p1, p2);
			drawLine(g, c, p1, p3);
			drawLine(g, c, p1, p5);
			drawLine(g, c, p2, p4);
			drawLine(g, c, p2, p6);
			drawLine(g, c, p3, p4);
			drawLine(g, c, p3, p7);
			drawLine(g, c, p4, p8);
			drawLine(g, c, p5, p6);
			drawLine(g, c, p5, p7);
			drawLine(g, c, p6, p8);
			drawLine(g, c, p7, p8);
		}

		private void drawFunction(Graphics g, Color c)
		{
			for(int i = 0; i < MultivariableGrapher.arraysize - 1; i++)
			{
				for(int j = 0; j < MultivariableGrapher.arraysize - 1; j++)
				{
					double[] p0 = {(double)MultivariableGrapher.xcoordinates[i], (double)MultivariableGrapher.ycoordinates[j], (double)MultivariableGrapher.functionvals[i][j]};
					double[] px = {(double)MultivariableGrapher.xcoordinates[i + 1], (double)MultivariableGrapher.ycoordinates[j], (double)MultivariableGrapher.functionvals[i + 1][j]};
					double[] py = {(double)MultivariableGrapher.xcoordinates[i], (double)MultivariableGrapher.ycoordinates[j + 1], (double)MultivariableGrapher.functionvals[i][j + 1]};

					drawFunctionLines(g, c, p0, px, py);
				}
			}

			for(int j = 0, i = MultivariableGrapher.arraysize - 1; j < MultivariableGrapher.arraysize - 1; j++)
			{
					double[] p0 = {(double)MultivariableGrapher.xcoordinates[i], (double)MultivariableGrapher.ycoordinates[j], (double)MultivariableGrapher.functionvals[i][j]};
					double[] py = {(double)MultivariableGrapher.xcoordinates[i], (double)MultivariableGrapher.ycoordinates[j + 1], (double)MultivariableGrapher.functionvals[i][j + 1]};
					drawFunctionLines(g, c, p0, py);
			}

			for(int i = 0, j = MultivariableGrapher.arraysize - 1; i < MultivariableGrapher.arraysize - 1; i++)
			{
					double[] p0 = {(double)MultivariableGrapher.xcoordinates[i], (double)MultivariableGrapher.ycoordinates[j], (double)MultivariableGrapher.functionvals[i][j]};
					double[] px = {(double)MultivariableGrapher.xcoordinates[i + 1], (double)MultivariableGrapher.ycoordinates[j], (double)MultivariableGrapher.functionvals[i + 1][j]};
					drawFunctionLines(g, c, p0, px);
			}
		}

		private void drawFunctionLines(Graphics g, Color c, double[] p0, double[] px)
		{
			for(;;)
			{
				//if both points are in range
				if(p0[2] <= MultivariableGrapher.graphzrange && p0[2] >= -MultivariableGrapher.graphzrange)
				{
					if(px[2] <= MultivariableGrapher.graphzrange && px[2] >= -MultivariableGrapher.graphzrange)
					{
						drawLine(g, c, p0, px);
					}
				}
				//one point is in range, and another is above the max height
				if((p0[2] > MultivariableGrapher.graphzrange) && (px[2] <= MultivariableGrapher.graphzrange))
				{
					double ratio =  (MultivariableGrapher.graphzrange - px[2])/(-px[2] + p0[2]);
					p0 = LinearAlgebra.sum(px, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, px)));
					drawLine(g, c, p0, px);
				}
				if(p0[2] > MultivariableGrapher.graphzrange) break; //don't do the rest of this if p0 is in range
				if(px[2] > MultivariableGrapher.graphzrange)
				{
					double ratio =  (MultivariableGrapher.graphzrange - p0[2])/(px[2] - p0[2]);
					px = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(px, p0)));
					drawLine(g, c, p0, px);
				}

				//if one point is in range, and the other is below the min height
				if(p0[2] < -MultivariableGrapher.graphzrange && px[2] >= -MultivariableGrapher.graphzrange)
				{
					double ratio = (px[2] + MultivariableGrapher.graphzrange)/(px[2] - p0[2]);
					p0 = LinearAlgebra.sum(px, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, px)));
					drawLine(g, c, p0, px);
				}
				if(p0[2] < -MultivariableGrapher.graphzrange) break;
				if(px[2] < -MultivariableGrapher.graphzrange)
				{
					double ratio = (p0[2] + MultivariableGrapher.graphzrange)/(p0[2] - px[2]);
					px = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(px, p0)));
					drawLine(g, c, p0, px);
				}
				break;
			}
		}

		private void drawFunctionLines(Graphics g, Color c, double[] p0, double[] px, double[] py)
		{
			for(;;)
			{
				//all three points are in range
				if(p0[2] <= MultivariableGrapher.graphzrange && px[2] <= MultivariableGrapher.graphzrange && py[2] <= MultivariableGrapher.graphzrange)

					if(p0[2] >= -MultivariableGrapher.graphzrange && px[2] >= -MultivariableGrapher.graphzrange && py[2] >= -MultivariableGrapher.graphzrange)
					{
						drawLine(g, c, p0, px);
						drawLine(g, c, p0, py);
						break;
					}

				//two points are in range, one is not
				if(p0[2] <= MultivariableGrapher.graphzrange && p0[2] >= -MultivariableGrapher.graphzrange)
				{
					if(px[2] <= MultivariableGrapher.graphzrange && px[2] >= -MultivariableGrapher.graphzrange)
				{
					drawLine(g, c, p0, px);
				}
				else if(py[2] <= MultivariableGrapher.graphzrange && py[2] >= -MultivariableGrapher.graphzrange)
					{
						drawLine(g, c, p0, py);
					}
				}
				//one point is in range, and another is above the max height
				if((p0[2] > MultivariableGrapher.graphzrange) && (px[2] <= MultivariableGrapher.graphzrange))
				{
					double ratio =  (MultivariableGrapher.graphzrange - px[2])/(-px[2] + p0[2]);
					p0 = LinearAlgebra.sum(px, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, px)));
					drawLine(g, c, p0, px);
				}
				if((p0[2] > MultivariableGrapher.graphzrange) && py[2] <= (MultivariableGrapher.graphzrange))
				{
					double ratio =  (MultivariableGrapher.graphzrange - py[2])/(-py[2] + p0[2]);
					p0 = LinearAlgebra.sum(py, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, py)));
					drawLine(g, c, p0, py);
				}
				if(p0[2] > MultivariableGrapher.graphzrange) break; //don't do the rest of this if p0 is in range
				if(px[2] > MultivariableGrapher.graphzrange)
				{
					double ratio =  (MultivariableGrapher.graphzrange - p0[2])/(px[2] - p0[2]);
					px = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(px, p0)));
					drawLine(g, c, p0, px);
				}
				if(py[2] > MultivariableGrapher.graphzrange)
				{
					double ratio =  (MultivariableGrapher.graphzrange - p0[2])/(py[2] - p0[2]);
					py = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(py, p0)));
					drawLine(g, c, p0, py);
				}

				//if one point is in range, and the other is below the min height
				if(p0[2] < -MultivariableGrapher.graphzrange && px[2] >= -MultivariableGrapher.graphzrange)
				{
					double ratio = (px[2] + MultivariableGrapher.graphzrange)/(px[2] - p0[2]);
					p0 = LinearAlgebra.sum(px, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, px)));
					drawLine(g, c, p0, px);
				}
				if(p0[2] < -MultivariableGrapher.graphzrange && py[2] >= -MultivariableGrapher.graphzrange)
				{
					double ratio = (py[2] + MultivariableGrapher.graphzrange)/(py[2] - p0[2]);
					p0 = LinearAlgebra.sum(py, LinearAlgebra.product(ratio, LinearAlgebra.difference(p0, py)));
					drawLine(g, c, p0, py);
				}
				if(p0[2] < -MultivariableGrapher.graphzrange) break;
				if(px[2] < -MultivariableGrapher.graphzrange)
				{
					double ratio = (p0[2] + MultivariableGrapher.graphzrange)/(p0[2] - px[2]);
					px = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(px, p0)));
					drawLine(g, c, p0, px);
				}
				if(py[2] < -MultivariableGrapher.graphzrange)
				{
					double ratio = (p0[2] + MultivariableGrapher.graphzrange)/(p0[2] - py[2]);
					py = LinearAlgebra.sum(p0, LinearAlgebra.product(ratio, LinearAlgebra.difference(py, p0)));
					drawLine(g, c, p0, py);
				}
				break;
			}
		}

		private void drawGraphBG(Graphics g, Color c)
		{
			g.setColor(c);
			g.fillRect(MultivariableGrapher.topleft[0], MultivariableGrapher.topleft[1], MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
		}

		private void drawAxes(Graphics g, Color c, Color labelcolor)
		{
			double[] p1 = {MultivariableGrapher.graphRange, 0, 0};
			double[] p2 = {-MultivariableGrapher.graphRange, 0, 0};

			double[] p3 = {0, MultivariableGrapher.graphRange, 0};
			double[] p4 = {0, -MultivariableGrapher.graphRange, 0};

			double[] p5 = {0, 0, MultivariableGrapher.graphzrange};
			double[] p6 = {0, 0, -MultivariableGrapher.graphzrange};

			drawLine(g, c, p1, p2);
			drawLine(g, c, p3, p4);
			drawLine(g, c, p5, p6);

			drawString(g, labelcolor, "x", p1);
			drawString(g, labelcolor, "-x", p2);
			drawString(g, labelcolor, "y", p3);
			drawString(g, labelcolor, "-y", p4);
			drawString(g, labelcolor, "z", p5);
			drawString(g, labelcolor, "-z", p6);
		}

		private void drawLine(Graphics g, Color c, double[] p1, double[] p2)
		{
			try
			{
				int[] s1 = MultivariableGrapher.viewer.pointOnScreen(p1, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
				int[] s2 = MultivariableGrapher.viewer.pointOnScreen(p2, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);

				g.setColor(c);
				g.drawLine(MultivariableGrapher.topleft[0] + s1[0], MultivariableGrapher.topleft[1] + s1[1], MultivariableGrapher.topleft[0] + s2[0], MultivariableGrapher.topleft[1] + s2[1]);
			}
			catch(UnsupportedOperationException ie)
			{

			}
		}

		private void drawString(Graphics g, Color c, String s, double[] p)
		{
			try
			{
				g.setColor(c);
				int[] d = MultivariableGrapher.viewer.pointOnScreen(p, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
				g.drawString(s, MultivariableGrapher.topleft[0] + d[0], MultivariableGrapher.topleft[1] + d[1]);
			}
			catch(UnsupportedOperationException ie)
			{

			}
		}
//
//		private void drawTriangle(Graphics g, Color c, double[] p1, double[] p2, double[] p3)
//		{
//			try
//			{
//				g.setColor(c);
//				int[] d1 = MultivariableGrapher.viewer.pointOnScreen(p1, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
//				int[] d2 = MultivariableGrapher.viewer.pointOnScreen(p2, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
//				int[] d3 = MultivariableGrapher.viewer.pointOnScreen(p3, MultivariableGrapher.WIDTH, MultivariableGrapher.HEIGHT);
//
//				int[] xs = {d1[0], d2[0], d3[0]};
//				int[] ys = {d1[1], d2[1], d3[1]};
//
//				Polygon p = new Polygon(xs, ys, 3);
//				g.fillPolygon(p);
//
//			}
//			catch(UnsupportedOperationException ie)
//			{
//
//			}
//		}
    }
}