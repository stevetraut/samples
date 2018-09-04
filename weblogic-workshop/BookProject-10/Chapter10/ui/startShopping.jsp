<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="javax.servlet.* %>
<%@ page import="javax.servlet.http.* %>
<%@ page import="java.util.* %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>
<%@ page errorPage="errorPage.jsp?debug=log" %>
<% request.setAttribute("sourcePage", request.getRequestURI()); %>

<%
try {

    /* 
     * Grab the user name and customer number from the URL, where 
     * it was placed by the login form. 
     */
    String userName = request.getParameter("userName");
    String customerNumber = request.getParameter("customerNumber");
    
    /* 
     * Create a variable to hold the web service proxy. This
     * code follows a predictable format. For example, the 
     * generated proxy name is always the name of the service 
     * with "_Impl" appended. The name of the SOAP proxy is always 
     * the name of the service with "Soap" appended. This is the 
     * component that does the actual work of translating requests 
     * and responses to and from SOAP-style XML messages. To make 
     * the code easier to understand, we'll use "storeProxy" as the 
     * variable name in each page.
     */
    String serviceURL = 
        "http://localhost:7001/BookProject/Chapter10/OnlineStorePoll.jws?WSDL";
    OnlineStorePoll_Impl storeImpl = 
        new OnlineStorePoll_Impl(serviceURL);
    OnlineStorePollSoap storeProxy = 
        storeImpl.getOnlineStorePollSoap();

    /* 
     * Store the newly created proxy object in the Session object. 
     * There, it will be available to other pages in the application,
     * so that it won't need to be created for each new page that 
     * communicates with the web service.
     */
    session.setAttribute("proxy", storeProxy);

    /*
     * Create a random string that may be used as a conversation
     * ID throughout the course of this purchase.
     */
    java.rmi.server.UID uid = new java.rmi.server.UID();
    String iastr = null;
    try
    {
        java.net.InetAddress ia = 
            java.net.InetAddress.getLocalHost();
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

    /* Store the conversation ID in the Session object. */
    session.setAttribute("convID", guid);

    /* 
     * Create an instance of the StartHeader class.
     * The start header will carry the conversation ID
     * to start this conversation. It will also carry the 
     * callback URL, the URL to which the OnlineStore web
     * service will send its callback containing the outcome
     * of the transaction. Notice that the callback URL
     * points to the wrapper web service. The results will
     * be stored there, then retrieved by a JSP page designed
     * to poll until results are available.
     */
    StartHeader sh = new StartHeader(guid, 
        "http://localhost:7001/OnlineStorePoll.jws");

    /* 
     * Call the "start" method of the web service, officially 
     * starting a conversation. Along with parameters specified 
     * by the method itself, pass the start header so that the 
     * service can begin a conversation with the conversation 
     * ID specified here.
     */
    String greeting = storeProxy.startShopping(userName, 
        customerNumber, sh);
    
    /* 
     * After starting the conversation, move to the displayCatalog.jsp
     * page, which is designed to display a list of items.
     */
    response.sendRedirect( "displayCatalog.jsp" );
    
} catch ( java.rmi.RemoteException re )
{
    /*
     * Catch an exception that may be thrown by the web service.
     * Note that the exception arrives as a RemoteException. From 
     * this, you can extract the actual exception stored inside. 
     * This code stores the contents of the exception in an "errMsg" 
     * attribute where it will be accessible from the error page.
     * This isn't the best way to handle exceptions in production,
     * but it can be useful while you're debugging.
     */
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
