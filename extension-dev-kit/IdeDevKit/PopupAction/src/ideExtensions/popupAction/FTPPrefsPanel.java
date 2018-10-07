package ideExtensions.popupAction;

import com.bea.ide.core.MessageSvc;
import com.bea.ide.core.ResourceSvc;
import com.bea.ide.swing.TitledTopBorder;
import com.bea.ide.workspace.IProject;
import com.bea.ide.workspace.IProjectPropertyPanel;
import com.bea.wlw.runtime.core.util.CryptUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * A properties panel through which users of the PopupAction extension
 * can set properties for the extension's FTP behavior; these properties
 * include the host name, port, user name, and password that should be
 * used for FTP operations.
 *
 * This class extends JPanel, a Java Swing component that's useful for
 * containing other components for display in a dialog.
 */
public class FTPPrefsPanel extends JPanel implements IProjectPropertyPanel
{
    // Get the package of resource strings that will be used in the UI.
    static ResourceSvc.IResourcePkg s_pkg =
            ResourceSvc.get().getResourcePackage(FTPPrefsPanel.class, "ftp");

    // Variables for user interface components.
    private JPanel m_ftpSettingsPanel;
    private JLabel m_hostNameLabel;
    private JTextField m_hostName;
    private JLabel m_portLabel;
    private JTextField m_port;
    private JLabel m_userNameLabel;
    private JTextField m_userName;
    private JLabel m_passwordLabel;
    private JPasswordField m_password;
    private JLabel m_remoteDirectoryLabel;
    private JTextField m_remoteDirectory;
    private JPanel m_filler;
    private JLabel m_remoteDirectoryTip;
    private JLabel m_portDefaultTip;

    /**
     * A variable to represent the project for which the user is currently
     * setting properties. This value will be set to the value received
     * from the IDE through the setProject method.
     */
    private IProject m_project;

    /**
     * Constructs a new instance of this class, calling a method
     * that builds the user interface from Java Swing components.
     */
    public FTPPrefsPanel()
    {
        super();
        initComponents();
    }

    /**
     * Retrieves properties from stored preferences and loads them into
     * the FTP properties panel for display.
     */
    public void loadProperties()
    {
        Preferences prefs = m_project.systemNodeForPackage(FTPPrefsPanel.class);
        m_hostName.setText(prefs.get(FTPSettings.HOSTNAME, ""));
        m_port.setText(prefs.get(FTPSettings.PORT, ""));
        m_remoteDirectory.setText(prefs.get(FTPSettings.REMOTE_DIRECTORY, ""));
        m_userName.setText(prefs.get(FTPSettings.USERNAME, ""));
        String password = prefs.get(FTPSettings.PASSWORD, null);
        if (password != null)
        {
            try
            {
                password = CryptUtil.get().deobfuscate(password);
                m_password.setText(password);
            } catch (Exception e)
            {
                MessageSvc.get().debugLog("Exception while attempting to decrypt proxy password: " + e);
            }
        }
    }

