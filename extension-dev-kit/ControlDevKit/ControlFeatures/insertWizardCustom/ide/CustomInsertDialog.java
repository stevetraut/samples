package insertWizardCustom.ide; 

import com.bea.control.Issue;
import com.bea.ide.Application;
import com.bea.ide.control.ControlWizard;
import com.bea.ide.core.MessageSvc;
import com.bea.ide.core.ResourceSvc;
import com.bea.ide.ui.browser.BrowserSvc;
import com.bea.ide.ui.output.OutputSvc;
import com.bea.ide.util.swing.DialogUtil;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A dialog implementation for the IDE to display when the user chooses to
 * add or insert a CustomWiz control. 
 */
public class CustomInsertDialog extends JDialog
{
    protected String m_variableName = new String();
    protected String m_jcxName = new String();
    /**
     * This variable is used in this dialog's logic to indicate
     * whether a new JCX may be created. This value is retrieved
     * by the CustomInsertWizard instance for this control for
     * passing to the IDE through the wizard's implementation of the
     * onFinish method.
     */
    protected boolean m_controlInsertRequested = false;
    private boolean m_jcxNameIsGood = false;
    private boolean m_variableNameIsGood = false;
    private CustomInsertWizard m_wizard = null;
    private Frame m_parent = null;
    
    // Declare variables for the user interface components.
    private JButton m_btnCancel;
    private JButton m_btnCreate;
    private JEditorPane m_editIntroduction;
    private JLabel m_lblJcxOptions;
    private JLabel m_lblNewJcx;
    private JLabel m_lblVariableName;
    private JPanel m_pnlCreateCancel;
    private JPanel m_pnlMessages;
    private JPanel m_pnlTab1;
    private JPanel m_pnlTab2;
    private JPanel m_pnlVariableJCXName;
    private JScrollPane m_scrIntroduction;
    private JScrollPane m_scrMessages;
    private JTabbedPane m_tabTabbedPane;
    private JTextField m_txtNewJcx;
    private JTextField m_txtVariableName;
    private JTextArea m_txtaMessages;
    private DialogLinkListener m_linkListener;
    private DialogFocusListener m_focusListener;
    private DialogMouseListener m_mouseListener;
    private DialogDocumentListener m_documentListener;

    /**
     * Constructs the dialog with a parent (the WebLogic Workshop
     * window) and an instance of the wizard. The wizard instance will be 
     * used to retrieve information about the current context.
     * 
     * @param parent A Frame representing the WebLogic Workshop main window.
     * @param wizard The current wizard instance.
     */    
    public CustomInsertDialog(Frame parent, CustomInsertWizard wizard)
    {
        super(parent, true);
        // Variables for the control's wizard and for the WebLogic Workshop frame.
        m_wizard = wizard;
        m_parent = parent;
        // Assemble the UI components.
        initComponents();
        // Set this dialog's size.
        this.setSize(499, 561);
        // This dialog cannot be resized.
        this.setResizable(false);
        // Set the title bar text.
        this.setTitle("Insert Control - CustomWiz");        
    }

