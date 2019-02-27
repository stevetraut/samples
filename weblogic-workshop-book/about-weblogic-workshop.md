# About WebLogic Workshop

WebLogic Workshop samples in this repository represent idiosyncratic product design decisions that can make the code a bit hard to understand. This topic aims to make that a little easier with some background about the product.

WebLogic Workshop began as an IDE for building SOAP-based web services. The IDE was developed by BEA Systems with the goal of making SOAP service development, which was often tedious and difficult, into something easier. Some of the IDE's designers came from Microsoft, including some from the Visual Basic team (where, coincidentally, I had also once worked). They wanted to create a graphical programming experience that masked the existence of SOAP messages, envelopes, and so on. They wanted the experience to be accessible in a Visual Basic-easy way, and aimed their product at Java programmers who weren't J2EE programmers.

In the IDE's design UI, a client application was represented on the left, a backend resource on the right. A design canvas in the middle illustrated logic between the client and backend. In the canvas, you coded web service operations as Java methods that appeared on the left, next to the client. Interactions with external components were encapsulated on the right as "controls", similar to the controls in Visual Basic. Behind everything on the design canvas was Java code written by or generated for the developer. At run time, all of the communication implemented in Java was translated into SOAP operations.

## Web service implementation in Java

The core of a WebLogic Workshop web service was a file with a .jws extension (for Java web service). The Java code in a .jws file implemented logic that should execute when a web service operation was invoked. If the web service included communication with backend resources, those resources could be represented in code as calls to methods of a control.

The [OnlineStore.jws](BookProject-08/Chapter08/OnlineStore.jws) web service implements a simple store service, with logic to handle a catalog and shopping cart, along with calls out to the Java Message Service for a credit check and to a database for inventory tracking.

## Controls to encapsulate functionality

As with controls in Visual Basic (the world from which WebLogic Workshop designers had come), controls in WebLogic Workshop encapsulated functionality. For example, a developer building a web service might add the built-in JMS control to interact with the Java Message Service, setting control properties that specified a JMS queue. Several controls were included.

In code, the design-time artifact for a control was a Java interface with a .ctrl extension. A developer set properties on the control, and the properties were coded as annotations on the interface and its methods. From web service code, the developer called control methods to access its functionality.

You could also build your own controls. Your custom control was built from a Java interface, such as [CustomerData.java](../extension-dev-kit/ControlDevKit/ControlFeatures/propEditor/CustomerData.java) and a class that implemented the interface, such as [CustomerDataImpl.jcs](../extension-dev-kit/ControlDevKit/ControlFeatures/propEditor/CustomerDataImpl.jcs).

## Annotations to represent properties

WebLogic Workshop introduced its own annotations to capture metadata used to configure web services and interactions with backend resources. 

In the following example from [CustomerDataTest.jws](../extension-dev-kit/ControlDevKit/ControlTest/featuresTests/CustomerDataTest.jws), the `@common:operation` annotation signals that the `getCustName` method is a web service operation.

```java
/**
 * Returns the customers name.
 * 
 * @common:operation
 */
public String getCustName()
{
    return thisCustomer.getCustomerName();
}
```

Below, the `@common:control` annotation signals that the `thisCustomer` variable points to a control. The `@jc:customer-db` annotation specifies a value for the customer-id attribute. Both the annotation and the attribute are defined in [CustomerData-tags.xml]().

```java
/**
 * @common:control
 * @jc:customer-db customer-id="987659"
 */
private propEditor.CustomerData thisCustomer;
```

## Callbacks and conversations

Web services supported asynchronous communication through callbacks. A stateful sequence of requests and responses between a client, a web service, and resources accessed via controls was called a *conversation*. Requests and responses within a conversation were correlated through a conversation ID created and maintained by the server.

Methods on a web service or control were annotated to indicate which started a conversation, which continued it, and which finished it. When a conversation finished, its ID and state were discarded.

In the following example from [OnlineStore.jws](), the `startShopping` method is annotated to indicate that calls to it start a conversation. State data such as the customer ID and shopping cart contents are maintained through subequent conversational calls to other methods.

```java
/**
 * @jws:operation 
 * @jws:conversation phase="start"
 */
public String startShopping(String name, String customerNumber)
{
    customerID = customerNumber;
    this.name         = name;
    this.shoppingCart = new ArrayList();    
    return "Welcome, " + name;
}
```

## XML maps

So that developers could transform XML from one shape to another, WebLogic Workshop  included support for a feature called XML maps. An XML map specified how, say, an incoming XML message could be transformed into the XML shape supported by the web service. Outgoing XML could be transformed the same way.

For more complex transformations, you could use JavaScript linked from the map. In JavaScript in a `.jsx` file, you could handle XML through imported Java classes (such as classes generated from schema with XMLBeans) and through an early version of E4X.

For an example, see [POMap.jsx]().

## File types

Most of the code in these samples is Java that implemented web service logic. In WebLogic Workshop, each web service artifact had a file extension that signaled the file's role in the project.

File extension | Type | Description
-------------- | ---- | -----------
.jws | Java class | Web service implementation file.
.ctrl | Java interface | The file design-time file generated from a control. A .crtl file gives a web service developer a way to set properties on a control.
.jcs| Java class | A control implementation file. A custom control includes a .jcs file for implementation and a .java file for the control interface.
.java| Java control interface | Though a .java file can be what it usually is in the Java world. in WebLogic Workshop it can specifically be the source interface for a Java control. A .jcs file is the control implementation file.
.jsx | JavaScript functions | A JavaScript file containing XML mapping code.
