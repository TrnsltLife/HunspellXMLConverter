package main

class Preferences
{	
	public static String getUserDataDirectory()
	{
		return System.getProperty("user.home") + File.separator + ".hunspellXMLConverter" + File.separator
	}
	
	public static String getPreferencesFile()
	{
		return Preferences.getUserDataDirectory() + "preferences.config"
	}
	
	
	public static ConfigObject loadPreferences()
	{
		def config = new ConfigObject()		
		def file = new File(Preferences.getPreferencesFile())
		if(file.exists())
		{
			try
			{
				config = new ConfigSlurper().parse(file.text)
			}
			catch(Exception e){System.err.println(e)}
		}
		return config
	}
	
	public static void savePreferences(Map map)
	{
		def config = new ConfigObject()
		config.putAll(map)
		
		def dir = new File(Preferences.getUserDataDirectory())
		if(!dir.exists())
		{
			dir.mkdir()
		}
		def file = Preferences.getPreferencesFile()
		new File(file).withWriter{writer->
			config.writeTo(writer)
		}
	}
}
