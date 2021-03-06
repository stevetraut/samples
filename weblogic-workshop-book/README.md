# WebLogic Workshop Book Samples

This directory contains three samples from a book I co-authored in 2003: [WebLogic Workshop: Building Next Generation Web Services Visually](https://www.amazon.com/BEA-WebLogic-Workshop-Building-Generation/dp/076451797X/ref=tmm_pap_swatch_0?_encoding=UTF8&qid=1536104874&sr=).

WebLogic Workshop began as an IDE for coding SOAP-based web services. Source artifacts were coded primarily in Java with IDE-specific file extensions. For more on the IDE and source artifacts, see [About WebLogic Workshop](about-weblogic-workshop.md).

I worked as a technical writer on the WebLogic Workshop team from 2002 to 2007. I contributed chapters 8, 10, and 11 as an "extracurricular" project. I like to joke that the WebLogic Workshop book received positive Amazon reviews from both of the people who read it.

Note that I've trimmed the sample file sets down to the code that I wrote, omitting artifacts (such as WEB-INF directories) that were either generated by the IDE or are unimportant to understanding what the code does. For example, given that these samples are from an old version of WebLogic Workshop, I've removed build files -- you probably wouldn't be able to use them anyway.

For samples from an SDK I developed for extending the WebLogic Workshop IDE, see the [Extension Dev Kit](../extension-dev-kit).

## BookProject-08

The chapter 8 samples illustrate how to use controls designed to interact with external Enterprise JavaBeans and JMS queues. The [`Chapter08`](BookProject-08/Chapter08) directory contains web service (.jws) and control (.ctrl) Java files that implement the web service. Chapter 8 was called, "Using the Advanced Controls".

## BookProject-10

The chapter 10 sample illustrates how to develop a client that communicates asynchronously with a web service built with WebLogic Workshop. Chapter 10 was called, "Calling Web Services from Outside WebLogic Workshop".

- The [`Chapter10`](BookProject-10/Chapter10) directory contains web service (.jws) and control (.ctrl) Java files implementing the web service. 
- The [`Chapter10` > `ui`](BookProject-10/Chapter10/ui) directory contains the client code.
- The [`beanSources10`](BookProject-10/beanSources10) directory contains code for EJBs used by the web service.

## BookProject-11

The chapter 11 sample illustrates how to use JavaScript to transform XML from one shape to another. This would be useful, for example, when an incoming XML message from a client didn't conform to the shape expected by the web service. The JavaScript code here uses a pre-standard version of [ECMAScript for XML (E4X)](https://en.wikipedia.org/wiki/ECMAScript_for_XML). Chapter 11 was called, "Using JavaScript in XML Maps".

- The [`Chapter11`](BookProject-11/Chapter11) directory contains a web service (.jws) file implementing the web service and JavaScript (.jsx) files for handling XML. 
