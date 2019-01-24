package no.java.submit

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp.WebAppContext
import java.io.File

var setupFile:String? = null
var submitTest:String? = null;

fun main(args: Array<String>) {
    if (args.size > 0) {
        if (File(args[0]).exists()) {
            setupFile = args[0]
        } else {
            submitTest = args[0]
        }

    }
    val server = Server(Setup.serverPort())
    server.handler = createHandler()
    server.start();
    println("Server running")
}

private fun createHandler(): WebAppContext {
    val webAppContext = WebAppContext()
    webAppContext.initParams["org.eclipse.jetty.servlet.Default.useFileMappedBuffer"] = "false"
    //webAppContext.sessionHandler.setMaxInactiveInterval(30)
    webAppContext.contextPath = "/"

    if (Setup.runAsJarFile()) {
        // Prod ie running from jar
        webAppContext.baseResource = Resource.newClassPathResource("webapp", true, false)
    } else {
        // Development ie running in ide
        webAppContext.resourceBase = "src/main/resources/webapp"
    }

    webAppContext.addServlet(ServletHolder(ApiServlet()), "/api/*")
    return webAppContext;
}
