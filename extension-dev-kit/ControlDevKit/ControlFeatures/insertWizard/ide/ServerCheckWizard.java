package insertWizard.ide; 

import com.bea.ide.control.ControlWizardSimple;
import javax.swing.JComponent;
import java.util.ArrayList;
import com.bea.ide.control.EditorContext;

/*
 * The insert-wizard-class attribute in the jc-jar file for this
 * control project specifies that this is the class WebLogic Workshop
 * should use to provide a custom insert wizard.
 * 
 * This class extends ControlWizardSimple, which provides methods through
 * which WebLogic Workshop retrieves the user interface to use, the 
 * annotation values to insert into the control's container, and so on. 
 */
public class ServerCheckWizard extends ControlWizardSimple
{ 
    private ServerCheckWizardPanel m_panel;
    private boolean _createExtension = false;
    private  EditorContext _context;

    /* 
     * Initializes the user interface. UI components
     * are assembled in the ServerCheckWizardPanel class.
     */
    public ServerCheckWizard ()
    {
        m_panel = new ServerCheckWizardPanel();
    }

    /*
     * Tells WebLogic Workshop whether to display an option
     * to create a JCX file. The constant returned here
     * tells it that an instance is all that is needed.
     */
    public int getConfigurationInfo()
    {
        return CONFIG_INSERT_INSTANCE;
    }
    /**
     * Gives the wizard a way to interact with the IDE.
     */
    public void setContext(EditorContext ctx)
    {
        _context = ctx;
        m_panel.setContext(ctx);
    }

    /**
     * Provides a way for subclasses to get the context as needed.
     */
    public EditorContext getContext()
    {
        return _context;
    }

     /*
     * Returns the user interface component to use.
     */
    public JComponent getComponent()
    {
        return m_panel;
    }

    /*
     * Provides a place to execute code when the control's user
     * clicks the "Create" button on the insert dialog.
     */
    public boolean onFinish()
    {
        if (super.onFinish() == false){
            return false;
        } else {
            return true;
        }
    }

    /*
     * Provides to WebLogic Workshop an ArrayList containing
     * the annotations it should write preceding the control
     * instance.
     */
	public ArrayList getInstanceAnnotations()
	{
        // An ArrayList for the annotation attributes. There are four.
        ArrayList attributeList = new ArrayList();
        // An ArrayList for this control's two annotations.
        ArrayList annotationsList = new ArrayList();

        /*
         * The attribute ArrayList is filled with TagAttributeValue
         * instances containing attribute name/value mappings. These
         * are used below for the jc:server-data annotation.
         * 
         * Attribute values are retrieved from the insert dialog box UI.
         */
        attributeList.add(new TagAttributeValue("server-name", m_panel.getServerName()));
        attributeList.add(new TagAttributeValue("url", m_panel.getServerURL()));
        attributeList.add(new TagAttributeValue("user-name", m_panel.getUserName()));
        attributeList.add(new TagAttributeValue("password", m_panel.getPassword()));
        
        /*
         * The annotation ArrayList is filled with TagAttributeList instances
         * containing the annotation name/attribute-list mappings.
         * Note that the second annotation uses the attribute ArrayList.
         */
        annotationsList.add(new TagAttributeList("common:control", null));
        annotationsList.add(new TagAttributeList("jc:server-data", attributeList));
		return annotationsList;
	}
} 
