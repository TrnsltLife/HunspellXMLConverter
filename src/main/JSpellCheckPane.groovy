package main

import java.awt.*
import javax.swing.*
import javax.swing.event.*
import javax.swing.text.*

class JSpellCheckPane extends JTextPane
{
	private static final String defaultDelimiters = " \t\n\r\f"
	
	private String delimiters = defaultDelimiters
	def spellCheckMethod
	
	public JSpellCheckPane(spellCheckMethod)
	{
		this(spellCheckMethod, "")
	}
	
	public JSpellCheckPane(spellCheckMethod, delimiters)
	{
		super()
		//setMaximumSize(new Dimension(300, 60))
		setMinimumSize(new Dimension(300, 60))
		setPreferredSize(new Dimension(400, 100))
		
		this.spellCheckMethod = spellCheckMethod
		setDelimiters(delimiters)

		injectSpellCheckMethod()
	}
	
	public setDelimiters(String delim)
	{
		//Always use whitespace as delimiters. Allow adding additional delimiters.
		String d = " \t\n\r\f"
		delim = delim.replaceAll(/[ \t\n\r\f]/, "")
		delimiters = d + delim
	}
	
	public String getDelimiters()
	{
		return delimiters
	}
	
	public String getCustomDelimiters()
	{
		//Return the list of delimiters, minus the default whitespace delimiters
		def delim = delimiters
		delim = delim.replaceAll(/[ \t\n\r\f]/, "")
		return delim
	}
	
	public static String getDefaultDelimiters()
	{
		return defaultDelimiters
	}
	
	private injectSpellCheckMethod()
	{
		def spellCheckingDocumentListener = [
			insertUpdate:{e->
				SwingUtilities.invokeLater{
					spellCheckMethod.call(e.getDocument(), e.getOffset(), e.getLength(), delimiters)
				}
			},
			removeUpdate:{e->
				SwingUtilities.invokeLater{
					spellCheckMethod.call(e.getDocument(), e.getOffset(), e.getLength(), delimiters)
				}
			},
			changedUpdate:{e->
			}
		]
		getDocument().addDocumentListener(spellCheckingDocumentListener as DocumentListener)
	}
	
	public checkSpelling()
	{
		spellCheckMethod.call(getDocument(), getDocument().getStartPosition().getOffset(), getDocument().getLength(), delimiters)
	}
}