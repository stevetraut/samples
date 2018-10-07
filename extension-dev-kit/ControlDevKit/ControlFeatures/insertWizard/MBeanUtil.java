package insertWizard; 

import weblogic.management.MBeanHome;
import weblogic.management.Helper;

/*
 * Represents the server's MBean to the control.
 */
public class MBeanUtil 
{ 
    public static MBeanHome getMBean(String userName, String password, String serverURL, 
        String serverName) throws IllegalArgumentException
    {
        MBeanHome requestedMBean = null;
        try { 
            requestedMBean = (MBeanHome)Helper.getMBeanHome(userName, password, 
                serverURL, serverName);
        } catch (IllegalArgumentException iae) {
            throw iae;
        }
        return requestedMBean;
    }    
} 
