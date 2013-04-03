package main

import java.awt.*
import javax.swing.*
import javax.swing.event.*
import javax.swing.text.*

class JSpellCheckPane extends JTextPane
{
	SimpleAttributeSet normalStyle
	SimpleAttributeSet errorStyle
	String delimiters
	def spellCheckMethod
	
	public JSpellCheckPane(spellCheckMethod)
	{
		this(spellCheckMethod, " \t\n\r\f")
	}
	
	public JSpellCheckPane(spellCheckMethod, delimiters)
	{
		super()
		//setMaximumSize(new Dimension(300, 60))
		setMinimumSize(new Dimension(300, 60))
		setPreferredSize(new Dimension(400, 100))
		
		this.spellCheckMethod = spellCheckMethod
		this.delimiters = delimiters

		injectSpellCheckMethod()
		
		//normal style
		normalStyle = new SimpleAttributeSet();
		StyleConstants.setUnderline(normalStyle, false)
		StyleConstants.setForeground(normalStyle, Color.black)

		//red and underlined
		errorStyle = new SimpleAttributeSet();
		StyleConstants.setUnderline(errorStyle, true)
		StyleConstants.setForeground(errorStyle, Color.red)
	}
	
	private injectSpellCheckMethod()
	{
		def spellCheckingDocumentListener = [
			insertUpdate:{e->
				SwingUtilities.invokeLater{
					spellCheckMethod.call(e.getDocument(), e.getOffset(), e.getLength(), normalStyle, errorStyle, delimiters)
				}
			},
			removeUpdate:{e->
				SwingUtilities.invokeLater{
					spellCheckMethod.call(e.getDocument(), e.getOffset(), e.getLength(), normalStyle, errorStyle, delimiters)
				}
			},
			changedUpdate:{e->
			}
		]
		getDocument().addDocumentListener(spellCheckingDocumentListener as DocumentListener)
	}
	
	public checkSpelling()
	{
		spellCheckMethod.call(getDocument(), getDocument().getStartPosition().getOffset(), getDocument().getLength(), normalStyle, errorStyle, delimiters)
	}
}