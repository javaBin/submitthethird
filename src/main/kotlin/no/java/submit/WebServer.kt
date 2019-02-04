package no.java.submit

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp.WebAppContext
import java.io.File
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory


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

    //setuphttps(server);

    server.handler = createHandler()
    server.start();
    println("Server running")
}

fun setuphttps(server: Server) {
    //if (!Setup.forceServerHttps()) {
    //    return;
    //}
    // Create HTTP Config
    val httpConfig = HttpConfiguration()

    // Add support for X-Forwarded headers
    httpConfig.addCustomizer(org.eclipse.jetty.server.ForwardedRequestCustomizer())

    // Create the http connector
    val connectionFactory = HttpConnectionFactory(httpConfig)
    val connector = ServerConnector(server, connectionFactory)

    // Make sure you set the port on the connector, the port in the Server constructor is overridden by the new connector
    connector.setPort(Setup.serverPort())

    // Add the connector to the server
    server.connectors = arrayOf<ServerConnector>(connector)
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
