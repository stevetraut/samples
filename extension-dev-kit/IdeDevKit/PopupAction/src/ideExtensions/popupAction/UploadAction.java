package ideExtensions.popupAction;

import com.bea.ide.Application;
import com.bea.ide.actions.DefaultAction;
import com.bea.ide.actions.IActionProxy;
import com.bea.ide.actions.IPopupAction;
import com.bea.ide.actions.IPopupContext;
import com.bea.ide.core.MessageSvc;
import com.bea.ide.core.ResourceSvc;
import com.bea.ide.core.asynctask.AsyncTaskSvc;
import com.bea.ide.core.asynctask.IAsyncTask;
import com.bea.ide.filesystem.FileSvc;
import com.bea.ide.filesystem.IFile;
import com.bea.ide.ui.output.OutputMessage;
import com.bea.ide.ui.output.OutputSvc;
import com.bea.ide.util.URIUtil;
import com.bea.ide.workspace.IProject;
import com.bea.ide.workspace.IWorkspace;
import com.bea.ide.workspace.IWorkspaceEventContext;
import com.bea.wlw.runtime.core.util.CryptUtil;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.prefs.Preferences;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * An action behind a popup menu that uploads the currently selected
 * file or folder via FTP.
 *
 * From DefaultAction, this class gets
 */
public class UploadAction extends DefaultAction implements IPopupAction
{
    static ResourceSvc.IResourcePkg s_pkg =
            ResourceSvc.get().getResourcePackage(FTPPrefsPanel.class, "ftp");

    // A flag to indicate whether it's valid to go ahead and upload.
    private boolean m_isValidUploadContext;
    // An IFile repressenting the project from which files will be uploaded.
    private IFile m_projectDirectory;
    /**
     * A project within which there are selected files; used to test for
     * selections across projects.
     */
    private IProject m_project;
    /**
     * A flag to indicate whether one of the items selected to upload
     * is a project directory. A project will need special handling.
     */
    private boolean m_projectSelected;
    /**
     * A flag to indicate, whether the list items selected for upload
     * spans multiple projects.
     */
    private boolean m_activeProjectChanged;

    // A flag to indicate whether this action is currently trying to upload.
    private boolean m_uploadInProgress;
    /**
     * A flag to indicate that the popup is figuring out whether (and
     * what) to display.
     */
    private boolean m_prepareInProgress;

    // An FTPClient instance to give this action FTP abilities.
    private FTPClient m_ftp;

    // The root directory as specified in project FTP properties.
    private String m_rootDir;

    private Hashtable m_LCAncestors;

    // A window to display FTP status.
    private OutputSvc.IOutputWindow m_win;
    // The window's description.
    private OutputSvc.OutputWindowDescription m_desc;

    private static final Color BLACK = new Color(55, 47, 47);
    private static final Color RED = new Color(223, 45, 45);

    public UploadAction()
    {
        // initialize the output window.
        m_desc = new OutputSvc.OutputWindowDescription();
        m_desc.title = "FTP Output";
        m_desc.destination = "FTP";
        m_win = OutputSvc.get().getWindow(m_desc, false, false);
        m_activeProjectChanged = false;
        m_uploadInProgress = false;
        m_prepareInProgress = false;
    }

