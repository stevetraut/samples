package jcxCreate.ide; 

import com.bea.ide.control.ControlAttribute;
import com.bea.ide.control.EditorSupport;
import com.bea.ide.control.ControlMethod;
import com.bea.ide.control.ControlBehaviorContext;
import com.bea.ide.control.DefaultEditorSupport;

/*
 * Represents IDE support for XQuery controls added to a project.
 * An "editor support" class specifies various "behaviors" within the IDE.
 * These include what to do when the user double-clicks on control methods, 
 * when the user attempts to add or edit methods and callbacks, and what icon 
 * to display on a method.
 */
public class XQueryEditorSupport extends DefaultEditorSupport
{ 
    /*
     * Provides a way for WebLogic Workshop to retrieve the behavior associated
     * with specified actions in the IDE. Each action has a corresponding 
     * constant in the EditorSupport interface, which is implemented by the 
     * DefaultEditorSupport class this class extends. A ControlBehaviorContext
     * argument specifies whether the context of the action is a method, attribute,
     * interface, instance, and so on.
     */
    public Object getBehavior(String behavior, ControlBehaviorContext context)
    {
        /*
         * If the user asks to edit a JCX method, return true.
         */
        if (behavior.equals(EditorSupport.BEHAVIOR_EDIT_METHOD))
        {
            return Boolean.TRUE;
        }
        /*
         * If the user asks to edit a callback, return false. XQuery JCX files do
         * not support callbacks.
         */
        else if (behavior.equals(EditorSupport.BEHAVIOR_EDIT_CALLBACK))
        {
            return Boolean.FALSE;
        }
       /*
         * If the user clicks an edit ... in the Property Editor, and if the attribute
         * corresponding to the ... is expression, return an instance of the expression
         * edit dialog box.
         */
        else if (behavior.equals(EditorSupport.BEHAVIOR_ATTRIBUTE_EDITOR))
        {
            if (context instanceof ControlAttribute && ((ControlAttribute)context).getName().equals("expression"))
            {
                return new QueryExpressionEditorSimple(((ControlAttribute)context).getValue());
            }            
        }
        return super.getBehavior(behavior, context);
    }
} 
