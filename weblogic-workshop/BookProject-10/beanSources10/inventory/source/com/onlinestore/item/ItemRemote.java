package com.onlinestore.item;

import java.rmi.RemoteException;

/* See ItemBean.java for the logic of this EJB. */
public interface ItemRemote extends javax.ejb.EJBObject {   
	public abstract void setCode(Integer code) throws RemoteException;
	public abstract Integer getCode() throws RemoteException;
	public abstract void setName(String name) throws RemoteException;
	public abstract String getName( ) throws RemoteException;
	public abstract void setPrice(Double price) throws RemoteException;
	public abstract Double getPrice( ) throws RemoteException;

}
