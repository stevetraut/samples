package jcxCreate.ide; 

import com.bea.ide.control.EditorSupport;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JFormattedTextField;
import com.bea.ide.control.AttributeEditorSimple;
import com.bea.control.Issue;

/*
 * Represent a dialog for editing the expression attribute. This
 * dialog is displayed when the control's user clicks the ... corresponding
 * to the attribute in the Property Editor, or double-clicks a method
 * in the JCX file.
 */
public class QueryExpressionEditorSimple extends javax.swing.JPanel
    implements AttributeEditorSimple
{ 
    
	javax.swing.JEditorPane edpQueryExpression = new javax.swing.JEditorPane();
	javax.swing.JLabel lblXQuery = new javax.swing.JLabel();
	javax.swing.JTextArea txtDescription = new javax.swing.JTextArea();

    /*
     * Call the initComponents method to assemble the dialog's 
     * pieces. Receive the expression attribute original value
     * to display in the dialog.
     */
    public QueryExpressionEditorSimple(String origValue)
    {
        super();
        this.initComponents(origValue);
    }

    public JFormattedTextField.AbstractFormatter getFormatter()
    {
        return null;
    }

    /*
     * Provides a way for WebLogic Workshop to retrieve the dialog's 
     * components for display.
     */
    public Component getEditorComponent()
    {
        return this;
    }

    /*
     * Used by WebLogic Workshop to retrieve the new expression attribute
     * value. edpQueryExpression is the JEditorPane in the dialog box
     * that contains the expression as entered by the control's user.
     */
    public String getNewAttributeValue()
    {
        return edpQueryExpression.getText();
    }

    /*
     * Provides a place for code that should execute when the control's
     * user clicks OK in the expression editor dialog.
     */
    public Issue[] onFinish()
    {
        return null;
    }

    /*
     * Assembles components of the dialog.
     */
	public void initComponents(String value) {
        java.awt.GridBagConstraints gridBagConstraints;
        setLayout(new java.awt.GridBagLayout());
        setEnabled(true);

		txtDescription.setText("Editing your XQuery expression in the box below.");
		txtDescription.setLineWrap(true);
		txtDescription.setBackground(null);
		txtDescription.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints(
            0, 0, 4, 1, 1, 0,
            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 
            new Insets(5, 5, 5, 5), 0, 0);
        add(txtDescription, gridBagConstraints);

		lblXQuery.setText("XQuery expression:");
		lblXQuery.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
        gridBagConstraints = new GridBagConstraints(
            0, 1, 1, 1, 0, 0,
            GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0);
        add(lblXQuery, gridBagConstraints);

        edpQueryExpression.setText(value);
        gridBagConstraints = new GridBagConstraints(
            0, 2, 3, 1, 1, 1,
            GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(5, 0, 5, 5), 0, 0);
        add(edpQueryExpression, gridBagConstraints);  
    }
} 
