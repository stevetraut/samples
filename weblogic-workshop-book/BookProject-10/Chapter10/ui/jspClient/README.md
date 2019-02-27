# WebLogic Workshop Book Chapter 10 JSP Client

This directory contains source code for a JavaServer Pages client to the [OnlineStore](../../OnlineStorePoll.jws) web service. This code assumes you're already familiar with JavaServer Pages.

For more on WebLogic Workshop-specific conventions used in this code, see the [client code readme](../README.md).

Files | Description
--------- | -----------
[`addItem.jsp`](addItem.jsp) | Sends a request to add an item to the cart.
[`checkout.jsp`](checkout.jsp) | Sends a request to check out with purchases.
[`displayCatalog.jsp`](displayCatalog.jsp) | Retrieves and displays a list of catalog items.
[`errorPage.jsp`](errorPage.jsp) | Just a page to display in case of error.
[`index.html`](index.html) | An index page.
[`login.jsp`](login.jsp) | A login page.
[`onlinestore.css`](onlinestore.css) | Styles for the client UI.
[`removeItem.jsp`](removeItem.jsp) | Sends a request to remove an item from the cart.
[`startShopping.jsp`](startShopping.jsp) | Begins to conversation with the web service.
[`validateUser.jsp`](validateUser.jsp) | Collects user information to begin the conversation.
[`viewCart.jsp`](viewCart.jsp) | Retrieve and displays the list of items in the cart.
[`viewResults.jsp`](viewResults.jsp) | Polls the web service to discover the results of the checkout response.
