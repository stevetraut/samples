# WebLogic Workshop Samples

WebLogic Workshop began as an IDE for building SOAP-based web services. The IDE was developed by BEA Systems with the goal of making something tedious and difficult into something easier. Some of the IDE's designers came from Microsoft, including some from the Visual Basic team  (where I had also once worked). They wanted to create a visual programming experience that masked the existence of SOAP messages, envelopes, and so on.

In the IDE, a client was represented on the left, a backend resource on the right, and a design canvas in the middle illustrated logic between the client and backend. In the canvas, web service operations were coded as Java methods next to the client on the left. Interactions with external components were encapsulated on the right as "controls", similar to Visual Basic. Behind everything on the design canvas was Java code written by or generated for the developer. The code in these samples is that underlying Java.

Web service artifacts coded in Java had special file extensions -- .jws for the service, .ctrl for control interfaces, and so on.

I worked as a technical writer on the WebLogic Workshop team from 2002 to 2007. This directory contains three samples from a book I co-authored in 2003: [WebLogic Workshop: Building Next Generation Web Services Visually](https://www.amazon.com/BEA-WebLogic-Workshop-Building-Generation/dp/076451797X/ref=tmm_pap_swatch_0?_encoding=UTF8&qid=1536104874&sr=).

## BookProject-08

The chapter 8 sample illustrates how to use controls designed to interact with external Enterprise JavaBeans and JMS queues. The [`Chapter08`](BookProject-08/Chapter08) directory contains web service (.jws) and control (.ctrl) Java files that implement the web service.

## BookProject-10

The chapter 10 sample illustrates how to develop a client that communicates asynchronously with a web service built with WebLogic Workshop. 

- The [`Chapter10`](BookProject-10/Chapter10) directory contains web service (.jws) and control (.ctrl) Java files implementing the web service. 
- The [`Chapter10` > `ui`](BookProject-10/Chapter10/ui) directory contains the client code.
- The [`beanSources10`](BookProject-10/beanSources10) directory contains code for EJBs used by the web service.

## BookProject-11

The chapter 11 sample illustrates how to use JavaScript to transform XML from one shape to another (an incoming XML message might not conform to the shape expected by the web service). The JavaScript code uses an early version of ECMAScript for XML (E4X).

- The [`Chapter11`](BookProject-11/Chapter11) directory contains a web service (.jws) file implementing the web service and JavaScript (.jsx) files for handling XML. 
