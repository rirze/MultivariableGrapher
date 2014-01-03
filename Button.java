import java.awt.*;
import java.util.ArrayList;

public class Button
{
	public static ArrayList<Button> buttons = new ArrayList<Button>();

	private Color color;
	private String name;
	private int[] location = new int[4];

	public Button(String s, Color c, int a, int b, int width, int height)
	{
		color = c;
		name = s;
		location[0] = a;
		location[1] = b;
		location[2] = a + width;
		location[3] = b + height;

		buttons.add(this);
	}

	public Color color()
	{
		return color;
	}

	public String toString()
	{
		return name;
	}

	public int top()
	{
		return location[1];
	}

	public int left()
	{
		return location[0];
	}

	public int bottom()
	{
		return location[3];
	}

	public int right()
	{
		return location[2];
	}

	public int width()
	{
		return location[2] - location[0];
	}

	public int height()
	{
		return location[3] - location[1];
	}

	public boolean contains(int x, int y)
	{
		return (location[0] <= x && location[2] >= x) && (location[1] <= y && location[3] >= y);
	}

	public void setColor(Color c)
	{
		color = c;
	}

	public static Button buttonAtLocation(int x, int y) throws NoSuchFieldException
	{
		for(int i = 0; i < buttons.size(); i++)
		{
			if(buttons.get(i).contains(x, y))
				return buttons.get(i);
		}
		throw new NoSuchFieldException("No button at this location");
	}
}