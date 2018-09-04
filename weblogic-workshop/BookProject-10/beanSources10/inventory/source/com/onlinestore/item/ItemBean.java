package com.onlinestore.item;

import javax.ejb.EntityContext;

/* 
 * This is the Item bean that the Inventory bean
 * uses to look up information about available widgets.
 */
public abstract class ItemBean 
implements javax.ejb.EntityBean {

	public Integer ejbCreate(Integer code){
		this.setCode(code);
		return null;
	}
	public void ejbPostCreate(Integer code){
		
	}
	public abstract void setCode(Integer code);
	public abstract Integer getCode();
 
	public abstract void setName(String name);
	public abstract String getName( );

	public abstract void setPrice(Double price);
	public abstract Double getPrice( );

    public void setEntityContext(EntityContext ctx) {
         // Not implemented.
    }
    public void unsetEntityContext() {
         // Not implemented.
    }
    public void ejbActivate() {
        // Not implemented.
    }
    public void ejbPassivate() {
        // Not implemented.
    }
    public void ejbLoad() {
        // Not implemented.
    }
    public void ejbStore() {
        // Not implemented.
    }
    public void ejbRemove() {
        // Not implemented.
    }
}
