package com.onlinestore.item;

/* 
 * This is the Item class through which the Inventory bean
 * passes widget data back to the OnlineStore service's EJB 
 * control.
 */
public class Item implements java.io.Serializable {

	private Integer m_code;
	private String m_name;
	private Double m_price;

	public void setCode(Integer code) {
		m_code = code;
	}
	
	public Integer getCode() {
		return m_code;
	}
 
	public void setName(String name) {
		m_name = name;
	}

	public String getName( ) {
		return m_name;
	}

	public void setPrice(Double price) {
		m_price = price;
	}
	
	public Double getPrice( ) {
		return m_price;
	}

        public Item()
        {}
        
        public Item(String name, Integer code, Double price)
        {
            this.setName(name);
            this.setCode(code);
            this.setPrice(price);
        }

}
