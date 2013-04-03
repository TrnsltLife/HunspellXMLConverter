package hunspellxml

import javax.swing.filechooser.FileFilter
import main.ClosureFileDropHandler
import main.JSpellCheckPane
import java.awt.Dimension
import javax.swing.event.DocumentListener
import javax.swing.text.*

fileChooserWindowXML = fileChooser(fileFilter: [getDescription: {-> "*.xml"}, accept:{file-> file ==~ /.*?\.xml/ || file.isDirectory() }] as FileFilter)
//fileChooserWindowDIC = fileChooser(fileFilter: [getDescription: {-> "*.dic"}, accept:{file-> file ==~ /.*?\.dic/ || file.isDirectory() }] as FileFilter)
fileChooserWindowAFFDIC = fileChooser(fileFilter: [getDescription: {-> "*.aff, *.dic"}, accept:{file-> file ==~ /.*?\.(aff|dic)/ || file.isDirectory() }] as FileFilter)
closureFileDropHandler = new ClosureFileDropHandler({controller.dropFile(it)})




application(title: 'HunspellXML',
  preferredSize: [800, 600],
  minimumSize: [800, 600],
  pack: true,
  //location: [50,50],
  locationByPlatform: true,
  iconImage:   imageIcon('/HunspellXML-icon-256x256.png').image,
  iconImages: [	imageIcon('/HunspellXML-icon-128x128.png').image,
				imageIcon('/HunspellXML-icon-64x64.png').image,
				imageIcon('/HunspellXML-icon-48x48.png').image,
				imageIcon('/HunspellXML-icon-32x32.png').image,
				imageIcon('/HunspellXML-icon-64x64.png').image,
				imageIcon('/HunspellXML-icon-16x16.png').image]) {
	actions
	{
		action(id:"openConvertAction", name:"Open and Convert...", mnemonic:"O", accelerator:shortcut("O"), closure:controller.openAndConvert)
		action(id:"openDictionaryAction", name:"Open Dictionary...", mnemonic:"D", accelerator:shortcut("D"), closure:controller.openDictionary)
		action(id:"quitAction", name:"Quit", mnemonic:"Q", accelerator:shortcut("Q"), closure:controller.quit)
		action(id:"copyAction", name:"Copy", mnemonic:"C", accelerator:shortcut("C"), closure:controller.copy)
		action(id:"selectAllAction", name:"Select All", mnemonic:"A", accelerator:shortcut("A"), closure:controller.selectAll)
		action(id:"about", name:"About", mnemonic:"?", accelerator:shortcut("A"), closure:controller.about)
	}
	menuBar {
		menu("File")
		{
			menuItem(openConvertAction)
			menuItem(openDictionaryAction)
			separator()
			menuItem(aboutAction)
			menuItem(quitAction)
		}
		menu(text: app.getMessage('application.menu.Edit.name', 'Edit'),
				mnemonic: app.getMessage('application.menu.Edit.mnemonic', 'E')) {
			menuItem(copyAction)
			menuItem(selectAllAction)
		}
	}
	borderLayout()
	scrollPane(constraints:CENTER)
	{
		textPane(id:"textPane", 
			editable:false, 
			contentType:"text/html; charset=UTF-8", 
			text:bind{model.statusLog.toString()}, 
			dragEnabled:true,
			transferHandler:closureFileDropHandler
		)
	}
	panel(constraints:EAST)
	{
		gridLayout(cols:1, rows:0, vgap:0)
		label(text:"Export these files:")
		for(map in model.toggleButtons)
		{
				checkBox(id:map.key, 
					text:map.value, 
					selected:(model.options[map.key]),
					actionPerformed:{controller.toggleOption(map.key)})
		}
	}
	panel(constraints:SOUTH)
	{
		borderLayout()
		panel(constraints:WEST)
		{
			tableLayout()
			{
				tr()
				{
					td(align:"left")
					{
						label(text:"Check spelling:")
					}
				}
				tr()
				{
					td(align:"left")
					{
						scrollPane()
						{
							widget(id:"spellCheck", new JSpellCheckPane(controller.&spellCheckDocument, "- \t\r\n\f.,:;'!?\\(\\)\\[\\]\\{\\}_\\<\\>\\/\\\\\\+=\\*"))
						}
					}
				}
			}
		}
		
		panel(constraints:EAST)
		{
			tableLayout()
			{
				tr()
				{
					td(align:"right")
					{
						label(text:"Custom filepath:")
					}
					td(align:"left")
					{
						textField(id:"customPath", text:"", columns:20)
					}
				}
				
				tr()
				{
					td(align:"right")
					{
						label(text:"Custom filename:")
					}
					td(align:"left")
					{
						textField(id:"hunspellFileName", text:"", columns:20)
					}
				}
				
				tr()
				{				
					td()
					{
						label(text:"")
					}
					td(align:"left")
					{
						checkBox(id:"hunspellFileNameUseInput", text:"Filename based on XML filename", selected:model.hunspellFileNameUseInput, 
							actionPerformed:{
								model.hunspellFileNameUseInput = view.hunspellFileNameUseInput.selected
								hunspellFileName.enabled = !view.hunspellFileNameUseInput.selected
							})
					}
				}
			}
		}
	}
}
