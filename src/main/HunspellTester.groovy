package main

import dk.dren.hunspell.*

public class HunspellTester
{
	Hunspell hunspell
	Hunspell.Dictionary dict
	
	HunspellTester(String dictionaryFilePath)
	{
		String basePath = dictionaryFilePath.replaceAll(/\.dic$/, "")
		println(basePath)
		hunspell = Hunspell.getInstance()
		dict = hunspell.getDictionary(basePath);
		println("Hunspell library and dictionary loaded");
	}
	
	def misspelled(String word)
	{
		if(dict.misspelled(word))
		{
			return true
		}
		else
		{
			return false
		}
	}
	
	def checkFile(String filename)
	{
		boolean bad = filename.endsWith("_bad.txt")
		def wordList = []
		new File(filename).eachLine{line->
			line.split(/\s+/).toList().each{word->
				wordList << word
			}
		}
		def results = []
		for(word in wordList)
		{
			if(bad)
			{
				if(dict.misspelled(word)){}
				else
				{
					def result = [word:word]
					result.stem = dict.stem(word)
					result.morph = dict.analyze(word)
					results << result
				}
			}
			else
			{
				if(dict.misspelled(word))
				{
					def result = [word:word]
					result.suggest = dict.suggest(word)
					result.stem = dict.stem(word)
					result.morph = dict.analyze(word)
					results << result
				}
				else{}
			}
		}
		return results
	}
}