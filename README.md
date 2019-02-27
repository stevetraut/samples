# Samples

This repository contains sample code connected with projects I've participated in. 

Generally, the code in these directories is "for display purposes only". That is, it is intended to be a view-only snapshot -- I haven't included the context and peripheral files needed for the code to run. Much of it requires support long-gone resources anyway.

## api-baas

The [`api-baas`](api-baas) directory contains three samples, each illustrating roughly the same functionality in the Apigee API BaaS product ([slated for EOL](https://docs.apigee.com/release/notes/api-baas-eol) in June 2019). Each sample is coded to use a different API BaaS SDK supporting a mobile platform -- Android, IOS, and JavaScript.

API BaaS is backend-as-a-service designed to support mobile apps. It includes a NoSQL data store and other services such as push notifications and geolocation. The samples were designed to illustrate how you could use API BaaS as a backend by incorporating the SDKs into your mobile apps.

These samples are snapshots from code I wrote in 2013; they each have later versions running on later SDKs. A form of API BaaS lives on as the open source project [Usergrid](http://usergrid.apache.org/), from which API BaaS originated.

## extension-dev-kit

The [`extension-dev-kit`](extension-dev-kit) directory contains code illustrating how to extend the WebLogic Workshop development environment. WebLogic Workshop was an IDE that began as a cockpit for building SOAP web services, and later supported building other kinds of J2EE components.

Using the two kits in the Extension Dev Kit, developers could develop custom components with which developers could build server-side apps, or could extend the WebLogic Workshop IDE, which was based on Java Swing.

## jsptagrefdoclet

The ['jsptagrefdoclet'](jsptagrefdoclet) directory contains the source code for a Javadoc doclet to generate reference for custom JSP tags. At the time, there weren't any doc generating tool that could combine the richness of Javadoc comments in the Java code backing the tags with the tag-and-attributes nature of the tags themselves. This one does that.

## weblogic-workshop

The [`weblogic-workshop`](weblogic-workshop) directory contains three samples from a book I co-authored in 2003: [WebLogic Workshop: Building Next Generation Web Services Visually](https://www.amazon.com/BEA-WebLogic-Workshop-Building-Generation/dp/076451797X/ref=tmm_pap_swatch_0?_encoding=UTF8&qid=1536104874&sr=).

These samples illustrate (for Workshop developers) how to use J2EE features, write web service client code, use JavaScript (via E4X) to manipulate XML.

WebLogic Workshop began as an IDE for building SOAP-based web services. The samples here require WebLogic Workshop 7.1, which is almost surely no longer available.

## xmlbeans

The ['xmlbeans'](xmlbeans) directory contains code for samples I wrote for the XMLBeans open source project. XMLBeans was a Java-native way to handle XML, with or without schema.s