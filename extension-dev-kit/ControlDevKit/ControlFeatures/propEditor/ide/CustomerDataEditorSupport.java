package propEditor.ide; 

import com.bea.ide.control.ControlBehaviorContext;
import com.bea.ide.control.EditorSupport;
import com.bea.ide.control.ControlAttribute;
import com.bea.ide.control.DefaultEditorSupport;


/*
 * Represents support for actions in the IDE. In particular, this
 * class provides code that executes when the user clicks the ... in the 
 * Property Editor to edit the customer-id attribute.
 */
public class CustomerDataEditorSupport extends DefaultEditorSupport
{ 
    public Object getBehavior(String behavior, ControlBehaviorContext ctx)
    {
        // if both editor and validator are specified only editor is used
        // TODO: validator example.
        if (behavior.equals(EditorSupport.BEHAVIOR_ATTRIBUTE_EDITOR))
        {
            if (ctx instanceof ControlAttribute && 
                ((ControlAttribute)ctx).getName().equals("customer-id"))
            {
                return new CustomerIdEditorSimple(((ControlAttribute)ctx).getValue());
            }            
        }        
        return super.getBehavior(behavior, ctx);
    }
    
 
} 
