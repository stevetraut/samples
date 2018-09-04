package com.onlinestore.inventory;

import com.onlinestore.item.ItemRemote;
import com.onlinestore.item.ItemHomeRemote;
import com.onlinestore.item.Item;
import java.rmi.RemoteException;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.ejb.EJBException;
import java.util.Properties;
import java.util.ArrayList;

/* 
 * The Inventory EJB is a session bean. This is the bean that the OnlineStore service's 
 * EJB control communicates with. This bean receives requests widget inventory, 
 * then looks up available widgets as entity beans (ItemHomeRemote). 
 * After finding all of the available items, this Inventory bean creates an 
 * instance of an Item class, assigns values from the Item beans to the Item
 * class, then returns the array of Item class instances to the web service.
 */
public class InventoryBean implements javax.ejb.SessionBean {

	public void ejbCreate() {
		// No code.
   	}

   	public Item[] listItems() {
        	try {
            		Properties props = new Properties();
            		props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
            		props.put(Context.PROVIDER_URL, "t3://localhost:7001");
            		javax.naming.Context jndiContext = new InitialContext(props);
            		Object obj = 
                   		jndiContext.lookup("java:comp/env/ejb/ItemHomeRemote");

            		ItemHomeRemote home = (ItemHomeRemote)javax.rmi.PortableRemoteObject.narrow(obj,ItemHomeRemote.class);

			ArrayList list = new ArrayList();

			for (int i = 0; ; i++) {
	        		Integer primKey = new Integer(i);
	         		ItemRemote itemBean = null;
	              		try {
	                		itemBean = home.findByPrimaryKey(primKey);
	                	} catch(javax.ejb.FinderException fe) {
	                		System.out.println("InventoryBean exception: "+ fe.getMessage() +" for primKey=" + i); 
	                   		break;
	                	}
				Item newItem = new Item();
				
				newItem.setName(itemBean.getName());
				newItem.setCode(itemBean.getCode());
				newItem.setPrice(itemBean.getPrice());			
				list.add(newItem);
	                }
             
	                Object[] objectList = list.toArray(new Item[0]);
	                Item[] itemList = (Item[])objectList;
		     	return itemList;

       		} catch(Exception e) { throw new EJBException(e); }    
   	}

   	public void ejbRemove(){}
   	public void ejbActivate(){}
   	public void ejbPassivate(){}
   	public void setSessionContext(javax.ejb.SessionContext cntx){}
}
