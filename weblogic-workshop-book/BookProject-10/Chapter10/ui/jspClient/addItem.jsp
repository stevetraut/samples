<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.Item" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>
<%
/* 
 * This page is designed to invoke the web service's addItem
 * method, passing the request item code and the continue header.
 */
try {
    OnlineStorePollSoap storeProxy = 
        (OnlineStorePollSoap)session.getAttribute("proxy");
	
    ContinueHeader ch = new ContinueHeader((String)session.getAttribute("convID"));

    String codeString = request.getParameter("code");
    Integer codeInteger = new Integer(codeString);
    int code = codeInteger.parseInt(codeString);

    storeProxy.addItem(code, ch);

    response.sendRedirect("viewCart.jsp");
    
} catch ( java.rmi.RemoteException re )
{
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
} catch ( Exception e )
{
    session.setAttribute( "errMsg", e.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
