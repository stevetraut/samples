package com.onlinestore.inventory;

import java.rmi.RemoteException;
import javax.ejb.CreateException;

/* See InventoryBean.java for the logic of this EJB. */
public interface InventoryHomeRemote extends javax.ejb.EJBHome {

    public InventoryRemote create()
        throws RemoteException, CreateException;

}
