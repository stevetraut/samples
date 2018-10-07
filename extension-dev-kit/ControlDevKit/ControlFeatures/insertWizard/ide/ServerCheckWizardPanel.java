package insertWizard.ide; 

import weblogic.management.MBeanHome;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JButton;
import insertWizard.MBeanUtil;
import com.bea.ide.control.EditorContext;
import weblogic.management.configuration.ConfigurationMBean;
import javax.management.MBeanServer;
import weblogic.management.RemoteMBeanServer;
import javax.management.MBeanInfo;
import weblogic.management.configuration.WebServerMBean;
import java.util.Set;
import java.util.Iterator;
import java.awt.FocusTraversalPolicy;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Represents the user interface for the ServerCheck control
 * insert dialog. 
 */
public class ServerCheckWizardPanel extends JPanel 
    implements ActionListener
{ 
	JPanel m_pnlServerInfo;
	JTextField m_txtUserName;
	JPasswordField m_pssPassword;
	JLabel m_lblPassword;
	JLabel m_lblUserName;
	JTextField m_txtServerURL;
	JLabel m_lblServerURL;
	JButton m_btnCheckConnection;
	JTextField m_txtServerName;
	JLabel m_lblServerName;
    EditorContext _ctx;

    public ServerCheckWizardPanel()
    {
        initComponents();          
    }

    public void setContext(EditorContext ctx)
    {
        _ctx = ctx;
        m_pnlServerInfo.setFocusCycleRoot(true);
        m_pnlServerInfo.setFocusTraversalPolicy(_newFtp);
    }
    /*
     * Executes when the user clicks the "Check Connection" button
     * in the insert dialog. If the connection does not succeed, 
     * relevant messages are forwarded to the user from the MBean.
     */
    public void actionPerformed(ActionEvent e) {
        MBeanHome mBeanFromUserData = null;
        MBeanHome mBeanCurServer = null;        
        String message="Connection Check:";
        try {
            if (!_ctx.ensureServerRunning())
                return;
           mBeanCurServer = (MBeanHome)_ctx.getMBeanHome();
            
            RemoteMBeanServer mBS = mBeanCurServer.getMBeanServer();
            message += "<br> Server Name current value is:  " + mBS.getServerName();
                    
            mBeanFromUserData = MBeanUtil.getMBean(getUserName(), getPassword(), 
                getServerURL(), getServerName());
            message += "<br> Password access verified!";
            this.showConnectionCheckResults(message);            
        } catch (IllegalArgumentException iae) {
            message += "<br>" + iae.getMessage();
            this.showConnectionCheckResults(message);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            message += "<br>" + ex.getMessage();
            this.showConnectionCheckResults(message);
        }
    }
    
    /*
     * Displays a dialog with the results of the "Check Connection"
     * button.
     */
    private void showConnectionCheckResults(String resultsMessage){
            StringBuffer message = new StringBuffer();
            message.append("<html><p>").append(resultsMessage).
                append("</p><p>").append("</p></html>");
            JOptionPane.showMessageDialog(this, message, "Connection Check", 
                JOptionPane.INFORMATION_MESSAGE);        
    }
    
    
    private FocusTraversalPolicy _newFtp = new FocusTraversalPolicy()
        {
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
            {                
                if (aComponent==m_btnCheckConnection)
                    return m_txtServerName;
                if (aComponent==m_txtServerName)
                    return m_txtServerURL;
                if (aComponent==m_txtServerURL)
                    return m_txtUserName;
                if (aComponent==m_txtUserName)
                    return m_pssPassword;
                if (aComponent==m_pssPassword)
                    return m_btnCheckConnection;                
                 return null;
            }

            public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
            {
                if (aComponent==m_txtServerName)
                    return m_btnCheckConnection;
                if (aComponent==m_btnCheckConnection)
                    return m_pssPassword;
                if (aComponent==m_pssPassword)
                    return m_txtUserName;
                if (aComponent==m_txtUserName)
                    return m_txtServerURL;
                if (aComponent==m_txtServerURL)
                    return m_txtServerName;              
                 return null;
            }

            public Component getDefaultComponent(Container focusCycleRoot)
            {
                  return m_btnCheckConnection;
            }

            public Component getFirstComponent(Container focusCycleRoot)
            {
                  return m_txtServerName;                
            }

            public Component getLastComponent(Container focusCycleRoot)
            {
                  return m_btnCheckConnection;                
            }
        };

    /*
     * Assembles user interface components that make up the insert
     * dialog.
     */
    private void initComponents() {
      
        m_pnlServerInfo = new JPanel();
        m_txtUserName = new JTextField();
        m_pssPassword = new JPasswordField();
        m_lblPassword = new JLabel();
        m_lblUserName = new JLabel();
        m_txtServerURL = new JTextField();
        m_lblServerURL = new JLabel();
        m_btnCheckConnection = new JButton();
        m_txtServerName = new JTextField();
        m_lblServerName = new JLabel();
             
        GridBagConstraints gridBagConstraints;

        setLayout(new GridBagLayout());
        setEnabled(true);
                                
        m_lblServerName.setText("Server name:   ");      
        m_lblServerName.setLabelFor(m_txtServerName);
        m_lblServerName.setDisplayedMnemonic('S');
                	
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(m_lblServerName, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_txtServerName, gridBagConstraints);

		m_lblServerURL.setText("Server URL:       ");
        m_lblServerURL.setLabelFor(m_txtServerURL);
        m_lblServerURL.setDisplayedMnemonic('r');
        m_txtServerURL.setFocusTraversalKeysEnabled(true);        
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_lblServerURL, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_txtServerURL, gridBagConstraints);


		m_lblUserName.setText("WLS Admin user:  ");
        m_lblUserName.setLabelFor(m_txtUserName);
        m_lblUserName.setDisplayedMnemonic('A');
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_lblUserName, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_txtUserName, gridBagConstraints);

		m_lblPassword.setText("Password:    ");
        m_lblPassword.setLabelFor(m_pssPassword);
        m_lblPassword.setDisplayedMnemonic('P');
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_lblPassword, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(m_pssPassword, gridBagConstraints);

		m_btnCheckConnection.setText("Check Connection");
        m_btnCheckConnection.setMnemonic('C');
        m_btnCheckConnection.addActionListener(this);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 250);
        add(m_btnCheckConnection, gridBagConstraints);
        
        
    }
    
    /*
     * A set of accesses that simplify retrieving the values
     * that the control's user has entered into the insert dialog.
     */
    
    public String getPassword(){    
        return m_pssPassword.getText();
    }
    
    public String getUserName(){
        return m_txtUserName.getText();
    }
    
    public String getServerName(){
        return m_txtServerName.getText();
    }
    
    public String getServerURL() throws IllegalArgumentException
    {
        
        try 
        {
            URL serverURL = new URL(m_txtServerURL.getText());
            return serverURL.toString();
        }
        catch (MalformedURLException mfue)
        {
            throw new IllegalArgumentException("Server URL must be of form http://localhost:7001");
        }
    }

} 