    /**
     * Stores property values entered in the FTP properties panel.
     */
    public void storeProperties()
    {
        // Retrieve the FTP preferences.
        Preferences prefs = m_project.systemNodeForPackage(FTPPrefsPanel.class);

        /**
         * Get the hostname property value. If the value is not equal to the
         * value already set, then set the new value.
         */
        String hostName = m_hostName.getText();
        if (!hostName.equals(prefs.get(FTPSettings.HOSTNAME, null)))
        {
            prefs.put(FTPSettings.HOSTNAME, hostName);
        }

        /**
         * Get the port property value. If the value is not equal to the
         * value already set, then set the new value.
         */
        String port = m_port.getText();
        if (!port.equals(prefs.get(FTPSettings.PORT, null)))
        {
            prefs.put(FTPSettings.PORT, port);
        }

        /**
         * Get the remote directory property value. If the value is not equal to the
         * value already set, then set the new value.
         */
        String remoteDirectory = m_remoteDirectory.getText();
        if (!remoteDirectory.equals(prefs.get(FTPSettings.REMOTE_DIRECTORY, null)))
        {
            prefs.put(FTPSettings.REMOTE_DIRECTORY, remoteDirectory);
        }

        /**
         * Get the username property value. If the value is not equal to the
         * value already set, then set the new value.
         */
        String userName = m_userName.getText();
        if (!userName.equals(prefs.get(FTPSettings.USERNAME, null)))
        {
            prefs.put(FTPSettings.USERNAME, userName);
        }

        /**
         * Retrieve the new password set in the properties panel;
         * retrieve the old password from preferences.
         */
        String password = m_password.getText();
        String oldPassword = prefs.get(FTPSettings.PASSWORD, null);
        /**
         * If the old password isn't null, then attempt to decrypt it for
         * comparison with the new password.
         */
        if (oldPassword != null)
        {
            try
            {
                oldPassword = CryptUtil.get().deobfuscate(oldPassword);
            } catch (Exception e)
            {
                MessageSvc.get().debugLog("Exception while attempting to decrypt proxy password: " + e);
                oldPassword = null;
            }
        }
        /**
         * If the new password is not the same as the old password, then
         * set the new password the preferences.
         */
        if (!password.equals(oldPassword))
        {
            try
            {
                prefs.put(FTPSettings.PASSWORD, hierCryptUtil.get().obfuscate(password));
            } catch (Exception e)
            {
                MessageSvc.get().debugLog("Exception while attempting to encrypt proxy password: " + e);
            }
        }
    }

