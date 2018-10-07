package insertWizard; 

import com.bea.control.*;


public interface ServerCheck extends Control
{ 

    /**
     * The getDomainName method is the control's only operation. It uses an external class, MBeanUtil, to get the server's domain name.
     * @common:operation
     */
    java.lang.String getDomainName();
} 
