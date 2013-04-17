package hunspellxml

import javax.swing.JFileChooser
import java.awt.KeyboardFocusManager
import java.awt.Window
import main.JFontChooser
import main.StackTrace
import org.sil.hunspellxml.HunspellXMLConverter
import org.sil.hunspellxml.HunspellTester
import java.beans.PropertyChangeListener

class HunspellXMLController {
    // these will be injected by Griffon
    def model
    def view
	def hxcLog
	
	
    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
		model.statusLog += model.startingMessage
		
		model.addPropertyChangeListener({ e ->
			println("property changed: " + e.getPropertyName())
			if(e.getPropertyName() == "delimiters")
			{
				changeDelimiters()
			}
		} as PropertyChangeListener)
    }
	
	
	def quit = { evt = null ->
		savePreferences()
		app.shutdown()
	}
	
	
	def about = { evt = null ->
		withMVCGroup("about", [owner: Window.windows.find{it.focused}]) { m, v, c ->
			m.title = "About HunspellXML"
			c.show()
		}
	}

	
    def openAndConvert = { evt = null ->
		if(model.xmlDirectory){view.fileChooserWindowXML.setCurrentDirectory(new File(model.xmlDirectory))}
		def openResult = view.fileChooserWindowXML.showOpenDialog()
		if(JFileChooser.APPROVE_OPTION == openResult) {
			File file = new File(view.fileChooserWindowXML.selectedFile.toString())
			doOutside {
				model.xmlDirectory = file.getParent().toString()
				convert(file)
			}
		}
    }
	
	def openDictionary = { evt = null ->
		if(model.dicDirectory){view.fileChooserWindowAFFDIC.setCurrentDirectory(new File(model.dicDirectory))}
		def openResult = view.fileChooserWindowAFFDIC.showOpenDialog()
		if(JFileChooser.APPROVE_OPTION == openResult) {
			File file = new File(view.fileChooserWindowAFFDIC.selectedFile.toString())
			doOutside {
				model.dicDirectory = file.getParent().toString()
				initializeDictionary(file.toString())
			}
		}
	}
	
	def selectDirectory = {evt = null ->
		if(model.dirDirectory){view.fileChooserWindowDIR.setCurrentDirectory(new File(model.dirDirectory))}
		def openResult = view.fileChooserWindowDIR.showOpenDialog()
		if(JFileChooser.APPROVE_OPTION == openResult) {
			File file = new File(view.fileChooserWindowDIR.selectedFile.toString())
			doOutside {
				model.dirDirectory = file.getParent().toString()
				view.customPath.text = file.toString()
			}
		}
	}
	
	def dropFile(file)
	{
		if(file.name.toLowerCase().endsWith(".aff") || file.name.toLowerCase().endsWith(".dic"))
		{
			initializeDictionary(file.toString())
		}
		else if(file.name.toLowerCase().endsWith(".xml"))
		{
			convert(file)
		}
	}
	
	def initializeDictionary(String file)
	{
		doOutside {
			model.tester = new HunspellTester(file)
			model.statusLog = "Loaded spellcheck dictionary: " + file + "<br>\r\n" + model.startingMessage
		}
		view.spellCheck.checkSpelling()
	}
	
	def chooseFont = {evt=null->
		view.fontChooser.setSelectedFont(model.font)
		def result = view.fontChooser.showDialog(Window.windows.find{it.focused})
		if(result == JFontChooser.OK_OPTION)
		{
			model.font = view.fontChooser.getSelectedFont();
			updateFont()
		}
	}
	
	def updateFont()
	{
		view.textPane.setFont(model.font)
		view.spellCheck.setFont(model.font)
		view.fontLabel.text = model.getFontString()
	}
	
	def changeDelimiters()
	{
		view.spellCheck.setDelimiters(view.delimitersField.text)
		view.spellCheck.checkSpelling()
	}
	
	def resetDelimiters = {evt=null->
		view.spellCheck.setDelimiters("'-`~!@#\$%^&*()_+=[]\\{}|;:\",./<>?")
		view.delimitersField.text = view.spellCheck.getCustomDelimiters()
		view.spellCheck.checkSpelling()
	}
	
	def convert(file)
	{
		doOutside{
			model.statusLog = ""
			
			model.statusLog += "Opening file ${file.getCanonicalPath()}<br>\r\n"
			
			model.options.customPath = view.customPath.text
			model.options.hunspellFileName = view.hunspellFileName.text
			if(model.hunspellFileNameUseInput)
			{
				def hunName = file.getName().replaceAll(/\.[xX][mM][lL]$/, "")
				model.options.hunspellFileName = hunName
			}
			
			new File(file.toString().replaceAll(/\.xml$/, "_HunspellXML-Converter.log")).withWriter("UTF-8"){logWriter->
				try
				{
					//Validate, convert, and export the Hunspell dictionary
					model.statusLog += "Validate HunspellXML...<br>\r\n"
					def hxc = new HunspellXMLConverter(file, model.options)
					hxc.log.infoLogHook = {message-> 
						logWriter << "INFO: " + message + "\r\n"
						model.statusLog += """<font color="#000099">$message</font><br>\r\n"""
					}
					hxc.log.errorLogHook = {message-> 
						logWriter << "ERROR: " + message + "\r\n"
						model.statusLog += formatError("ERROR:<pre>$message</pre>")
					}
					hxc.log.warningLogHook = {message->
						logWriter << "WARNING: " + message + "\r\n"
						message = message.replaceAll(/\r\n\{\r\n/, "\r\n<ul>\r\n")
						message = message.replaceAll(/\r\n\}\r\n/, "\r\n</ul>\r\n")
						for(i in 1..2)
						{
							message = message.replaceAll(/\r\n\t(.*)\r\n/, "\r\n<li>\$1</li>\r\n")
						}
						model.statusLog += formatWarning("WARNING: $message")
					}
					hxcLog = hxc.log

					hxc.convert()
					
					//Run tests
					if(hxc?.tester)
					{
						model.tester = hxc.tester
					}
					
					if(hxc?.parser?.check?.checkMap)
					{
						hxcLog.info("List of affixation paths in your dictionary:")
					}
					for(entry in hxc?.parser?.check?.checkMap)
					{
						def route = entry.value.route
						StringBuffer sb = new StringBuffer()
						hxcLog.info(hxc.parser.check.shortFormatRoute(route, true))
					}
					
					
					model.statusLog += model.startingMessage
					
					view.spellCheck.checkSpelling()
				}
				catch(Exception e)
				{
					model.statusLog += formatError("ERROR:<pre>${StackTrace.getStackTrace(e)}</pre>")
				}
			}
			model.statusLog += "<br>\r\n"
		} //end doOutside
	}
	
	def toggleOption(String optionKey)
	{
		model.options[optionKey] = !model.options[optionKey]
	}
	
	private formatError(text)
	{
		return """<font color="#ff0000">""" + text.toString() + "</font><br>\r\n"
	}
	
	private formatWarning(text)
	{
		return """<font color="#ff0000">""" + text.toString() + "</font><br>\r\n"
	}
	
	//private spellCheckDocument(document, start, length, normalStyle, errorStyle, delimiters)
	private spellCheckDocument(document, start, length, delimiters)
	{
		def beg = document.getStartPosition().getOffset()
		def end = beg + document.getLength()
		String text = document.getText(beg, end)
		
		view.spellCheck.getHighlighter().removeAllHighlights()

		if(model.tester)
		{
			int offset = 0
			def st = new StringTokenizer(text, delimiters, true)
			while(st.hasMoreTokens())
			{
				def token = st.nextToken()
				if(delimiters.contains(token))
				{
					//don't underline it
				}
				else
				{
					if(model.tester?.misspelled(token))
					{
						//misspelled
						view.spellCheck.getHighlighter().addHighlight(offset, offset+token.size(), view.badJag);
					}
					else
					{
						//correct
						view.spellCheck.getHighlighter().addHighlight(offset, offset+token.size(), view.goodJag);
					}
				}
				offset += token.size()
			}
		}
	}
	
	
	private spellCheck(text)
	{
		def results = ""
		if(model.tester)
		{
			for(word in text.split(/[\t\s]/).toList())
			{
				if(model.tester.misspelled(word))
				{
					results += "<u><font color='#ff0000'>$word</font></u> "
				}
				else
				{
					results += word + " "
				}
			}
		}
		return "<html><body>" + results + "</body></html>"
	}

	
	def savePreferences()
	{
		model.savePreferences()
	}
}
