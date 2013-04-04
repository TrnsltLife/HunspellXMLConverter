package hunspellxml

import javax.swing.JFileChooser
import java.awt.Window
import main.StackTrace
import org.sil.hunspellxml.HunspellXMLConverter
import org.sil.hunspellxml.HunspellTester

class HunspellXMLController {
    // these will be injected by Griffon
    def model
    def view
	def hxcLog
	
	def quit = { evt = null ->
		savePreferences()
		app.shutdown()
	}
	
	def copy = { evt = null ->
		view.textPane.copy()
	}
	
	def selectAll = { evt = null ->
		view.textPane.selectAll()
	}
	
	def about = { evt = null ->
		withMVCGroup("about", [owner: Window.windows.find{it.focused}]) { m, v, c ->
			m.title = "About HunspellXML"
			c.show()
		}
	}

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
		model.statusLog += model.startingMessage
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
	
	def convert(file)
	{
		model.statusLog = ""
		
		model.statusLog += "Opening file ${file.getCanonicalPath()}<br>\r\n"
		
		model.options.customPath = view.customPath.text
		model.options.hunspellFileName = view.hunspellFileName.text
		if(model.hunspellFileNameUseInput)
		{
			def hunName = file.getName().replaceAll(/\.[xX][mM][lL]$/, "")
			model.options.hunspellFileName = hunName
		}
		
		try
		{
			//Validate, convert, and export the Hunspell dictionary
			model.statusLog += "Validate HunspellXML...<br>\r\n"
			def hxc = new HunspellXMLConverter(file, model.options)
			hxc.log.infoLogHook = {message-> model.statusLog += """<font color="#000099">$message</font><br>\r\n"""}
			hxc.log.errorLogHook = {message-> model.statusLog += formatError("ERROR:<pre>$message</pre>")}
			hxc.log.warningLogHook = {message->
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
				hxcLog.info(hxc.parser.check.shortFormatRoute(route))
			}
			
			
			model.statusLog += model.startingMessage
			
			view.spellCheck.checkSpelling()
		}
		catch(Exception e)
		{
			model.statusLog += formatError("ERROR:<pre>${StackTrace.getStackTrace(e)}</pre>")
		}
		model.statusLog += "<br>\r\n"
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
	
	private spellCheckDocument(document, start, length, normalStyle, errorStyle, delimiters)
	{
		def beg = document.getStartPosition().getOffset()
		def end = beg + document.getLength()
		String text = document.getText(beg, end)

		if(model.tester)
		{
			int offset = 0
			def st = new StringTokenizer(text, delimiters, true)
			while(st.hasMoreTokens())
			{
				def token = st.nextToken()
				if(token =~ /[$delimiters]/)
				{
					document.setCharacterAttributes(offset, token.size(), normalStyle, true)
				}
				else
				{
					if(model.tester?.misspelled(token))
					{
						//random, misspelled
						document.setCharacterAttributes(offset, token.size(), errorStyle, true)
					}
					else
					{
						//random, correct
						document.setCharacterAttributes(offset, token.size(), normalStyle, true)
					}
				}
				offset += token.size()
			}
		}
		else
		{
			document.setCharacterAttributes(beg, end, normalStyle, true)
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
