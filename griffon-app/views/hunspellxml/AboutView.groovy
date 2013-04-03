package hunspellxml

import javax.imageio.*

actions {
    action(id: 'cancelAction',
       name: 'Cancel',
       closure: controller.hide,
       mnemonic: 'C',
       shortDescription: 'Cancel'
    )
    action(id: 'okAction',
       name: 'Ok',
       closure: controller.hide,
       mnemonic: 'K',
       shortDescription: 'Ok'
    )
}

panel(id: 'content') {
    borderLayout()
	label(text:"hello world")
    //imageIcon('/HunspellXML-splash.png')
	jximagePanel(id:"image", image:ImageIO.read(app.getResourceAsStream("HunspellXML-splash.png")))
    panel(constraints: SOUTH) {
        gridLayout(cols: 1, rows: 2)
		label(text:"Version " + Metadata.current["app.version"])
        button(okAction)
    }
    
    keyStrokeAction(component: current,
        keyStroke: "ESCAPE",
        condition: "in focused window",
        action: cancelAction)
}
