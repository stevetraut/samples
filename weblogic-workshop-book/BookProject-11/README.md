# WebLogic Workshop Book Chapter 11 Sample

This directory contains a sample illustrating how to use JavaScript to access and create XML for use with a web service. The sample features an enhanced version of WebLogic Workshop XML maps. With an XML map, you could transform XML between the shapes used by the client and shapes expected by the web services. In the enhanced variety, the XML map called out to JavaScript that handled the transformation.

JavaScript XML maps in WebLogic Workshop had two features that made it easier to transform between XML and the Java types known to the web service: support for Java types in JavaScript and an early version of ECMAScript for XML (E4X). In JavaScript, you could import the Java types that containing data as the web service handled it. E4X (deprecated in 2014) was a way to handle XML as a native JavaScript type. The combination of these two features made it easier to transform between Java and XML using accessors only.

Chapter 11 was called, "Using JavaScript in XML Maps".

| Directory | Description |
| --------- | ----------- |
| [`Chapter11`](Chapter11) | Contains all of the sample code -- JavaScript, an instance of the XML to use, and the web service code. |
