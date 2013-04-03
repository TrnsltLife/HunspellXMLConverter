package main
import org.sil.hunspellxml.HunspellXMLConverter
import org.sil.hunspellxml.HunspellXMLExporter

class HunspellXMLCLI
{
	static flagMap = [hs:"hunspell",
		tst:"tests",
		th:"thesaurus",
		rm:"readme",
		lc:"license",
		ff:"firefox",
		lo:"libreOffice",
		op:"opera",
		rng:"relaxNG"]
	
	public static void main(String[] args)
	{
		boolean invalidFlags = false
	
		if(args.size() < 1)
		{
			printUsage()
			System.exit(1)
		}
		
		def file = args[-1]
		def options = HunspellXMLExporter.defaultOptions.clone()
		
		if(args[0] == "-h" || args[0] == "-?")
		{
			printUsage()
			System.exit(0)
		}
		
		//process flags
		for(int i=0; i<args.size()-1; i++)
		{
			def arg = args[i]
			if(arg.startsWith("-"))
			{
				arg -= "-"
				if(flagMap[arg])
				{
					options[flagMap[arg]] = false
				}
				else if(arg == "h" || arg == "?")
				{
					printUsage()
					System.exit(0)
				}
				else if(arg.startsWith("p="))
				{
					//custom path
					arg -= "p="
					if(arg.startsWith('"') && arg.endsWith('"'))
					{
						arg = arg.replaceAll(/^"/, "")
						arg = arg.replaceAll(/"$/, "")
					}
					options.customPath = arg
				}
				else if(arg.startsWith("o="))
				{
					//custom output filename
					arg -= "o="
					if(arg.startsWith('"') && arg.endsWith('"'))
					{
						arg = arg.replaceAll(/^"/, "")
						arg = arg.replaceAll(/"$/, "")
					}
					options.hunspellFileName = arg
				}
				else
				{
					println("Invalid flag: -" + arg)
					invalidFlags = true
				}
			}
		}
		
		if(invalidFlags)
		{
			printUsage()
			System.exit(2)
		}
		
		if(new File(file).exists())
		{
			println("Options: " + options)
			
			def hxc = new HunspellXMLConverter(new File(file), options)
			hxc.convert()
		}
		else
		{
			println("File not found: " + file)
		}
	}
	
	public static printUsage()
	{
println(
"""Usage:
hunspellxml [optional_flags] hunspellXML_input_file.xml
Optional Flags:
-o=filename      Base output filename for Hunspell dictionary
-p=custom_path   Custom path for Hunspell dictionary output
-hs              Do not export Hunspell dictionary files
-tst             Do not export Hunspell test files
-th              Do not export MyThes thesaurus files
-rm              Do not export Readme file
-lc              Do not export License file
-ff              Do not export Firefox dictionary plugin
-lo              Do not export LibreOffice dictionary plugin
-op              Do not export Opera dictionary plugin
-rng             Do not export RelaxNG schema for HunspellXML
-h or -?         Print this help message
""")
	}
}
