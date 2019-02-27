# WebLogic Workshop Book Chapter 8 Client Code

This directory contains the source code for web service clients in JavaServer Pages and Java Swing. These samples illustrate how to write code native to the client technology while supporting WebLogic Workshop's requirements for asynchronous SOAP-based messages.

For more on WebLogic Workshop idiosyncrasies, see [About WebLogic Workshop](../../../about-weblogic-workshop.md).

## Supporting asynchronous requests

A client's interaction with a web service could include multiple calls. For example, in this online store sample, the client makes separate requests to get inventory, add items to a cart, and check out. In WebLogic Workshop, multiple requests in a single session were known as a *conversation*. To correlate requests from a given client with state maintained by the web service, WebLogic Workshop used a *conversation ID*. A client generated a unique identifier as the conversation ID, then sent that ID with each request in a conversational exchange.

## Converting formats

To support ease of use, WebLogic Workshop included features to make it easier for clients coded in Java-native technologies such as JavaServer Pages and Java Swing to interact with web services via SOAP. For example, a WebLogic Workshop developer could use the IDE to download a proxy component to represents the web service to the client. Through a proxy, a Java client could call the web service’s methods as if it were calling them directly in the Java way. The proxy was responsible for packaging the client’s requests and sending them in the appropriate form to the web service.

Directory | Description
--------- | -----------
[`jspClient`](jspClient) | Source for a client written in JavaServer Pages.
[`swingClient`](swingClient) | Source for a client written in Java Swing.
