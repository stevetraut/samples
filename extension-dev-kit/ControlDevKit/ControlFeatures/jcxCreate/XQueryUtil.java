package jcxCreate; 

import com.bea.xml.XmlCursor;
import java.lang.reflect.InvocationTargetException;
import com.bea.xml.XmlObject;

/*
 * Represents XQuery functionality to the XQuery control.
 */
public class XQueryUtil 
{ 
    /*
     * Returns an XMLBeans XmlCursor object containing the results of
     * the query. If results are not possible, perhaps because the expression
     * was incorrect, the method throws an exception.
     */
    public XmlCursor runXQueryExpression(XmlObject xml, String expression)
        throws InvocationTargetException, Exception
    {
        XmlCursor queryResults = null;
        try{
            XmlCursor itemCursor = xml.newCursor().execQuery(expression);
            queryResults = itemCursor;
        } catch (Exception e){
            throw e;
        }
        return queryResults;
    }
} 
