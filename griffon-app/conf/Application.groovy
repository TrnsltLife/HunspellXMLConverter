application {
    title = 'HunspellXML'
    startupGroups = ['hunspellXML']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "about"
    'about' {
        model      = 'hunspellxml.AboutModel'
        view       = 'hunspellxml.AboutView'
        controller = 'hunspellxml.AboutController'
    }

    // MVC Group for "about"
    'about' {
        model      = 'hunspellxml.AboutModel'
        view       = 'hunspellxml.AboutView'
        controller = 'hunspellxml.AboutController'
    }

    // MVC Group for "hunspellXML"
    'hunspellXML' {
        model      = 'hunspellxml.HunspellXMLModel'
        view       = 'hunspellxml.HunspellXMLView'
        controller = 'hunspellxml.HunspellXMLController'
    }

}
