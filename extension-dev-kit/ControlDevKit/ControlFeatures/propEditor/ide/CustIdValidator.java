package propEditor.ide; 

import com.bea.control.Issue;
import com.bea.control.ValidateAttribute;
import java.util.Map;

public class CustIdValidator implements ValidateAttribute
{ 
    int legalValues[] = { 987654, 987655, 987658, 987659 };
    
    static class CustIdIssue implements Issue
    {
        String _message;
        private CustIdIssue(String message) { _message = message; }
        public boolean isError() { return true; }
        public String getDescription() { return _message; }
        public String getPrescription() 
        { 
            return "Provide one of the following values: " +
             "987654, 987655, 987658, 987659. that's all"; 
        } 
    }
    
    // called by the attribute editor.
    public Issue[] validateId(String value)
    {
         return validateDuringCompile(null, value, null);   
    }

    public Issue[] validateDuringCompile(String attributeType, String value, Map context)
    {
            Issue[] issues = new Issue[1];
            int val;
            try 
            {
                val = Integer.parseInt(value);
            }
            catch(Exception e)
            {
                issues[0] = new CustIdIssue(e.getMessage());
                return issues;
            }
            

        for(int i = 0; i < legalValues.length; i++)
        {
            if(legalValues[i] == val)
            {
                return null;
            }
       }            
    
        issues[0] = new CustIdIssue("unsupported value error");
        return issues;
    }
    
    public Issue[] validateDuringEdit(String attributeType, String value)
    {
        return null;
    }
} 