    /**
     * Displays this dialog.
     */
    public void show()
    {
        /**
         * Add a key event dispatcher to listen for ENTER and ESC keystrokes.
         */
        DialogKeyEventDispatcher dked = new DialogKeyEventDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dked);
        super.show();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dked);
    }
    
    /**
     * Assemble the user interface for this dialog. All of the work
     * to assemble this dialog's user interface is done in this method.
     */
    private void initComponents() 
    {
        java.awt.GridBagConstraints gridBagConstraints;

        /*
         * Initialize the user interface components.
         */
        m_tabTabbedPane = new JTabbedPane();
        m_pnlTab1 = new JPanel();
        m_scrIntroduction = new JScrollPane();
        m_editIntroduction = new JEditorPane();
        m_pnlTab2 = new JPanel();
        m_pnlVariableJCXName = new JPanel();
        m_lblVariableName = new JLabel();
        m_txtVariableName = new JTextField();
        m_lblJcxOptions = new JLabel();
        m_lblNewJcx = new JLabel();
        m_txtNewJcx = new JTextField();
        m_pnlMessages = new JPanel();
        m_scrMessages = new JScrollPane();
        m_txtaMessages = new JTextArea();
        m_pnlCreateCancel = new JPanel();
        m_btnCreate = new JButton();
        m_btnCancel = DialogUtil.getCancelButton();
        m_linkListener = new DialogLinkListener();
        m_focusListener = new DialogFocusListener();
        m_mouseListener = new DialogMouseListener();
        m_documentListener = new DialogDocumentListener();

        /**
         * Set the layout style for this dialog.
         */
        getContentPane().setLayout(new java.awt.GridBagLayout());

        // Specify what should happen when the dialog closes.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
                closeDialog(evt);
            }
        });

        /*
         * Assemble the panel that makes up the first tab of this dialog.
         */
        m_pnlTab1.setLayout(new java.awt.GridBagLayout());

        /*
         * m_editIntroduction is the JEditorPane that displays the HTML
         * introduction to this sample. m_scrIntroduction is the JScrollPane
         * that contains it and provides a vertical scroll bar.
         */
        m_editIntroduction.setContentType("text/html");
        // Connect a listener to handle clicks to links in the introduction.
        m_editIntroduction.addHyperlinkListener(m_linkListener);
        m_editIntroduction.setEditable(false);
        m_editIntroduction.setMargin(new Insets(0, 0, 0, 8));
        m_editIntroduction.setSize(m_editIntroduction.getSize());
        m_scrIntroduction.setViewportView(m_editIntroduction);
        m_scrIntroduction.setAutoscrolls(true);

        /*
         * Use the application's classloader to find the HTML file
         * to load in the introduction pane. Set that URL as the 
         * content of the pane.
         */
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL introUrl = loader.getResource("insertWizardCustom/ide/html/introduction.html");
        try
        {
            m_editIntroduction.setPage(introUrl);
        }
        catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }

        /*
         * Swing provides several "layout styles" -- ways to structure the components
         * of a user interface. For the most part, this dialog uses GridBagLayout
         * as its style. In a gridbag layout, components are structured based on a grid
         * and their position in the grid. This includes X and Y axes, number of 
         * grid points in width or height, "weight" relative to other components, 
         * where the component is anchored (top, right, left, etc), and so on.
         * 
         * You use a GridBagConstraint instance to set these component properties,
         * then add the component to its parent along with the constraint instance.
         * 
         * Here, the constraint instance is used to set layout properties for 
         * m_scrIntroduction, the JScrollPane that contains the pane used to 
         * display introduction HTML.
         */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        // Add the scroll pane to the panel on the first tab.
        m_pnlTab1.add(m_scrIntroduction, gridBagConstraints);

        m_tabTabbedPane.setName("tabs");
        
        /*
         * Add the first tab panel as the UI component to the tabbed pane.
         */ 
        m_tabTabbedPane.addTab("Introduction", m_pnlTab1);

        /*
         * Assemble the panel that makes up the first tab of this dialog.
         */
        m_pnlTab2.setLayout(new java.awt.GridBagLayout());

        /*
         * Set layout of components in this panel to null. Its components will be 
         * arranged through explicit coordinates.
         */
        m_pnlVariableJCXName.setLayout(null);

        /*
         * Define the variable name label and box and add them to the panel.
         */
        m_lblVariableName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_lblVariableName.setText("Variable name for this control:");
        m_lblVariableName.setBounds(0, 20, 163, 16);
        m_pnlVariableJCXName.add(m_lblVariableName);

        m_txtVariableName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        m_txtVariableName.setName("variable_name_box");
        m_txtVariableName.setToolTipText("Enter the name for a variable of this control type.");
        // Connect a listener that will handle receiving focus.
        m_txtVariableName.addFocusListener(m_focusListener);
        // Connect a listener that will handle changes to the box's contents.
        m_txtVariableName.getDocument().addDocumentListener(m_documentListener);
        m_txtVariableName.setBounds(160, 20, 295, 20);
        m_pnlVariableJCXName.add(m_txtVariableName);

        /**
         * Define the JCX name label and box and add them to the panel.
         */
        m_lblNewJcx.setText("JCX name for this control:");
        m_lblNewJcx.setBounds(0, 62, 163, 16);
        m_pnlVariableJCXName.add(m_lblNewJcx);

        m_txtNewJcx.setToolTipText("Enter the name of the JCX to create");
        m_txtNewJcx.setName("new_jcx_name_box");
        // Connect a listener that will handle receiving focus.
        m_txtNewJcx.addFocusListener(m_focusListener);
        // Connect a listener that will handle changes to the box's contents.
        m_txtNewJcx.getDocument().addDocumentListener(m_documentListener);
        m_txtNewJcx.setBounds(140, 62, 315, 20);
        m_pnlVariableJCXName.add(m_txtNewJcx);

        /**
         * Define this panel's relationship to other components on 
         * the tab, then add the panel to the tab.
         */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_pnlTab2.add(m_pnlVariableJCXName, gridBagConstraints);
        
        /**
         * Define the message panel and text area, then add them to the 
         * tab.
         */
        m_pnlMessages.setLayout(new java.awt.GridBagLayout());
        m_pnlMessages.setPreferredSize(m_pnlMessages.getPreferredSize());
        m_txtaMessages.setLineWrap(true);
        m_txtaMessages.setWrapStyleWord(true);
        m_txtaMessages.setMargin(new Insets(6, 6, 6, 6));
        m_txtaMessages.setSize(m_txtaMessages.getSize());
        m_txtaMessages.setEditable(false);
        m_scrMessages.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        m_scrMessages.setViewportView(m_txtaMessages);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_pnlMessages.add(m_scrMessages, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 3.5;
        m_pnlTab2.add(m_pnlMessages, gridBagConstraints);

        // Add the tab to the group of tabs.        
        m_tabTabbedPane.addTab("Name the Control", m_pnlTab2);

        /**
         * Add the tabs to the dialog. getContentPane returns this 
         * dialog's content pane.
         */ 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 15, 6);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        // Connect a listener that will handle mouse clicks.
        m_tabTabbedPane.addMouseListener(m_mouseListener);        
        getContentPane().add(m_tabTabbedPane, gridBagConstraints);

        /*
         * Define the panel that holds the Create and Cancel buttons,
         * then add it to the dialog.
         */
        m_pnlCreateCancel.setLayout(new java.awt.GridBagLayout());
        m_btnCreate.setText("Create");
        m_btnCreate.setName("create_button");
        // Connect a listener that will handle mouse clicks.
        m_btnCreate.addMouseListener(m_mouseListener);
        // Because the first tab is visible by default, disable the Create button.
        m_btnCreate.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 100);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_pnlCreateCancel.add(m_btnCreate, gridBagConstraints);
        m_btnCancel.setText("Cancel");
        m_btnCancel.setName("cancel_button");
        // Connect a listener that will handle mouse clicks.
        m_btnCancel.addMouseListener(m_mouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_pnlCreateCancel.add(m_btnCancel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(m_pnlCreateCancel, gridBagConstraints);
        
        /*
         * If the user tried to add a CustomWiz control by clicking File -> New 
         * (or by right-clicking in the Application pane), then don't show the second tab.
         * The user does not want to add this control as an instance to a container (yet),
         * and has already been prompted for a JCX name.
         * 
         * If the user has clicked the Insert menu (or right-clicked Design View), the second
         * tab will be displayed. In that scenario, the IDE will set configuration as 
         * a sum of CONFIG_CREATE_EXTENSION_FILE and CONFIG_INSERT_INSTANCE.
         */
        if (m_wizard.m_requestedConfig == CustomInsertWizard.CONFIG_CREATE_EXTENSION_FILE)
        {            
            // Disable the tab at index 1 (the second tab).
            m_tabTabbedPane.setEnabledAt(1, false);
            // Hide the Create button because it's not relevant in this case.
            m_btnCreate.setVisible(false);
            // Select the Cancel button (which is relabeled below).
            m_btnCancel.setSelected(true);
            // Relabel the Cancel button to Done.
            m_btnCancel.setLabel("Done");
        }
        
        // Sizes the window to preferred size.
        pack();
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) 
    {
        setVisible(false);
    }
    
    /**
     * Prints messages to the dialog's bottom pane as the user 
     * shifts focus to components in the user interface.
     */
    private void printActionMessage(String messageText)
    {
        m_txtaMessages.append(messageText + "\n");
    }

    /**
     * Displays a dialog that contains messages resulting from
     * validating JCX or variable names. This method is called if there
     * are validation issues when the Create button is clicked.
     * 
     * @param issues Issues that arose while validating dialog values.
     * @param proposedName The name that was validated.
     */
    private void displayIssues(Issue[] issues, String proposedName)
    {
        StringBuffer issueMessages = new StringBuffer();
        for (int i = 0; i < issues.length; i++)
        {
            issueMessages.append(issues[i].getDescription() + "\n");
            if (issues[i].getPrescription() != null)
            {
                issueMessages.append(issues[i].getPrescription() + "\n");
            }
        }
        DialogUtil.showErrorDialog(issueMessages.toString());
    }

    /**
     * Enables the Create button if there are values in both the 
     * variable and JCX name boxes; otherwise, disables the Create
     * button.
     */    
    private void checkForEntries()
    {
        if (!m_txtNewJcx.getText().equals("") && !m_txtVariableName.getText().equals(""))
        {
            m_btnCreate.setEnabled(true);
        }
        else
        {
            m_btnCreate.setEnabled(false);
        }        
    }
    
    /**
     * Evaluates the contents of the dialog's text boxes,
     * validating those boxes' values.
     * 
     * @param componentName The name of the UI component that
     * received an action -- ie, the Create or Cancel button.
     */
    private void evaluateDialog(String componentName)
    {
        /**
         * If the user presses return when focus is on the create button,
         * variable name box, or either of the JCX name boxes, do the same
         * thing: check that there are allowable variable names and
         * JCX names, and handle errors accordingly.
         */
        if (componentName.equals("create_button"))
        {
            
            /*
             * If the second tab is visible, validate the input in the
             * variable and JCX name boxes.
             */
            if (m_pnlTab2.isVisible())
            {
                /*
                 * If there's text in the variable name box,
                 * assign that text value to a variable. If there's no text,
                 * prompt the user to enter some. Also, set a boolean
                 * variable to false so it can be checked along with the 
                 * presence of a JCX name value later.
                 */
                if (!m_txtVariableName.getText().equals(""))
                {
                    m_variableName = m_txtVariableName.getText();                        
                }
                else if (m_txtVariableName.getText().equals(""))
                {
                    m_variableNameIsGood = false;
                    DialogUtil.showErrorDialog("Please specify a variable name.");                                                
                }
                /*
                 * If there's text in the JCX name box,
                 * assign that text value to a variable. If there's no text,
                 * prompt the user to enter some. Also, set a boolean
                 * variable to false so it can be checked along with the 
                 * presence of a variable name value later.
                 */
                if (!m_txtNewJcx.getText().equals(""))
                {
                    m_jcxName = m_txtNewJcx.getText();
                }
                else if (m_txtNewJcx.getText().equals(""))
                {
                    m_jcxNameIsGood = false;
                    DialogUtil.showErrorDialog("Please specify a JCX name.");                        
                }    
                /*
                 * If both boxes have values, validate those values for use as 
                 * Java code in WebLogic Workshop. If there are issues,
                 * report the issues to the user.
                 */
                if (!m_jcxName.equals("") && !m_variableName.equals(""))
                {
                    Issue[] jcxIssues = m_wizard.getNameValidator().validateExtensionName(m_jcxName);
                    Issue[] variableIssues = m_wizard.getNameValidator().validateInstanceName(m_variableName);
                    if (jcxIssues != null)
                    {
                        m_jcxNameIsGood = false;
                        displayIssues(jcxIssues, m_jcxName);
                    }
                    if (variableIssues != null)
                    {
                        m_variableNameIsGood = false;
                        displayIssues(variableIssues, m_variableName);
                    }
                    if (variableIssues == null)
                    {
                        m_variableNameIsGood = true;
                    }
                    if (jcxIssues == null)
                    {
                        m_jcxNameIsGood = true;
                    }
                    /*
                     * After validation, if both the variable and JCX name boxes have
                     * valid values, signal that it's okay to insert the control
                     * and close the dialog.
                     */
                    if (m_jcxNameIsGood && m_variableNameIsGood)
                    {
                        m_controlInsertRequested = true;
                        closeDialog(null);
                    }
                }
            }
        }
        
        /*
         * The "Cancel" button needs to be handled as "OK" in one
         * circumstance.
         */
        if (componentName.equals("cancel_button"))
        {
            /*
             * If CONFIG_CREATE_EXTENSION_FILE is the requested configuration,
             * it means that the users reached this dialog through File -> New -> Java Control
             * (or a related command). They've already named the control, so we
             * change the Cancel button to an OK button (see code in initComponents), set
             * "requested" to true, and close the dialog.
             */
            if (m_wizard.m_requestedConfig == CustomInsertWizard.CONFIG_CREATE_EXTENSION_FILE)
            {
                m_controlInsertRequested = true;
                closeDialog(null);                    
            }
            /*
             * For other configurations, we presented a cancel button and 
             * set "requested" to false when it is clicked.
             */
            else
            {
                m_controlInsertRequested = false;
                closeDialog(null);
            }
        }
    }


    /**
     * A m_focusListener implementation simply to present messages about
     * how different pieces of the dialog user interface contribute to the
     * control's insertion.
     * 
     * Note that it can be it can be difficult to debug a focus listener with
     * WebLogic Workshop's debugger. This is because a breakpoint set in the 
     * listener will immediately be hit again when you attempt to switch back
     * to the instance launched when you started debugging. Instead of using 
     * the debugger, you can print messages to the Output pane using the
     * message service, in this way:
     * 
     *      MessageSvc.get().debugLog("My debugging message.");
     * 
     * This is an alternative to using System.out, which is unavailable when
     * a console window is not displayed.
     */
    private class DialogFocusListener implements FocusListener
    {
        /**
         * Received when a component that has added this listener receives
         * focus. This event handler is used to print messages to the 
         * dialog's message window as the variable and JCX name boxes
         * receive focus.
         * 
         * @param event The details of the event.
         */
        public void focusGained(FocusEvent event)
        {
            String componentName = ((JComponent)event.getSource()).getName();

            /*
             * If the component that received focus is a text field, then it 
             * must be the variable or JCX name box. Print a descriptive
             * message.
             */
            if (event.getSource() instanceof JTextField)
            {
                if (componentName.equals("variable_name_box"))
                {
                    String message = "Control variable name: \n" +
                        "The name entered here is stored in a dialog " +
                        "variable. When the Create button is clicked, the value is " +
                        "checked and validated within this dialog. If the " +
                        "value is valid, the control wizard retrieves the value from " +
                        "from this dialog and holds it for retrieval by the IDE through " +
                        "the wizard's getInstanceName method.\n";
                    printActionMessage(message);
                }
                if (componentName.equals("new_jcx_name_box"))
                {
                    String message = "New JCX name: \n" +
                        "The name entered here is stored in a dialog " +
                        "variable. When the Create button is clicked, the value is " +
                        "checked and validated within this dialog. If the " +
                        "value is valid, the control wizard retrieves the value from " +
                        "from this dialog and holds it for retrieval by the IDE through " +
                        "the wizard's getExtensionName method.\n";
                    printActionMessage(message);
                }
            }
        }

        /**
         * Not implemented. This dialog doesn't do anything special when focus
         * leaves a component.
         */
        public void focusLost(FocusEvent arg0){}
    }

    /**
     * Handles hyperlinks events within the JEditorPane component
     * in the "Introduction" pane of the dialog. This 
     * code launches a browser to display the HTML page at the other
     * end of the clicked link.
     */
    private class DialogLinkListener implements HyperlinkListener 
    {
        /**
         * Handles a hyperlink click.
         * 
         * @param event The details of the event.
         */
        public void hyperlinkUpdate(HyperlinkEvent event) 
        {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
            {
                // Use the browser service to display the page.
                BrowserSvc.get().invokeBrowser(event.getURL(), true);
            }
        }
    }
    
    /**
     * An event listener to listen for mouse actions in this dialog.
     * In particular, if the event is a mouse click on one of the dialog's
     * tabs, this listener enables or disables the "Create" button. If
     * the mouse click is on the first tab ("Introduction"), the button is disabled. It would
     * be counterintuitive to allow the user to click Create from the first tab, which 
     * does not prompt for information needed to create the control.
     */
    private class DialogMouseListener implements MouseListener
    {
        /**
         * Handles a mouse click event. 
         * 
         * @param event The click event.
         */
        public void mouseClicked(MouseEvent event)
        {
            /**
             * If a tab was clicked, enable or disable the Create button to 
             * based on which tab was clicked and whether there are values
             * in the variable and JCX name boxes.
             */
            if (event.getSource() instanceof JTabbedPane)
            {
                /*
                 * If the first tab was clicked, disable the Create button.
                 */
                if (((JTabbedPane)event.getSource()).getSelectedIndex() == 0)
                {
                    m_btnCreate.setEnabled(false);
                }
                /*
                 * If the second tab was clicked, call a function that finds out
                 * if there's text in the variable and JCX name boxes, and 
                 * enables or disables the Create button accordingly.
                 */
                else if (((JTabbedPane)event.getSource()).getSelectedIndex() == 1)
                {
                    checkForEntries();
                }
            }
            /*
             * If a the Create or Cancel button was clicked, handle it with the
             * evaluateDialog function.
             */
            else if (event.getSource() instanceof JButton)
            {
                String componentName = ((JComponent)event.getSource()).getName();
                evaluateDialog(componentName);
            }
        }

        // These methods aren't needed in this dialog.
        public void mouseEntered(MouseEvent event){}
        public void mouseExited(MouseEvent event){}
        public void mousePressed(MouseEvent event){}
        public void mouseReleased(MouseEvent event){}
    }    
    
    /**
     * A listener to handle ENTER and ESC keystrokes. A key event dispatcher
     * handles keystrokes before they can be consumed by other components in the 
     * user interface.
     */
    private class DialogKeyEventDispatcher implements KeyEventDispatcher
    {
        /**
         * Receives key strokes sent to the dialog, handling only the ENTER
         * and ESC keys.
         * 
         * @param event The details of the event.
         */
        public boolean dispatchKeyEvent(KeyEvent event)
        {
            /*
             * Handle an ENTER keystroke.
             */
            if (event.getKeyCode() == KeyEvent.VK_ENTER)
            {
                /*
                 * If the Create button is enabled, then the second tab must be 
                 * visible and there are values in the variable and JCX name
                 * boxes. Do the same name value checking that would be done if the
                 * Create button had been clicked.
                 */
                if (m_btnCreate.isEnabled())
                {
                    String componentName = ((JComponent)event.getSource()).getName();
                    evaluateDialog("create_button");
                }
                /*
                 * If the current configuration specifies creating a new JCX file, then
                 * the user reached this dialog through File -> New -> Java Control. Just
                 * signal that the control may be added, and close the dialog.
                 */                
                if (m_wizard.m_requestedConfig == ControlWizard.CONFIG_CREATE_EXTENSION_FILE)
                {
                    m_controlInsertRequested = true;
                    closeDialog(null);
                }
            }
            /*
             * Handle an ESC keystroke. Signal that the control may not be inserted
             * and close the dialog.
             */
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                m_controlInsertRequested = false;
                closeDialog(null);
            }
            return false;
        }        
    }
    
    /**
     * A listener to handle keystrokes in the variable and JCX name 
     * text boxes. All three methods of this class simply call a function
     * that enables or disables the Create button depending on whether 
     * there's text in the boxes.
     */
    private class DialogDocumentListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent event)
        {
            checkForEntries();
        }
        public void insertUpdate(DocumentEvent event)
        {
            checkForEntries();
        }
        public void removeUpdate(DocumentEvent event)
        {
            checkForEntries();
        }
    }
} 
