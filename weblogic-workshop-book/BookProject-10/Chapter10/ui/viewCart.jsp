<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="org.openuri.www.Item" %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>
<%
/* 
 * This page is designed to display the current state of the user's
 * shopping trip. It retrieves a list of the items in the cart so
 * far, then displays them in a table. Users can also remove items
 * from this page.
 */
try {
    OnlineStorePollSoap storeProxy = 
        (OnlineStorePollSoap)session.getAttribute("proxy");
	
    ContinueHeader ch = new ContinueHeader((String)session.getAttribute("convID"));
%>
<html>
<head>
<link rel="stylesheet" href="onlinestore.css" type="text/css">
<title>Widgets R Us Shopping Cart</title>
</head>
<body>
<h1>Widgets R Us Online Store</h1>
<h4>Your Shopping Cart</h4>
<%
                Item[] catalogItems = storeProxy.viewCart(ch);
                if (catalogItems.length > 0){
                    for (int i = 0; i < catalogItems.length; i++){ 
                        Item currentItem = catalogItems[i];
%>
<form method="get" action="removeItem.jsp" name="catalogList">
  <table width="200">
    <tr> 
      <td width="96"> <input type="submit" value="Remove Me"/> </td>
      <td width="110"><b><%= currentItem.getName() %></b></td>
      <td width="123"><%= currentItem.getPrice() %></td>
      <td width="232">Code: <%= currentItem.getCode() %></td>
      <input type="hidden" name="index" value="<%=i %>"/>
    </tr>
  </table>
</form>

  <% 
                }
%>
<p><a href="checkout.jsp">Check out</a></p> 
  <%
                }
                else {
%>
<p>Looks like your cart is empty.</p>
<%		
                }
} catch ( java.rmi.RemoteException re )
{
    /*
     * tbd: notice that this catches RemoteException, then extracts
     * the message inside.
     */
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
} catch ( Exception e )
{
    session.setAttribute( "errMsg", e.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
</body>
</html>
