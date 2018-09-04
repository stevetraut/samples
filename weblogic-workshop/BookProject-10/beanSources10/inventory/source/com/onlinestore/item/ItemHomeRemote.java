package com.onlinestore.item;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

/* See ItemBean.java for the logic of this EJB. */
public interface ItemHomeRemote extends javax.ejb.EJBHome {

    public ItemRemote create(Integer code)
        throws CreateException, RemoteException;

    public ItemRemote findByPrimaryKey(Integer pk)
        throws FinderException, RemoteException;
}
