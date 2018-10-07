package propEditor; 

import com.bea.control.*;

public interface CustomerData extends Control
{ 

    /**
     * Returns the customer's company name using the nested Database control.
     * @common:operation
     */
    java.lang.String getCustomerName();

    /**
     * Returns a list of items the customer has ordered. The database includes tables for customers, items, items by order, and customers by order. This is method nests queries against these tables to produce joined results.
     * @common:operation
     */
    java.util.ArrayList getItemsOrdered();
} 
