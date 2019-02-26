<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page errorPage="errorPage.jsp?debug=log" %>
<% request.setAttribute("sourcePage", request.getRequestURI()); %>

<!-- 
Users log in from this page. Clicking the Start Shopping
button advances processing to startShopping.jsp.
-->
<html>
<head>
<link rel="stylesheet" href="onlinestore.css" type="text/css">
<title>Widgets R Us Login</title>
<script language="JavaScript">
    function setFocus(){ 
        document.requestInfoForm.userName.focus(); 
    }
</script>
</head>
<body bgcolor="white" onLoad="setFocus()">
<form method="get" action="startShopping.jsp"
                    name="requestInfoForm">
  <table width="100%">
    <tr> 
      <td> 
        <h1>Widgets R Us Online Store</h1>
        <h4>Please sign in to start shopping!</h4>
      </td>
    </tr>
    <tr> 
      <td colspan="5"> 
        <p>Enter your user name: <br>
          <input tabindex="1" type="text" name="userName" size="30">
        </p>
      </td>
    </tr>
    <tr> 
      <td colspan="5"> 
        <p>Enter your customer number: <br>
          <input tabindex="2" type="text" name="customerNumber" 
            size="30">
        </p>
      </td>
    </tr>
    <tr> 
      <td colspan="6"> 
        <p><input tabindex="3" type="submit" value="Start Shopping!" 
            name="Submit"></p>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
