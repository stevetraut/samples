package ideExtensions.customProject; 

import com.bea.ide.Application;
import com.bea.ide.core.MessageSvc;
import com.bea.ide.filesystem.IFile;
import com.bea.ide.workspace.IProject;
import com.bea.ide.workspace.IWorkspace;
import com.bea.ide.workspace.project.IProjectDriver;

/**
 * A project driver loaded for each project of the PHP project type.  
 * A project driver implements IProjectDriver, whose two methods,
 * activate and deactivate, provide a way for you to associate code
 * with the start and end of a project's use in an application. 
 * 
 * This sample shows a typical use of these methods: adding and removing
 * listeners. By adding a property event listener in the driver's
 * activate method and removing it in the deactivate method, you 
 * associate the listener with the project for the duration of 
 * the project's use. In this way you can listen for IDE-, 
 * application- and document-related events and changes to properties
 * defined by WebLogic Workshop.
 */
public class PhpProjectDriver implements IProjectDriver
{ 
    IProject m_project = null;
    PhpProjectListener m_listener = null;

    /**
     * Constructs a new instance of this driver using the 
     * property with which this instance is associated. A
     * project driver must provide a constructor that takes
     * an IProject instance. If multiple projects of this type
     * are included in the application, then multiple instances
     * of this driver will be created, one for each project.
     * 
     * @param project The project with which this driver instance
     * is associated.
     */
    public PhpProjectDriver(IProject project)
    {
        m_project = project;
        /**
         * Create a listener with the project associated with
         * this instance of the driver.
         */ 
        m_listener = new PhpProjectListener(m_project);
    }
    
    /**
     * Called by the IDE when a project of this type is activated.
     */
    public void activate()
    {
        /**
         * Add a listener that will be used for changes to the PROP_InitLevel
         * property. This property's value changes to track progress of the IDE's 
         * startup.
         */
        Application.get().addPropertyChangeListener(Application.PROP_InitLevel, m_listener);           
    }
    
    /**
     * Called by the IDE when a project of this type is deactivated.
     */
    public void deactivate()
    {
        /**
         * Remove the listener that was added when this project 
         * type was activated.
         */
        Application.get().removePropertyChangeListener(m_listener);
    }
} 
