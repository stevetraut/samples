package propEditor.ide; 

import java.util.StringTokenizer;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import com.bea.ide.control.EditorSupport;
import javax.swing.JTextPane;
import com.bea.ide.control.AttributeEditorSimple;
import com.bea.control.Issue;
import com.bea.control.DefaultIssue;

/*
 * Represents an attribute editing/validation dialog for the
 * customer-id attribute. 
 */
public class CustomerIdEditorSimple extends JPanel implements AttributeEditorSimple
{ 
    private JTextField m_field;
    
    /*
     * Constructs this dialog using the existing value for 
     * the customer-id attribute.
     */
    public CustomerIdEditorSimple(String origValue)
    {
        super(new BorderLayout());
        m_field = new JTextField(origValue);
        String messageText = "This sample supports the following values: \n" + 
                             "987654    987655 \n" +
                             "987658    987659 \n"
                             +"that's all \n";
        JTextPane valueMessage = new JTextPane();
        valueMessage.setText(messageText);
        valueMessage.setBackground(null);
        valueMessage.setEditable(false);
        this.add(m_field, BorderLayout.SOUTH);
        this.add(valueMessage, BorderLayout.NORTH);
     
    }

    public JFormattedTextField.AbstractFormatter getFormatter()
    {
        return null;
    }

    /*
     * Returns the component to use for the customer-id
     * editing dialog. The IDE calls this method to display
     * the dialog.
     */
    public Component getEditorComponent()
    {
        return this;
    }

    /*
     * Provides a way for the IDE to retrieve the newly
     * entered attribute value.
     */
    public String getNewAttributeValue()
    {
        return m_field.getText();
    }

    /*
     * Provides a place for code that should execute when the
     * user clicks OK. This method calls code that validates the
     * attribute value, ensuring that it is a six-digit numeric
     * value.
     */
    public Issue[] onFinish()
    {
        CustIdValidator cIdV = new CustIdValidator();
        return cIdV.validateId(getNewAttributeValue());  
    }

    /*
     * Removes any nonnumeric characters in the new attribute value.
     */
    private String removeNonNumeric(String stringNumber) {
        StringBuffer delimiters = new StringBuffer();
        StringBuffer cleanString = new StringBuffer();

        for (int i = 0; i < stringNumber.length(); i++) {
            if (stringNumber.charAt(i) < '0' || stringNumber.charAt(i) > '9')
                delimiters.append(stringNumber.charAt(i));
        }
        StringTokenizer tokens = new StringTokenizer(stringNumber, delimiters.toString());

        while(tokens.hasMoreTokens()) {
            cleanString.append(tokens.nextToken());
        }
        return cleanString.toString();
    }
} 
