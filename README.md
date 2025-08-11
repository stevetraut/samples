# Samples

This repository contains sample code I wrote in connection with projects I've participated in.

Generally, the code in these directories is "for display purposes only". That is, it's intended to be a view-only snapshot -- I haven't included the context and peripheral files needed for the code to run. Much of it requires support from long-gone resources anyway.

## Code for mobile apps accessing a backend-as-a-service

The [api-baas](api-baas) directory contains three samples, each illustrating roughly the same functionality in the Apigee API BaaS product ([declared at end-of-life](https://docs.apigee.com/release/notes/api-baas-eol) in June 2019). Each sample is coded to use a different API BaaS SDK supporting a mobile platform -- Android, iOS, and JavaScript.

API BaaS was a backend-as-a-service designed to support mobile apps. It included a NoSQL data store and other services such as support for push notifications and geolocation. The samples were designed to illustrate how developers could use API BaaS as a backend by incorporating the SDKs into their mobile apps.

These samples are snapshots from code I wrote in 2013. A form of API BaaS is still visible in the Apache Attice as the open source project [Usergrid](https://attic.apache.org/projects/usergrid.html), from which API BaaS originated.

## Samples for extending an IDE

The [`extension-dev-kit`](extension-dev-kit) directory contains code illustrating how to extend the WebLogic Workshop development environment. WebLogic Workshop was an IDE that began as a cockpit for building SOAP web services, then later supported building other kinds of J2EE components.

Using the two kits in the Extension Dev Kit, developers could develop custom components with which developers could build server-side apps, or could extend the WebLogic Workshop IDE, whose code was based on [Java Swing](https://en.wikipedia.org/wiki/Swing_(Java)).

## Java doclet for generating JSP tag references

The [`jsptagrefdoclet`](jsptagrefdoclet) directory contains the source code for a Javadoc doclet to generate reference for custom JSP tags. When I wrote this, there weren't any doc-generating tools that could combine the richness of Javadoc comments in the Java code backing the tags with the tag-and-attributes nature of the tags themselves. This one does that.

## Samples from a published book on building web services

The [`weblogic-workshop-book`](weblogic-workshop-book) directory contains three samples from a book I co-authored in 2003: [WebLogic Workshop: Building Next Generation Web Services Visually](https://www.amazon.com/BEA-WebLogic-Workshop-Building-Generation/dp/076451797X/ref=tmm_pap_swatch_0?_encoding=UTF8&qid=1536104874&sr=).

These samples illustrate (for Workshop developers) how to use J2EE features, write web service client code, and use JavaScript (via [E4X](https://en.wikipedia.org/wiki/ECMAScript_for_XML) to manipulate XML.

These samples in their complete form would require WebLogic Workshop 7.1, which is almost surely no longer available.

## Samples for working with XML in Java

The [`xmlbeans`](xmlbeans) directory contains code for samples I wrote for the XMLBeans open source project. XMLBeans is a (now deprecated) Java-native way to handle XML.
