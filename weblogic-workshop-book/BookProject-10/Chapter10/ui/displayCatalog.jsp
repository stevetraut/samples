<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.Item" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>
<%
try {
    /* 
     * Retrieve the web service proxy from the Session object, where
     * it was placed by the startShopping.jsp page.
     */
    OnlineStorePollSoap storeProxy = 
        (OnlineStorePollSoap)session.getAttribute("proxy");

    /* 
     * Prepare a continue header. This will be sent with new requests
     * to the web service. Notice that it contains the same
     * conversation ID that was created and used to start the
     * conversation.
     */
    ContinueHeader ch = 
        new ContinueHeader((String)session.getAttribute("convID"));
%>
<html>
<head>
<link rel="stylesheet" href="onlinestore.css" type="text/css">
<title>Widgets R Us Catalog</title>
</head>
<body>
<h1>Widgets R Us Online Store</h1>
<h4>Our Current Catalog</h4>
<%
        /* 
         * Get a list of the catalog items by calling the
         * web service's viewCatalog method. Note that the method
         * call passes in the conversation header even though the 
         * method (as defined by the web service) doesn't include
         * this parameter. This is a convenience offered by the
         * proxy, enabling you to send a conversation ID without
         * having had to account for it in your service interface.
         * the header parameter is automatically made the last
         * parameter of each conversational method.
         */
        Item[] catalogItems = storeProxy.viewCatalog(ch);
        
        /* 
         * Use the returned array of items to build a table that
         * displays their names. The table also displays a button
         * that users can click to purchase the item. Notice that 
         * expressions are used to insert values from the Item 
         * class. Finally, notice that the entire table is contained
         * by a form whose action is the addItem.jsp page. This
         * provides a way to respond to the user's request to 
         * buy a particular item by passing the code of the
         * item to the addItem.jsp page. The addItem page has code
         * that can request that the item be added to the shopping
         * cart managed by the web service.
         */
        if (catalogItems.length > 0){
%>
<table width="401">
  <tr> 
    <td width="80"> <div align="left"> </div></td>
    <td width="120"> <div align="left"><b>Item Name</b></div></td>
    <td width="80"><b>Item Price</b> <div align="left"></div></td>
    <td width="100"> <div align="left"><b>Item Code</b></div></td>
  </tr>
  <tr>
    <td colspan=4><hr noshade size="1"/></td>
  </tr>
  <%
            for (int i = 0; i < catalogItems.length; i++){ 
                Item currentItem = catalogItems[i];
%>
  <form method="get" action="addItem.jsp" name="catalogList">
    <tr> 
      <td width="80"> <div align="left"> 
          <input type="submit" value="Buy Me"/>
        </div></td>
      <td width="120"> <div align="left"><%= currentItem.getName() %></div></td>
      <td width="80"> <%= currentItem.getPrice() %> <div align="left"></div></td>
      <td width="100"> <div align="left">Code: <%= currentItem.getCode() %></div></td>
      <input type="hidden" name="code" value="<%= currentItem.getCode() %>"/>
    </tr>
  </form>
  <% 
        }
%>
</table>
<%
    } else {
%>
<p>Looks like we're sold out for now! Please check back later.</p>
<%		
    }
} catch ( java.rmi.RemoteException re )
{
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
</body>
</html>
