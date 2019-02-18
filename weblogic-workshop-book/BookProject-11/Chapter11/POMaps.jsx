/*
 * Import Java classes needed for translation. These
 * classes happen to be defined in the web service 
 * itself. Note that the import statement available 
 * with JavaScript in WebLogic Workshop does not support
 * importing entire packages with the * symbol. You must
 * specify classes by name.
 */
import Chapter11.POTranslator.Address;
import Chapter11.POTranslator.Item;
import Chapter11.POTranslator.PurchaseOrder;

/* 
 * Declare the function for translating from incoming
 * XML to Java. This kind of function's name must end with 
 * the suffix "FromXML". Its parameter is the incoming
 * XML.
 */
function translatePurchaseOrderFromXML ( poXML )
{
    /* 
     * Declare an array variable to later hold data
     * from <item> elements in the incoming XML 
     * message. Also, declare Address variables to 
     * hold the message's <shippingAddress> and
     * <billingAddress> values.
     */
    var items = new Array();
    var shippingAddr = new Address();
    var billingAddr = new Address();

    /* 
     * Extract shipping and billing address information 
     * from the XML message and assign them to separate
     * XML variables.
     */ 
    var shippingAddressXML = poXML..shippingAddress;
    var billingAddressXML = poXML..billingAddress;

    /* 
     * Assign values in the separate shipping and
     * billing address XML variables to fields of 
     * separate the Address objects.
     */
    shippingAddr.name = shippingAddressXML.name;
    shippingAddr.street = shippingAddressXML.street;
    shippingAddr.city = shippingAddressXML.city;
    shippingAddr.state = shippingAddressXML.state;
    shippingAddr.zip = shippingAddressXML.zip;

    billingAddr.name = billingAddressXML.name;
    billingAddr.street = billingAddressXML.street;
    billingAddr.city = billingAddressXML.city;
    billingAddr.state = billingAddressXML.state;
    billingAddr.zip = billingAddressXML.zip;

    /* 
     * Collect XML for all of the <item> elements
     * into a single XML list that may be used in 
     * a loop.
     */
    var itemsXML = poXML..item;

    /* 
     * Create a new Item object for each of the elements
     * in the <item> element list. Assign each <item> 
     * element's children's values to a field of the Item 
     * object.
     */
    var i = 0;
    for ( var itemXML in itemsXML )
    {
        var newItem = new Item();

        newItem.name = itemXML.name;
        newItem.price = itemXML.price;
        newItem.quantity = itemXML.quantity;

        items[i] = newItem;

        i++;
    }
    
    /* 
     * Assign the PO number from the orderNumber attribute
     * to a variable.
     */
    var poNumber = poXML.@orderNumber;
    
    /* 
     * Return the two Address objects and the Item
     * array for use as parameters of the submitOrder
     * method.
     */
    return [ shippingAddr, billingAddr, poNumber, items]
}  

/* 
 * Declare the function for translating from the method's
 * Java return value to outgoing XML. This kind of function 
 * must end with the suffix "ToXML". It may have multiple
 * parameters, depending on how the Java data it receives is
 * structured.
 */
function translatePurchaseOrderToXML ( purchaseOrder )
{
    /* 
     * Declare an XML variable to serve as a root. The 
     * remaining code in this function will build on this
     * variable to generate the outgoing XML message
     * from values in the submitOrder return value
     * captured in the purchaseOrder parameter of this
     * function.
     */
    var poXML = <purchaseOrder/>;

    /* 
     * Declare XML variables to hold the XML for shipping and 
     * billing address information.
     */
    var shippingAddressXML = <shippingAddress>
        <name/><street/><city/><state/><zip/></shippingAddress>;
    var billingAddressXML = <billingAddress>
        <name/><street/><city/><state/><zip/></billingAddress>;

    /* 
     * Assign values from the Address objects contained by the 
     * incoming PurchaseOrder object to corresponding XML.
     */
    shippingAddressXML..name = purchaseOrder.shippingAddr.name;
    shippingAddressXML..street = purchaseOrder.shippingAddr.street;
    shippingAddressXML..city = purchaseOrder.shippingAddr.city;
    shippingAddressXML..state = purchaseOrder.shippingAddr.state;
    shippingAddressXML..zip = purchaseOrder.shippingAddr.zip;

    billingAddressXML..name = purchaseOrder.billingAddr.name;
    billingAddressXML..street = purchaseOrder.billingAddr.street;
    billingAddressXML..city = purchaseOrder.billingAddr.city;
    billingAddressXML..state = purchaseOrder.billingAddr.state;
    billingAddressXML..zip = purchaseOrder.billingAddr.zip;

    /* 
     * Append the new <shippingAddress> and <billingAddress>
     * elements (and their children) to the <purchaseOrder> root 
     * element.
     */
    poXML.purchaseOrder.appendChild(shippingAddressXML);
    poXML.purchaseOrder.appendChild(billingAddressXML);

    /* 
     * For each Item object in the PurchaseOrder object returned
     * by the submitOrder method, declare a new XML variable. 
     * The syntax used here is efficient in that it uses few 
     * lines of code. In a single statement this code declares 
     * a variable containing four elements and assigns a value 
     * to each using embedded expressions.
     *
     * However, compressing so much into a single statement
     * can make it hard to debug. For example, the debugger
     * will only stop at a breakpoint set at the top of the
     * statement. If you were trying to track down an error
     * related to just one of the values, this could be
     * awkward. Still, it's useful to see what is possible.
     */
    for ( var i in purchaseOrder.items ) 
    {
        var elementItem = <item>
            <name>{purchaseOrder.items[i].name}</name>
            <price>{purchaseOrder.items[i].price}</price>
            <quantity>{purchaseOrder.items[i].quantity}</quantity>
        </item>;
        
        /* Append the newly created <item> element to the root. */
        poXML.purchaseOrder.appendChild(elementItem);
    }
    
    /* Assign the PO number submitted by the requestor. */
    poXML.purchaseOrder.@orderNumber = purchaseOrder.orderNumber;

    /* 
     * Use the XML variable containing the <item> elements to
     * calculate the total cost of the purchase, as well as 
     * the total quantity of items ordered.
     */
    var sumOfPurchase = 0;
    var sumOfQuantity = 0
    for ( item in poXML..item )
    {
        var itemTotal = item.price * item.quantity;
        sumOfPurchase += new Number(itemTotal);
        sumOfQuantity += new Number(item.quantity);
    }

    /* 
     * Declare an XML variable to hold the information calculated 
     * from item values. Note that this XML was not part of the 
     * XML request message sent by the client.
     */
    var summaryXML = <summary><totalCost/><averagePrice/></summary>;
    summaryXML..totalCost = sumOfPurchase;
    summaryXML..averagePrice = sumOfPurchase / sumOfQuantity;

    /* 
     * Append the <summary> element (with its children) to the 
     * <purchaseOrder> root element.
     */
    poXML.purchaseOrder.shippingAddress += summaryXML;

    /* Return the newly generated XML for return to the client. */
    return poXML;
}    
