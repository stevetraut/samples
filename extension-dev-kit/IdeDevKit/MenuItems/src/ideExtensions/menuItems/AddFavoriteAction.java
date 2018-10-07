package ideExtensions.menuItems;

import com.bea.ide.Application;
import com.bea.ide.actions.ActionSvc;
import com.bea.ide.actions.DefaultAction;
import com.bea.ide.core.PreferencesSvc;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * An action behind an "Add Favorite" menu command. The command
 * adds a "favorite" URL menu command to a "Favorites"
 * menu in the IDE. This class extends DefaultAction, which in turn
 * implements IAction. Action extensions such as menu commands and
 * toolbar buttons implement the IAction interface's actionPerformed
 * method to provide logic for the action. The IDE calls actionPerformed
 * when the command is selected by the IDE's user.
 *
 * The extension.xml file provided in the META-INF folder of the MenuItems
 * sample specifies this class as the handler for the "Add Favorite" action.
 */
public class AddFavoriteAction extends DefaultAction
{
    /**
     * Adds a "favorite" URL to the "Favorites" menu provided by the
     * MenuItems sample. This method is called by the IDE when the user
     * clicks the "Favorites -> Add Favorite" menu.
     *
     * @param e The menu click event that occurred in the IDE.
     */
    public void actionPerformed(ActionEvent e)
    {
        /**
         * Start creating the user interface for a dialog to prompt the
         * user for a URL to add. The panel contains the text box, then is
         * added to a dialog below.
         */
        JPanel panel = new JPanel();
        JTextField textField = new JTextField(25);
        textField.setText("http://");
        textField.setCaretPosition(7);
        JLabel label = new JLabel("Yo. Enter a favorite web link: ");
        panel.add(label);
        panel.add(textField);

        /**
         * Display a simple dialog that includes the panel created above. If
         * the user clicks OK (rather than Cancel), then proceed with the
         * following.
         */
        if (JOptionPane.showConfirmDialog(Application.getRootFrame(),
                panel,
                "Demo Insert Wizard",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null) == JOptionPane.OK_OPTION)
        {
            /**
             * Use the preferences service to get any favorites already saved
             * to $USER_HOME/.workshop.pref.
             */
            PreferencesSvc.IPreferencePkg prefs =
                    PreferencesSvc.get().getUserPreferences(Application.get(),
                            Application.class).getPackageForNode("favorites");

            /**
             * Favorites are stored in .workshop.pref as <option> elements
             * whose name attribute is a value such as "favorites1" and value
             * attribute is the URL. Find the end of the list and add a new entry
             * with the text in the dialog's text box.
             */
            int favNum = 0;
            while (prefs.get("favorites" + (++favNum)) != null) ;
            prefs.put("favorites" + favNum, textField.getText());

            /**
             * Use the action service to refresh the menu with the complete list
             * using the FavoritesGenerator class included in this extension.
             * See the code in that class for details.
             */
            ActionSvc.get().refreshGenerator(FavoritesGenerator.class.getName());
        }
    }
}
