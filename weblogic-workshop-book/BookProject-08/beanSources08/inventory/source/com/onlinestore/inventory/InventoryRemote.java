package com.onlinestore.inventory;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.onlinestore.item.ItemRemote;
import com.onlinestore.item.Item;

/* See InventoryBean.java for the logic of this EJB. */
public interface InventoryRemote extends javax.ejb.EJBObject {

    public Item[] listItems() throws RemoteException;
}
