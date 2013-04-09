package main

import java.awt.Color
import java.awt.BasicStroke
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter
import javax.swing.text.BadLocationException
import javax.swing.text.JTextComponent
import javax.swing.text.Position
import javax.swing.text.View

class JaggedUnderlinePainter extends DefaultHighlightPainter
{
	private static final java.awt.BasicStroke UNDERLINE1 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, [1,3] as float[], 0);
    private static final java.awt.BasicStroke UNDERLINE2 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, [1,1] as float[], 1);
    private static final java.awt.BasicStroke UNDERLINE3 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, [1,3] as float[], 2);

	private Color color
	
	public JaggedUnderlinePainter()
	{
		super(Color.RED)
		setColor(Color.RED)
	}
	
	public JaggedUnderlinePainter(Color color)
	{
		super(color)
		setColor(color)
	}
	
	public Color getColor()
	{
		return this.color
	}
	
	def setColor(Color color)
	{
		if(color == null){this.color = Color.RED}
		this.color = color
	}
	
	public Shape paintLayer(Graphics g, int offset0, int offset1, Shape bounds, JTextComponent comp, View view) 
	{
		g.setColor(color);

		if(offset0 == view.getStartOffset() && offset1 == view.getEndOffset()) 
		{
			// All the text is in the view: use the bounds
			Rectangle rect;
			if(bounds instanceof Rectangle)
			{
				rect = (Rectangle)bounds;
			}
			else
			{
				rect = bounds.getBounds();
			}
			paintJaggedLine(g, rect);
			return rect;
		}

		// Not all the text is in the view: do a partial render
		try 
		{
			// --- determine locations ---
			Shape shape = view.modelToView(offset0, Position.Bias.Forward, offset1, Position.Bias.Backward, bounds);
			Rectangle r = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
			paintJaggedLine(g, r);
			return r;
		}
		catch (BadLocationException e)
		{
			e.printStackTrace(); // can't render
		}

		// Only if exception
		return null;

	}

	protected void paintJaggedLine(Graphics g, Rectangle r)
	{
		int x1 = r.x;
		int x2 = x1 + r.width - 1;
		int y = r.y + r.height - 1;
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(UNDERLINE1);
		g2.drawLine(x1, y, x2, y);
		y--;
		
		g2.setStroke(UNDERLINE2);
		g2.drawLine(x1, y, x2, y);
		y--;
		
		g2.setStroke(UNDERLINE3);
		g2.drawLine(x1, y, x2, y);
	}
}