    /**
     * Called by the IDE to discover whether property values entered by
     * the user are valid before the entries are saved and the dialog is
     * closed. This method will be called after the user clicks OK in the
     * dialog, and before the IDE calls your storeProperties implementation..
     * This method provides an opportunity for you to check the new
     * values and return <code>true</code> if they're valid for use as
     * property values.
     *
     * The IDE will call your implementation of the storeProperties method
     * if you return <code>true</code> from validateEntries.
     *
     * @param dialog The dialog that contains this panel.
     * @return <code>true</code> if the entries are valid; <code>false</code>
     * if they are not.
     */
    public boolean validateEntries(JDialog dialog)
    {
        // Validate the host name by ensuring it exists.
        String hostName = m_hostName.getText();
        try
        {
            if (hostName == null || hostName.equals(""))
            {
                throw new UnknownHostException();
            }
        } catch (UnknownHostException uhe)
        {
            MessageSvc.get().displayError(s_pkg.getString("hostnameError"), MessageSvc.LEVEL_ERROR);
            return false;
        }

        // Validate the port by ensuring that it exists as an int.
        String port = m_port.getText();
        if (!port.equals(""))
        {
            try
            {
                int value = Integer.parseInt(port);
            } catch (NumberFormatException nfe)
            {
                MessageSvc.get().displayError(s_pkg.getString("portError"), MessageSvc.LEVEL_ERROR);
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the IDE after the user clicks the Cancel button. If this
     * panel had acquired resources, this would be a good place to
     * release them.
     */
    public void cancel()
    {
        // Not implemented
    }

    /**
     * Called by the IDE to inform this panel as to which project the
     * user is currently setting properties for. It is necessary to know
     * this information in order to retrieve and display properties for
     * the correct project.
     *
     * @param project The project for which the user is setting
     * properties.
     */
    public void setProject(IProject project)
    {
        m_project = project;
    }

    /**
     * Builds the user interface for the properties panel by arranging
     * Java Swing components into a "gridbag" layout.
     */
    public void initComponents()
    {
        setLayout(new GridBagLayout());

        m_ftpSettingsPanel = new JPanel();

        // The label and text box for the hostname property value.
        m_hostNameLabel = new JLabel(s_pkg.getString("host"));
        m_hostName = new JTextField();

        // The label and text box for the port property value.
        m_portLabel = new JLabel(s_pkg.getString("port"));
        m_port = new JTextField();

        // The label and text box for the username property value.
        m_userNameLabel = new JLabel(s_pkg.getString("username"));
        m_userName = new JTextField();

        // The label and text box for the password property value.
        m_passwordLabel = new JLabel(s_pkg.getString("password"));
        m_password = new JPasswordField();

        // The label and text box for the remote directory property value.
        m_remoteDirectoryLabel = new JLabel(s_pkg.getString("remoteDirectory"));
        m_remoteDirectory = new JTextField();

        /**
         * The panel that holds the input components. This is nested inside
         * the panel that the IDE puts into the Properties dialog.
         */
        m_ftpSettingsPanel.setBorder(new TitledTopBorder(s_pkg.getString("ftpSettings")));
        m_ftpSettingsPanel.setLayout(new GridBagLayout());

        // The descriptive text for the remote directory and port boxes.
        m_remoteDirectoryTip = new JLabel(s_pkg.getString("remoteDirectoryTip"));
        m_portDefaultTip = new JLabel(s_pkg.getString("portDefaultTip"));
        m_filler = new JPanel();

        /**
         * Use GridBagConstraints instances to hold the components
         * that make up the user interface. Each of the following sections
         * creates an instance, sets layout characteristics for the
         * instance, then adds a component to the panel that contains it
         * using the instance as it has been set up. The added component
         * is positioned in the panel according to the characteristics
         * set for the constraints instance.
         */
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(2, 2, 2, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_ftpSettingsPanel.add(m_hostNameLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        m_ftpSettingsPanel.add(m_hostName, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        m_ftpSettingsPanel.add(m_portLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        m_ftpSettingsPanel.add(m_port, gbc);

        GridBagConstraints tipGBC = new GridBagConstraints();
        tipGBC.gridwidth = GridBagConstraints.REMAINDER;
        tipGBC.weightx = 1.0;
        tipGBC.insets = new Insets(2, 10, 2, 6);
        tipGBC.anchor = GridBagConstraints.WEST;
        tipGBC.fill = GridBagConstraints.HORIZONTAL;
        m_ftpSettingsPanel.add(m_portDefaultTip, tipGBC);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        m_ftpSettingsPanel.add(m_remoteDirectoryLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        m_ftpSettingsPanel.add(m_remoteDirectory, gbc);

        tipGBC = new GridBagConstraints();
        tipGBC.gridwidth = GridBagConstraints.REMAINDER;
        tipGBC.weightx = 1.0;
        tipGBC.insets = new Insets(2, 10, 2, 6);
        tipGBC.anchor = GridBagConstraints.WEST;
        tipGBC.fill = GridBagConstraints.HORIZONTAL;
        m_ftpSettingsPanel.add(m_remoteDirectoryTip, tipGBC);

        GridBagConstraints fillerGBC = new GridBagConstraints();
        fillerGBC.gridwidth = GridBagConstraints.REMAINDER;
        fillerGBC.anchor = GridBagConstraints.WEST;
        fillerGBC.fill = GridBagConstraints.HORIZONTAL;
        fillerGBC.insets = new Insets(8, 0, 8, 0);
        fillerGBC.weightx = 1.0;
        fillerGBC.weighty = 1.0;
        m_ftpSettingsPanel.add(m_filler, fillerGBC);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        m_ftpSettingsPanel.add(m_userNameLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        m_ftpSettingsPanel.add(m_userName, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        m_ftpSettingsPanel.add(m_passwordLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        m_ftpSettingsPanel.add(m_password, gbc);

        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.gridwidth = GridBagConstraints.REMAINDER;
        topGbc.anchor = GridBagConstraints.WEST;
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(4, 1, 8, 1);
        topGbc.weightx = 1.0;
        topGbc.weighty = 1.0;
        add(m_ftpSettingsPanel, topGbc);
        add(new JPanel(), topGbc);
    }
}
