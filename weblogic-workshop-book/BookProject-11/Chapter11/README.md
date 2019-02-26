# Chapter 11 Sample Sources

This directory contains all of the sample code -- JavaScript, an instance of the XML to use, and the web service code.

Calls to JavaScript map functions can be a little confusing. When transforming from XML to Java, the parameters in the function *call* are the names of parameters from the Java function for which the map is doing its work. Simply put, the syntax needed a way to specify which Java types to map the XML into, and this is the place where they're specified. In code for the JavaScript function itself, note that the return value is the same sequence of Java types.

I know. It's remarkable this idea didn't take off.

```
@jws:parameter-xml xml-map::
    <purchaseOrder xmlns="http://www.wiley.com/">
        {Chapter11.POMaps.translatePurchaseOrder(shippingAddr, billingAddr, poNumber, items)}
    </purchaseOrder>
::
public PurchaseOrder submitOrder(Address shippingAddr, Address billingAddr, String poNumber, Item[] items)
{ // }
```

File | Description
--------- | -----------
[`POMaps.jsx`](POMaps.jsx) | The JavaScript file that does the heavy lifting of translating between Java types and XML.
[`POTranslator.jws`](POTranslator.jws) | The web service from which the map script is called.
[`POXML.xml`](POXML.xml) | An example of the client XML received by the web service.
