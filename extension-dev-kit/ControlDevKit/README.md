# Control Developer's Kit

The Control Developer's Kit was an SDK designed to help Java programmers extend WebLogic Workshop by creating their own custom controls for use in the IDE. In WebLogic Workshop, controls encapsulated functionality so that a web service developer could more easily add features to a service. A control could encapsulate access a data store or other resource. Developing a custom control meant writing its logic in Java, but also adding support for design-time properties, user interface, and so on. (For my overview of the IDE, see [About WebLogic Workshop](../weblogic-workshop-book/about-weblogic-workshop.md)).

I developed the SDK in 2004 while working as a technical writer for BEA Systems. BEA was later acquired by Oracle. For documentation content from the SDK, see the [Extension Development Kit PDF](../ExtensionDevelopmentKit.pdf).

This part of the SDK included:

- [ControlFeatures](ControlFeatures) -- Samples that each illustrate a particular aspect of custom control development.
- [ControlTest](ControlTest) -- A set of SOAP web services (implemented as WebLogic Workshop .jws files) that illustrate how the sample controls in this kit work.
