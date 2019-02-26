# Web service for client sample

This directory contains code for the web service portion of the web service client sample. This is the web service whose operations the client calls.

This code includes logic for the web service, a "online store" service with operations for viewing a catalog, managing a shopping cart, and so on. 

This code includes a wrapper web service that does not send callbacks to its clients. It is intended to support clients, such as JSP pages, that are unable to receive callbacks. The wrapper (OnlineStorePoll.jws) service uses a service control to make calls to the service it wraps (OnlineStore.jws), then holds the results for the client to retrieve.

Directory | Description
--------- | -----------
[`Chapter10` > `ui`](Chapter10/ui) | Contains the client code.

File | Description
--------- | -----------
[`CreditCheckControl.ctrl`](CreditCheckControl.ctrl) | A JMS control for requesting credit checks.
[`InventoryControl.ctrl`](InventoryControl.ctrl) | An EJB control for retrieving items from inventory.
[`OnlineStore.jws`](OnlineStore.jws) | A web service for shopping online.
[`OnlineStoreControl.ctrl`](OnlineStoreControl.ctrl) | A service control making calls to the OnlineStore.jws web service.
[`OnlineStorePoll.jws`](OnlineStorePoll.jws) | A wrapper web service for OnlineStore.jws.
