package ideExtensions.customProject; 

import com.bea.ide.Application;
import com.bea.ide.core.MessageSvc;
import com.bea.ide.filesystem.IFile;
import com.bea.ide.util.swing.DialogUtil;
import com.bea.ide.workspace.IProject;
import com.bea.ide.workspace.IWorkspace;
import com.bea.ide.workspace.project.IProjectInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * A property change listener to listen for IDE startup
 * and display a message about PHP projects in the application.
 * 
 * Note that this listener will only be used when the IDE has just
 * finished starting up, and its message will only be displayed if 
 * there is at least one PHP project in the application. If you 
 * have not yet done so, the easiest way to execute this code
 * is to begin debugging the CustomProject application. That will
 * build the extension and start a new instance of the IDE with
 * the extension added.
 */
public class PhpProjectListener implements PropertyChangeListener
{ 
    IProject m_phpProject = null;
    
    public PhpProjectListener (IProject phpProject)
    {
        m_phpProject = phpProject;
    }    
    
    /**
     * Called by the IDE on changes to properties with which this
     * listener is associated. The PhpProjectDriver associates
     * this listener with changes to the Application.PROP_InitLevel
     * property. That property's value changes to track the 
     * IDE's startup progress. 
     * 
     * If the property's new value is Application.INIT_StartupComplete,
     * indicating that the IDE has finished starting up and is ready
     * for use, this listener will display an informational dialog
     * listing the contents of the PHP projects in this application.
     * The dialog will be displayed once for each project of the PHP
     * type in the application -- each, after all, has its own instance
     * of the driver by which this listener was created.
     * 
     * Needless to say, this would be annoying in a real-world
     * scenario, but it suggests the potential of a listener.
     * 
     * @param event The event with which this listener is
     * associated.
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        IWorkspace workspace = Application.getWorkspace();

        /**
         * If the received event is "startup complete", put together
         * a message and display it in a dialog.
         */
        if (event.getNewValue().equals(Application.INIT_StartupComplete))
        {
            /**
             * Get an array of projects whose types have an extension.xml 
             * containing an <attribute> element whose name attribute value is
             * "supportsPhp". Note that the IProjectInfo interface provides
             * other methods for retrieving the contents of the extension.xml
             * file for the project type.
             */ 
            IProject[] phpProjects = workspace.getProjects("supportsPhp");

            // A string buffer to contain an assembled message.            
            StringBuffer messageBuffer = new StringBuffer();

            // Add the message's introduction.
            messageBuffer.append("Information about a PHP project in this application: \n\n");
            
            /**
             * Get the IProjectInfo for this project. An instance of this
             * interface contains information stored in the extension.xml
             * for the project type.
             */
            IProjectInfo projectInfo = m_phpProject.getProjectInfo();

            // Get the type's URN.
            String typeId = projectInfo.getTypeId();
            
            /**
             * Confirm that the project really is a PHP project as 
             * specified by the project type URN given in the project 
             * type's extension.xml file. This is unnecessary because this 
             * listener is only associated with PHP projects by the project
             * driver, but the code shows how you can get the ID.
             */
            if (typeId.equals("urn:com-bea-ide:project.type:php"))
            {                
                // Collect the project's name, path, and contents.
                String projectName = m_phpProject.getName();
                String projectPath = m_phpProject.getPath();
                IFile projectIFile = m_phpProject.getIFile();
                IFile[] projectIFiles = projectIFile.listIFiles();

                // Put project info into the message.
                messageBuffer.append("Name: " + projectName + "\n");
                messageBuffer.append("Path: " + projectPath + "\n");
                if (projectIFiles.length > 0)
                {
                    messageBuffer.append("Contains these directories and files:\n");

                    // Add to the message the name of each file in the project.
                    for (int j = 0; j < projectIFiles.length; j++)
                    {
                        messageBuffer.append("  " + projectIFiles[j].getName() + "\n");
                    }
                }
                messageBuffer.append("\n");
            } else {
                messageBuffer.append("No news.");
            }
            
            // Show the collected information.
            DialogUtil.showInfoDialog(Application.getRootFrame(), messageBuffer.toString());
        }
    }
} 
