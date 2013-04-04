package hunspellxml

import groovy.beans.Bindable
import org.sil.hunspellxml.HunspellTester
import main.Preferences

@Bindable
//@PropertyListener()
class HunspellXMLModel 
{
   @Bindable String statusLog = ""
   String startingMessage = "<br><br><b>Drop your Hunspell.xml file here!</b><br><i>(Or use the File menu.)</i><br>"
   
   boolean hunspellFileNameUseInput = false
   
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
		[key:"relaxNG", value:"RelaxNG Schema"]
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
			xmlDirectory = config.xmlDirectory
			dicDirectory = config.dicDirectory
			dirDirectory = config.dirDirectory
			hunspellFileNameUseInput = config.hunspellFileNameUseInput
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
		Preferences.savePreferences(map)
	}
}