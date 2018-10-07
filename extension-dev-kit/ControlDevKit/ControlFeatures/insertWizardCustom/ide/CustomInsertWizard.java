package insertWizardCustom.ide; 

import com.bea.ide.Application;
import com.bea.ide.control.ControlWizard;
import com.bea.ide.control.EditorContext;
import com.bea.ide.filesystem.FileSvc;
import com.bea.ide.filesystem.IFile;
import com.bea.ide.ui.IURISelectionContext;
import com.bea.ide.util.URIUtil;
import com.bea.ide.util.swing.DialogUtil;
import com.bea.ide.workspace.WorkspaceSvc;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * A control wizard implementation that returns a full dialog to prompt
 * the control's user for information when they are inserting or creating
 * the control. Contrast this with the insertWizard.ide.ServerCheckWizard
 * class in this application, which returns only a panel that will be
 * included in a dialog constructed by the IDE. The full dialog that
 * this implementation uses is defined as insertWizardCustom.ide.CustomInsertDialog.
 * 
 * When implementing ControlWizard, you are responsible for prompting the
 * user for control instance and JCX names, and for validating the names that
 * the user specifies.
 * 
 * Note that a ControlWizard implementation does not support allowing the user
 * to specify an existing JCX file -- a new file must always be created.
 */
public class CustomInsertWizard extends ControlWizard
{ 
    protected String m_variableName = null;
    protected String m_jcxName = null;
    protected String m_jcxPackageName = null;
    protected String m_currentProjectPath = null;
    protected URI m_activeDocumentUri = null;
    private EditorContext m_context = null;
    private ControlWizard.NameValidator m_validator = null;
    protected boolean m_okayToInsert = false;
    protected boolean m_nameHasBeenSet = false;
    protected boolean m_packageHasBeenSet = false;
    protected int m_requestedConfig = -1;
    private Frame m_container = null;
    
    public CustomInsertDialog m_insertDialog;

    /**
     * Called by the IDE to get possible configurations.
     */
    public int getConfigurationInfo()
    {
        return (CONFIG_CREATE_EXTENSION_FILE | CONFIG_INSERT_INSTANCE);
    }

    /**
     * Called by the IDE to set the insert configuration
     * requested by the user.
     * 
     * @param config A sum of the ControlWizardSimple configuration constants that
     * represent the user's insert request.
     */
    public void setConfiguration(int config)
    {
        m_requestedConfig = config;
    }

    /**
     * Called by the IDE to get the custom dialog to show.
     * 
     * @return The insert dialog to display.
     */
    public Dialog getDialog(Frame container)
    {
        m_insertDialog = new CustomInsertDialog(container, this);
        m_container = container;
        return m_insertDialog;        
    }

    /**
     * Called by the IDE to get the name of the interface defined
     * in the JCX.
     * 
     * @return The control JCX and interface name.
     */
    public String getExtensionName()
    {
        m_jcxName = m_insertDialog.m_jcxName;
        return m_jcxName;
    }

    /**
     * Called by the IDE to get the control instance name specified by 
     * the user in the insert dialog.
     * 
     * @return The control instance name specified by the user.
     */
    public String getInstanceName()
    {
        m_variableName = m_insertDialog.m_variableName;
        return m_variableName;
    }

    /**
     * Called by the IDE to set the package name. This value is
     * used in the JCX template's <code>package</code> statement.
     * 
     * @param packageName The package name.
     */
    public void setPackage(String packageName)
    {
        if (!m_packageHasBeenSet)
        {
            if (m_activeDocumentUri != null)
            {
                String uriPath = m_activeDocumentUri.getPath();
                if (uriPath.startsWith("/"))
                {
                    uriPath = uriPath.substring(uriPath.indexOf("/") + 1);
                }
            }
            if (packageName.endsWith("/"))
            {
                packageName = packageName.substring(0, packageName.lastIndexOf("/"));
            }
            if (packageName.endsWith(".jcx"))
            {
                packageName = packageName.substring(0, packageName.lastIndexOf("/"));
            }
            if (packageName.indexOf("/") > 0)
            {
                if (packageName.endsWith("/"))
                {
                    packageName = packageName.substring(0, packageName.lastIndexOf("/"));
                }
                packageName = packageName.replaceAll("/", ".");
            }
            m_jcxPackageName = packageName;
            m_packageHasBeenSet = true;
        }
    }
    
    /**
     * Displays a yes/no dialog. Used to ask the user if they want to choose a folder
     * for their JCX.
     * 
     * @return <code>true</code> if the user selected "Yes" in the dialog; <code>false</code>
     * if they selected "No".
     */
    private boolean showPackageAlert()
    {
        String alertText = "A Java control must reside in a subfolder of the " +
            "project. Do you want to choose a subfolder in " +
            "which to create this control?";                    
        return DialogUtil.showYesNoDialog(m_container, alertText);
    }

    /**
     * Called to set the name of the JCX file to create.
     * 
     * @param name The name specified by the user.
     */
    public void setName(String name)
    {
        if (!m_nameHasBeenSet)
        {
            m_jcxName = name;
            m_nameHasBeenSet = true;
        }
    }

    /**
     * Called by the IDE to pass in a validator that can be used to validate
     * the names of control variable and JCX names.
     * 
     * @param validator The validator instance.
     */
    public void setNameValidator(ControlWizard.NameValidator validator)
    {
        m_validator = validator;
    }

    /**
     * Gets the validator passed in by the IDE. This is called from the 
     * insert dialog.
     * 
     * @return The validator instance.
     */
    public ControlWizard.NameValidator getNameValidator()
    {
        return m_validator;
    }

    /**
     * Called by the IDE just before it inserts the control.
     * 
     * @return <code>true</code> if the control may be inserted; otherwise, <code>false</code>.
     */
    public boolean onFinish()
    {
        if (m_insertDialog.m_controlInsertRequested)
        {
            if (!m_nameHasBeenSet)
            {
                m_jcxName = m_insertDialog.m_jcxName;
            }
            m_okayToInsert = true;
        }
        m_insertDialog.dispose();
        return m_okayToInsert;
    }

    /**
     * Called by the IDE to pass in an EditorContext instance that contains
     * information about, for example, the current project.
     * 
     * @param context Details about the context into which this control
     * is being inserted.
     */
    public void setContext(EditorContext context)
    { 
        m_context = context;
        m_currentProjectPath = URIUtil.getFilePath(URIUtil.fromFile(m_context.getProjectDir()));
        if (Application.getActiveDocument() != null)
        {
            m_activeDocumentUri = Application.getActiveDocument().getURI();
        }
    }

    /**
     * Provides a place for the IDE to retrieve the content that it
     * should insert into newly created JCX files. Here, the package name
     * and interface name passed in by the IDE are inserted into a 
     * template using the MessageFormat class.
     * 
     * @return The text of the JCX file to insert.
     */
    public String getExtensionFileContent()
    {
        String jcxTemplate = this.getTemplate();
        String jcxContent = 
            MessageFormat.format(jcxTemplate, new Object[] {m_jcxPackageName, m_jcxName});
        return jcxContent;
    }
    
    /**
     * Returns a template for text to be inserted into a new JCX file.
     * 
     * @return The template.
     */
    private String getTemplate()
    {
        String template = 
            "package {0}; \n\n" +
            
            "public interface {1} extends com.bea.control.ControlExtension, insertWizardCustom.CustomWiz \n " +
            "'{' \n\n" +
            "    static final long serialVersionUID = 1L; \n\n" +
            "  \n\n" + 
            "    public String echoInput(String input); \n\n" + 
            "'}' \n ";
        return template;
    }
} 
