<%@ page contentType="text/html; charset=iso-8859-1" %>
<%@ page isErrorPage="true" %>

<% String supportMailTo = "mailto:support@widgetsrus.com"; %>
<!-- 
This page is designed to let the user know something
unexpected has happened.
-->
<html>
    <head>
        <link rel="stylesheet" href="onlinestore.css" type="text/css">
        <title>Widgets R Us Error Page</title>
    </head>
    <body>
        <h1>Oops.</h1>
        <p>Looks like our online store ran into a little glitch while
           you were shopping with us. We hope you'll take a moment to
           send us email with a description of the problem to: 
            <a href="<%= supportMailTo %>>Widgets R Us support staff</a>.</p>
        <p>Internal specifics about the error:</p>
        <p><%= session.getAttribute("errMsg") %></p>
    </body>
</html>
