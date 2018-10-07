package jcxCreate.ide; 

import java.text.MessageFormat;
import com.bea.ide.control.ControlWizardSimple;
import javax.swing.JComponent;

/*
 * Represents an insert dialog for the XQuery control. While
 * this dialog doesn't provide any special user interface, it
 * does provide the text that the IDE should insert into
 * newly created JCX files.
 */
public class XQueryWizard extends ControlWizardSimple
{ 
    private boolean m_createExtension = false;

    String m_jcxPackage = null;
    String m_jcxName = null;

    /*
     * Tell the IDE how to configure the insert dialog. This dialog
     * should provide an option to use an existing JCX file, or to
     * create a new one.
     */
    public int getConfigurationInfo()
    {
        return CONFIG_CREATE_EXTENSION_FILE |
            CONFIG_INSERT_INSTANCE;
    }

    /*
     * Provide a place for the IDE to tell this code whether the 
     * user has asked to create a new JCX or not.
     */
    public void setConfiguration(int config)
    {
        m_createExtension =
            ((config & ControlWizardSimple.CONFIG_CREATE_EXTENSION_FILE) != 0);
    }

    /*
     * Tell the IDE what to use for custom insert dialog user interface.
     * None is needed here.
     */
    public JComponent getComponent()
    {
        return null;
    }

    /*
     * Provide a place for code that should execute when the user clicks the
     * Create button on the insert dialog.
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
     * Provides a way for the IDE to pass in the package name
     * that should be specified at the top of a new JCX file.
     */
    public void setPackage(String packageName)
    {
        m_jcxPackage = packageName;
    }

    /*
     * Provides a way for the IDE to pass in the name of the
     * interface defined in a new JCX file.
     */
    public void setName(String name)
    {
        m_jcxName = name;
    }

    /*
     * Provides a place for the IDE to retrieve the content that it
     * should insert into newly created JCX files. Here, the package name
     * and interface name passed in by the IDE are inserted into a 
     * template using the MessageFormat class.
     */
    public String getExtensionFileContent(){
        // did they ask for an extension?
        String jcxTemplate = this.getTemplate();

        String jcxContent = 
            MessageFormat.format(jcxTemplate, new Object[] {m_jcxPackage, m_jcxName});
            
        return jcxContent;
    }
    
    /*
     * Returns a template for text to be inserted into a new JCX file.
     */
    private String getTemplate(){
        String template = 
            "package {0}; \n\n " +
            "import com.bea.control.*; \n " +
            "import com.bea.xml.XmlCursor; \n " +
            "import jcxCreate.XQuery; \n\n " +
            
            "public interface {1} extends XQuery, com.bea.control.ControlExtension \n " +
            "'{' \n \n" +

            "    /* \n" +
            "     * A version number for this JCX. This will be incremented in new versions of \n" +
            "     * this control to ensure that conversations for instances of earlier \n" +
            "     * versions were invalid. \n" +
            "     */ \n" +
            "    static final long serialVersionUID = 1L; \n \n" +

            "    /* Replace the following method with your own. Be sure \n" +
            "     * that your method returns an XmlCursor and that its \n" +
            "     * parameter is a File object to contain the XML against \n" +
            "     * which to execute the XQuery expression. \n" +
            "     */ \n \n" +

            "    /** \n" +
            "     * @jc:query expression=\"$this/purchase-order/line-item[price <= 20.00]\" \n" +
            "     */ \n" +
            "     XmlCursor selectLineItem(String filePath); \n \n" +

            "'}' \n ";
        return template;
    }

} 
