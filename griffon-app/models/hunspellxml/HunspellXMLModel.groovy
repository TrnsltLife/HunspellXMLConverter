package hunspellxml

import groovy.beans.Bindable
import org.sil.hunspellxml.HunspellTester

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
}