    /**
     * Called by the IDE to inform the popup that it is about to be requested
     * for display. The <em>pc</em> parameter includes information
     * about the popup's potential position in the IDE.
     *
     * This method retrieves information
     * needs to handle the case where multiple items in the application
     * tree are selected
     *
     * given the set of nodes that are selected in the application tree,
     * determines whether the "upload" menu item should be visible.
     */
    public void prepare(IPopupContext pc)
    {
        // If there's already some FTPing going on, don't show this command.
        if (m_uploadInProgress || m_prepareInProgress)
        {
            getProxy().setEnabled(false);
            getProxy().putValue(IActionProxy.PROP_Visible, Boolean.valueOf(true));
            getProxy().putValue(IActionProxy.PROP_Label, "Upload");
            return;
        }
        // Set flags to initial values.
        m_prepareInProgress = true;
        m_isValidUploadContext = true;
        m_project = null;
        m_projectSelected = false;

        /**
         * Get a list of all the projects in this application, along with the
         * files in each project. Add each file to an IFile array. An IFile
         * provides many methods useful to handling files through an
         * extension.
         */
        IWorkspace workspace =
                (IWorkspace) Application.get().getProperty(Application.PROP_ActiveWorkspace);
        IProject[] projects = workspace.getProjects();
        IFile[] projectIFiles = new IFile[projects.length];
        for (int i = 0; i < projectIFiles.length; i++)
        {
            projectIFiles[i] = projects[i].getIFile();
        }

        /**
         * This section verifies that the files selected in the Application
         * window are part of the same project; FTP actions are not
         * permitted for multiple files crossing project boundaries.
         */
        IWorkspaceEventContext ctx = (IWorkspaceEventContext) pc;
        // Get the URIs of the selected nodes in the Application window.
        URI[] uris = ctx.getURIs();

        /**
         *
         */
        for (int i = 0; i < uris.length; i++)
        {
            URI uri = uris[i];
            /**
             * Find out if an entire project is selected. If it is, this action
             * will later need to add all of the project's file to the list of those
             * to upload.
             */
            if (uri.getScheme().equals("project"))
            {
                /**
                 * FileSvc.I.getIFile() expects URIs whose scheme component is "file",
                 * so adjust "project" URIs accordingly.
                 */
                uri = URIUtil.changeScheme(uri, "file");
                if (uri == null)
                {
                    MessageSvc.get().displayError(s_pkg.getString("uriChangeSchemeError"), MessageSvc.LEVEL_ERROR);
                    m_prepareInProgress = false;
                    return;
                }
                m_projectSelected = true;
            }

            /**
             * The item selected in the window must be a regular file, folder, or a
             * project (as noted above). In other words, the scheme must be
             * "file," rather than "library", "workspace", or "resource."
             */
            if (!uri.getScheme().equals("file"))
            {
                m_isValidUploadContext = false;
                break;
            }

            // Get an IFile for the item at the URI.
            IFile file = FileSvc.get().getIFile(uri);
            /**
             * A flag to indicate whether the current file's parent project
             * has been found.
             */
            boolean foundProject = false;

            while (file != null)
            {
                /**
                 * Loop through the list of projects in this application, checking
                 * (essentially) to see whether there are multiple projects
                 * represented in the list of files selected in the Application
                 * window.
                 */
                for (int pIndex = 0; pIndex < projectIFiles.length; pIndex++)
                {
                    IFile projectIFile = projectIFiles[pIndex];
                    /**
                     * If the current file in the list of selected files is the same
                     * as the current IFile representing a project in the application,
                     * then find out if the current project IFile is also the same as the
                     * the project IFile established for testing. If a different project
                     * IFile, then the selection list spans multiple projects, and
                     * that's bad because this action doesn't support that.
                     */
                    if (file.compareTo(projectIFile) == 0)
                    {
                        /**
                         * If, among the files selected for upload, a test project hasn't
                         * been determined yet, then set the test project to the
                         * project represented by the current projectIFile.
                         */
                        if (m_project == null)
                        {
                            /**
                             * If the current projectIFile is not the same as the IFile
                             * established for testing, then a change of projects
                             * has occurred.
                             */
                            if (projectIFile != m_projectDirectory)
                            {
                                m_activeProjectChanged = true;
                            } else
                            {
                                m_activeProjectChanged = false;
                            }
                            /**
                             * Set the test project directory and test project to the
                             * current directory and project. If these don't change
                             * during the course of this loop, then they'll be used
                             * for the FTP upload. If they do change, then this action
                             * is unsupported because it means the selection list
                             * spans projects.
                             */
                            m_projectDirectory = projectIFile;
                            m_project = projects[pIndex];
                        /**
                         * If the test project directory is not the same as the current
                         * project IFile, then this action is unsupported because
                         * multiple projects are represented in the list of files to
                         * upload.
                         */
                        } else if (m_projectDirectory.compareTo(projectIFile) != 0)
                        {
                            m_isValidUploadContext = false;
                        }
                        foundProject = true;
                        break;
                    }
                }

                /**
                 * If a project has been found, exit this loop. Otherwise,
                 * set the file to check to the current file's parent.
                 */
                if (foundProject)
                {
                    break;
                }
                file = file.getParentIFile();
            }

            /**
             * For some reason, the upload action can't be done. Stop
             * looping through the list of selected files.
             */
            if (!m_isValidUploadContext)
            {
                break;
            }
        }
        /**
         * If no project is represented by the list of selected files, then
         * set the "valid" flag to false. There's nothing to upload.
         */
        if (m_project == null)
        {
            m_isValidUploadContext = false;
        }

        /**
         * If the items selected are valid for uploading, then enable display
         * of this popup and set display characteristics. Otherwise,
         * don't allow display.
         */
        if (m_isValidUploadContext)
        {
            getProxy().setEnabled(true);
            getProxy().putValue(IActionProxy.PROP_Visible, Boolean.valueOf(true));
            getProxy().putValue(IActionProxy.PROP_Label, "Upload");
        } else
        {
            getProxy().setEnabled(false);
            getProxy().putValue(IActionProxy.PROP_Visible, Boolean.valueOf(false));
        }
        // All done now, so indicate that preparation is finished.
        m_prepareInProgress = false;
    }

