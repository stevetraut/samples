<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="javax.servlet.* %>
<%@ page import="javax.servlet.http.* %>
<%@ page import="java.util.* %>
<%@ page import="java.io.IOException %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>

<%
try {

    /* Grab the user name and customer number from the URL, where it was
        placed by the login form. */
    String userName = request.getParameter( "userName" );
    String customerNumber = request.getParameter( "customerNumber" );
    
    /* Create a variable to hold the web service. */
    String serverURL = "http://localhost:7001/BookProject/Chapter10/OnlineStorePoll.jws?WSDL";
    OnlineStorePoll_Impl storeServiceProxy = new OnlineStorePoll_Impl(serverURL);
    OnlineStorePollSoap storeProxy = storeServiceProxy.getOnlineStorePollSoap();
    session.setAttribute("proxy", storeProxy);

    java.rmi.server.UID uid = new java.rmi.server.UID();
    String iastr = null;
    try
    {
        java.net.InetAddress ia = java.net.InetAddress.getLocalHost();
        iastr = ia.getHostAddress();
    }
    catch(java.net.UnknownHostException uhe)
    {
        iastr = "uknownhost";
    }

    String guid = iastr + "-" + uid.toString();

    /*
      * Colons won't be accepted by the WebLogic 
      * Workshop runtime.
      */
    guid = guid.replace(':', '.');
    guid = guid.replace('[', '(');
    guid = guid.replace(']', ')');

    session.setAttribute("convID", guid);

    StartHeader sh = new StartHeader(convID, "http://localhost:7001/OnlineStorePoll.jws");

    /* 
     * Call the "start" method of the web service, officially starting
     * a conversation. Along with parameter's specified by the 
     * method itself, pass the start header so that the service
     * can begin a conversation with the conversation ID specified here.
     */
    String greeting = storeProxy.startShopping(userName, customerNumber, sh);

    response.sendRedirect( "displayCatalog.jsp" );
    
} catch ( java.rmi.RemoteException re )
{
    /*
     * tbd: notice that this catches RemoteException, then extracts
     * the message inside.
     */
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
