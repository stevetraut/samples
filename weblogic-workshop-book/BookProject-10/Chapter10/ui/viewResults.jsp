<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page import="org.openuri.www.Item" %>
<%@ page import="weblogic.jws.proxies.*" %>
<%@ page import="org.openuri.www.x2002.x04.soap.conversation.*" %>
<%
try {
    OnlineStorePollSoap storeProxy = 
        (OnlineStorePollSoap)session.getAttribute("proxy");

    ContinueHeader ch = 
        new ContinueHeader((String)session.getAttribute("convID"));
%>
<html>
<head>
<link rel="stylesheet" href="onlinestore.css" 
            type="text/css">
<title>Widgets R Us Purchase Confirmation</title>
<script language="JavaScript1.2">

            /* 
             * Use two JavaScript functions to do the polling.
             * The startTimer function specifies that the 
             * refresh function should be called every second.
             * When the refresh function is called, it requests
             * the viewResults.jsp page again, effectively
             * causing the Java code in the page to execute. 
             * Because the code calls the isPurchaseComplete
             * method, the page will keep refreshing until
             * the method returns "true".
             * 
             * Note that the page refresh happens because the
             * page's body onload event invokes the startTimer 
             * function if isPurchaseComplete doesn't return
             * "true". If it the method returns "true", then 
             * a different <body> tag is used -- one that does
             * not call the startTimer function.
             */
            function startTimer()
            {
                var refreshTimer = setTimeout("refresh()", 1000);
            }
    
            function refresh()
            {
                window.location.reload(false);
            }
        </script>
</head>
<%
    /* 
     * Call the web service's isPurchaseComplete method to 
     * see if it has finished processing the purchase
     * request.
     */
    if(storeProxy.isPurchaseComplete(ch)) {
%>
<body>
<%
        /* 
         * If the purchase processing is complete, check to
         * see if any items were returned. If not, it means
         * that the customer's credit was denied, and an 
         * appropriate message should be displayed. If the
         * collection was returned, then displayed the contents
         * of the array in a table.
         */ 
        Item[] items = storeProxy.getResults(ch);
        if (items != null) {
%>
<h1>Widgets R Us Online Store</h1>
<p>Thanks! Here are the results of your purchase.</p>
<table width="401">
  <tr> 
    <td width="120"> <div align="left"><b>Item Name</b></div></td>
    <td width="80"><b>Item Price</b> <div align="left"></div></td>
    <td width="100"> <div align="left"><b>Item Code</b></div></td>
  </tr>
  <tr>
    <td colspan=3><hr noshade size="1"/></td>
  </tr>
  <%
                for (int i = 0; i < items.length; i++){ 
                    Item currentItem = items[i];
%>
  <tr> 
    <td width="120"> <div align="left"> <%= currentItem.getName() %> </div></td>
    <td width="80"> <div align="left"> <%= currentItem.getPrice() %> </div></td>
    <td width="100"> <div align="left"> Code: <%= currentItem.getCode() %> </div></td>
  </tr>
  <% 
            }
%>
</table>
<%
        } else {
        /* 
         * If an array of items was not returned by the web 
         * service, it means that the customer's credit 
         * wasn't accepted.
         */
%>
<h1>Widgets R Us Online Store</h1>
<p>Sorry. It looks as though your credit has been declined.</p>
<%
        }
%>
</body>
<%
    } else {
%>
<!-- 
        The startTimer is called (in other words, polling begins)
        only if the isPurchaseComplete method returned "false".
    -->
<body onload="startTimer()">
<h1>Widgets R Us Online Store</h1>
<p><b>Please wait while we process your order.</b></p>
</body>
</html>
<%
    }
} catch ( java.rmi.RemoteException re )
{
    session.setAttribute( "errMsg", re.detail.getMessage() );
    response.sendRedirect( "errorPage.jsp" );
}
%>
