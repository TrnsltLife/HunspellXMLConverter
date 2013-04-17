package hunspellxml

import groovy.beans.Bindable
import org.sil.hunspellxml.HunspellTester
import main.JSpellCheckPane
import main.Preferences
import java.awt.Font

@Bindable
//@PropertyListener()
class HunspellXMLModel 
{
   //@Bindable
   String statusLog = ""
   @Bindable String delimiters = "'-`~!@#\$%^&*()_+=[]\\{}|;:\",./<>?"
   String startingMessage = "<br><br><b>Drop your Hunspell.xml file here!</b><br><i>(Or use the File menu.)</i><br>"
   
   boolean hunspellFileNameUseInput = false
   
   Font font = new Font("SansSerif", Font.PLAIN, 12)
   
   def options = [hunspell:true,
		tests:true,
		thesaurus:true,
		license:true,
		readme:true,
		libreOffice:true,
		firefox:true,
		opera:true,
		relaxNG:false,
		runTests:true,
		printPaths:true,
		customPath:"",
		hunspellFileName:""]
	
	def xmlDirectory = ""
	def dicDirectory = ""
	def dirDirectory = ""
		
   ArrayList toggleButtons = 
   [
		[key:"hunspell", value:"Dictionary"],
		[key:"tests", value:"Tests"],
		[key:"thesaurus", value:"Thesaurus"],
		[key:"readme", value:"Readme"],
		[key:"license", value:"License"],
		[key:"firefox", value:"Firefox"],
		[key:"libreOffice", value:"LibreOffice"],
		[key:"opera", value:"Opera"],
		[key:"relaxNG", value:"RelaxNG Schema"],
		[key:"runTests", value:"Run Tests"],
		[key:"printPaths", value:"Print Paths"]
	]
	
	HunspellTester tester
	
	
	public HunspellXMLModel()
	{
		loadPreferences()
	}
	
	
	def loadPreferences()
	{
		def config = Preferences.loadPreferences()
		if(config)
		{
			if(config.options)
			{
				for(key in config.options.keySet())
				{
					options[key] = config.options[key]
				}
			}
			if(config.xmlDirectory){xmlDirectory = config.xmlDirectory}
			if(config.dicDirecotry){dicDirectory = config.dicDirectory}
			if(config.dirDirectory){dirDirectory = config.dirDirectory}
			if(config.hunspellFileNameUseInput){hunspellFileNameUseInput = config.hunspellFileNameUseInput}
			if(config.font){font = Font.decode(config.font)}
			if(config.delimiters){delimiters = config.delimiters}
		}
	}
	
	def savePreferences()
	{
		def map = [:]
		map.options = options
		map.options.remove("customPath")
		map.options.remove("hunspellFileName")
		map.xmlDirectory = xmlDirectory.replaceAll($/\\/$, "/")
		map.dicDirectory = dicDirectory.replaceAll($/\\/$, "/")
		map.dirDirectory = dirDirectory.replaceAll($/\\/$, "/")
		map.hunspellFileNameUseInput = hunspellFileNameUseInput
		map.font = getFontString()
		map.delimiters = delimiters.replaceAll($/\\/$, "\\\\\\\\")
		Preferences.savePreferences(map)
	}
	
	def getFontString()
	{
		if(!font){return ""}
		def fontString = font.getFontName()
		def sep = " "
		if(fontString.contains(" ")){sep = "-"}
		fontString += sep
		if(font.isPlain()){fontString += "PLAIN"}
		if(font.isBold()){fontString += "BOLD"}
		if(font.isItalic()){fontString += "ITALIC"}
		fontString += sep
		fontString += font.getSize()
		return fontString
	}
}