    /**
     * A background task for performing the FTP operation.
     */
    private class FTPTask implements IAsyncTask
    {
        // Constructs an instance of this class.
        FTPTask()
        {}

        // No task to run.
        public void cleanup()
        {}

        // No task to run.
        public void interrupt()
        {}

        // No task to run.
        public void runForeground()
        {}

        /**
         * Called by the IDE for code to be run in the background.
         */
        public void runBackground()
        {
            // If uploading is already happening, then don't do it now.
            if (m_uploadInProgress || m_prepareInProgress)
            {
                return;
            }
            // If the upload action wasn't in progress, it is now.
            m_uploadInProgress = true;

            // Initialize the status window.
            m_win = OutputSvc.get().getWindow(m_desc, true, true);
            m_win.addMessage(new OutputMessage("", BLACK, null));
            m_win.addMessage(new OutputMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", BLACK, null));
            m_win.addMessage(new OutputMessage("Current project: " +
                    m_projectDirectory.getName(), BLACK, null));

            // Store all the nodes in a hashtable as IFiles.
            m_LCAncestors = new Hashtable();
            /**
             * If the project itself is selected, put all its direct descendants
             * on the list.
             */
            if (m_projectSelected)
            {
                IFile[] childIFiles = m_projectDirectory.listIFiles();
                for (int i = 0; i < childIFiles.length; i++)
                {
                    m_LCAncestors.put(childIFiles[i], Boolean.TRUE);
                }
            } else
            {
                /**
                 * Use an object representing the current application tree's
                 * selections to get URIs for the selected items.
                 */
                IWorkspaceEventContext ctx =
                        (IWorkspaceEventContext) Application.get().getCookie(IWorkspaceEventContext.KEY);
                URI[] uris = ctx.getURIs();
                /**
                 * Get an IFile for each of the selected items, then put each into
                 * a HashTable to
                 */
                for (int i = 0; i < uris.length; i++)
                {
                    URI uri = uris[i];
                    IFile file = FileSvc.get().getIFile(uri);
                    m_LCAncestors.put(file, Boolean.TRUE);
                }

                /**
                 * For each child node, if one of the other nodes is an ancestor,
                 * remove the child node from the list of least common ancestors.
                 */
                for (int i = 0; i < uris.length; i++)
                {
                    URI childURI = uris[i];
                    IFile childIFile = FileSvc.get().getIFile(childURI);
                    boolean ancestorFound = false;

                    for (int j = 0; j < uris.length; j++)
                    {
                        URI ancestorURI = uris[j];
                        IFile ancestorIFile = FileSvc.get().getIFile(ancestorURI);

                        if (childIFile.compareTo(ancestorIFile) == 0)
                        {
                            continue;
                        }

                        IFile parentIFile = childIFile.getParentIFile();
                        while (parentIFile != null && parentIFile.compareTo(m_projectDirectory) != 0)
                        {
                            if (parentIFile.compareTo(ancestorIFile) == 0)
                            {
                                if (m_LCAncestors.containsKey(childIFile))
                                {
                                    m_LCAncestors.remove(childIFile);
                                }
                                ancestorFound = true;
                                break;
                            }

                            parentIFile = parentIFile.getParentIFile();
                        }

                        if (ancestorFound)
                        {
                            break;
                        }
                    }
                }
            }

            // Get the FTP preferences set by the user.
            Preferences prefs = m_project.systemNodeForPackage(FTPPrefsPanel.class);

            /**
             * If there's no FTP connection yet, attempt to create one using
             * FTP preferences.
             */
            if (m_ftp == null || !m_ftp.isConnected() || m_activeProjectChanged)
            {
                String hostname = "";
                try
                {
                    m_ftp = new FTPClient();
                    hostname = prefs.get(FTPSettings.HOSTNAME, "");
                    hostname = hostname.trim();
                    if (hostname.equals(""))
                    {
                        m_win.addMessage(new OutputMessage("You must specify a hostname for FTP connections", RED, null));
                        m_uploadInProgress = false;
                        return;
                    }
                    m_ftp.connect(hostname);
                    int reply = m_ftp.getReplyCode();
                    if (!(FTPReply.isPositiveCompletion(reply)))
                    {
                        m_win.addMessage(new OutputMessage(hostname + ": refused connection.", BLACK, null));
                        m_ftp.disconnect();
                        m_uploadInProgress = false;
                        return;
                    }
                    m_win.addMessage(new OutputMessage("Connected to " + hostname, BLACK, null));

                    // Get the FTP password and decrypt it.
                    String password = prefs.get(FTPSettings.PASSWORD, null);
                    if (password != null)
                    {
                        try
                        {
                            password = CryptUtil.get().deobfuscate(password);
                        } catch (Exception ex)
                        {
                            MessageSvc.get().debugLog("Exception while attempting to decrypt proxy password: " + ex.getMessage());
                            password = "";
                        }
                    }

                    // Get the preferred FTP port number and set it for file transfers.
                    String port = prefs.get(FTPSettings.PORT, null);
                    int portNumber;
                    try
                    {
                        portNumber = Integer.parseInt(port);
                    } catch (Exception ex)
                    {
                        portNumber = FTPSettings.DEFAULT_PORT;
                    }
                    if (m_ftp.getDefaultPort() != portNumber)
                    {
                        m_ftp.setDefaultPort(portNumber);
                    }

                    /**
                     * Get the preferred username and attempt to log in using
                     * it and the preferred password. Publish a message to
                     * the status window.
                     */
                    String username = prefs.get(FTPSettings.USERNAME, "");
                    username = username.trim();
                    if (!m_ftp.login(username, password))
                    {
                        m_win.addMessage(new OutputMessage("Login failed for user: \"" + username + "\"", RED, null));
                        m_ftp.disconnect();
                        m_uploadInProgress = false;
                        return;
                    }
                    m_win.addMessage(new OutputMessage("Login successful for user: \"" + username + "\"", BLACK, null));

                    /**
                     * Get the preferred root directory and change to that directory
                     * on the server.
                     */
                    m_rootDir = prefs.get(FTPSettings.REMOTE_DIRECTORY, "");
                    m_rootDir = m_rootDir.trim();
                    if (m_rootDir.equals(""))
                    {
                        m_win.addMessage(new OutputMessage("You must specify a directory where files should be uploaded.", RED, null));
                        m_uploadInProgress = false;
                        return;
                    }
                    if (!m_ftp.changeWorkingDirectory(m_rootDir))
                    {
                        if (!m_ftp.makeDirectory(m_rootDir))
                        {
                            m_win.addMessage(new OutputMessage("Failure changing to directory: " + m_rootDir, RED, null));
                            m_uploadInProgress = false;
                            return;
                        }
                    }
                    m_win.addMessage(new OutputMessage("Remote directory: " + m_rootDir, BLACK, null));
                } catch (UnknownHostException ex)
                {
                    m_win.addMessage(new OutputMessage("\"" + hostname + "\" is an unknown host.", RED, null));
                    m_uploadInProgress = false;
                    return;
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    try
                    {
                        m_ftp.disconnect();
                    } catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                    m_uploadInProgress = false;
                    return;
                }
            }

            // Ensure that the root directory exists.
            try
            {
                if (!m_ftp.changeWorkingDirectory(m_rootDir))
                {
                    if (!m_ftp.makeDirectory(m_rootDir))
                    {
                        m_win.addMessage(new OutputMessage("Failure changing to directory: " + m_rootDir, RED, null));
                        m_uploadInProgress = false;
                        return;
                    }
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
                try
                {
                    m_ftp.disconnect();
                } catch (Exception exc)
                {
                    exc.printStackTrace();
                }
                m_uploadInProgress = false;
                return;
            }

            /**
             * For each node to transfer, create its parents on the remote server
             * if necessary, then upload the node and its children.
             */
            Enumeration enum = m_LCAncestors.keys();
            while (enum.hasMoreElements())
            {
                IFile file = (IFile) (enum.nextElement());
                String cwd = m_createParents(file, m_rootDir);
                m_uploadFile(cwd, file);
            }
            // Publish a completion message to the FTP status window.
            m_win.addMessage(new OutputMessage("DONE with FTP task.", BLACK, null));
            // Signal FTPing is done now.
            m_uploadInProgress = false;
        }
    }

    /**
     * Called by the IDE to execute the action associated with this popup.
     * This method adds to the IDE a background task to perform the
     * file upload.
     *
     * @param e The IDE even that occurred.
     */
    public void actionPerformed(ActionEvent e)
    {
        AsyncTaskSvc.get().addTask(new FTPTask());
    }

    /**
     * Creates the subdirectory path from a file to the parent project on
     * the remote server. Returns the string that maps to the file's parent
     * directory. This method is called by the background task in FTPTask
     * in preparation for uploading.
     *
     * @param file The file to upload.
     * @param cwd The remote root directory specified in project properties.
     */
    private String m_createParents(IFile file, String cwd)
    {
        /**
         * If the IFile is really a project root, then just return the
         * root directory; this function is really only intended to be
         * called with a file inside a project root.
         */
        if (file.compareTo(m_projectDirectory) == 0)
        {
            return cwd;
        }

        try
        {
            Stack subdirs = new Stack();

            // Push the file and its parents onto a stack.
            while ((file = file.getParentIFile()) != null && file.compareTo(m_projectDirectory) != 0)
            {
                subdirs.push(file);
            }

            /**
             * For each of the items in the stack, make an IFile and
             * append its name to a path. Then set the current FTP working
             * directory to the one specified by the path.
             */
            while (!subdirs.empty())
            {
                IFile dir = (IFile) subdirs.pop();
                cwd += "/" + dir.getName();
                if (!m_ftp.changeWorkingDirectory(cwd))
                {
                    if (!m_ftp.makeDirectory(cwd))
                    {
                        break;
                    }
                }
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return cwd;
    }

    /**
     * Uploads the specified <em>file</em> to the root directory specified
     * by <em>cwd</em>.
     *
     * @param cwd The root directory to upload to.
     * @param file The file to upload.
     */
    private void m_uploadFile(String cwd, IFile file)
    {
        try
        {
            /**
             * If this is a file, not a directory, then prepend the path to the
             * root and upload it. Publish a status message to the status window.
             */
            if (file.isFile())
            {
                File f = new File(file.getURI());
                FileInputStream stream = new FileInputStream(f);
                String path = cwd + "/" + file.getName();
                boolean ret = m_ftp.storeFile(path, stream);
                if (ret)
                {
                    m_win.addMessage(new OutputMessage(path + ": upload successful.", BLACK, null));
                } else
                {
                    m_win.addMessage(new OutputMessage(path + ": upload failed.", RED, null));
                }
                return;
            }

            /**
             * If the file is a directory, make a corresponding directory on
             * the receiving server.
             */
            cwd += "/" + file.getName();
            if (!m_ftp.changeWorkingDirectory(cwd))
            {
                if (!m_ftp.makeDirectory(cwd))
                {
                    return;
                }
            }

            // Upload each of the files in the current directory.
            IFile[] childIFiles = file.listIFiles();
            for (int i = 0; i < childIFiles.length; i++)
            {
                m_uploadFile(cwd, childIFiles[i]);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}