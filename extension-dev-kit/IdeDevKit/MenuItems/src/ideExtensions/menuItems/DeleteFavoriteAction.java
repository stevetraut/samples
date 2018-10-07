package ideExtensions.menuItems;

import com.bea.ide.Application;
import com.bea.ide.actions.ActionSvc;
import com.bea.ide.actions.DefaultAction;

import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * An action behind a "Delete Favorite" menu command. See the comments
 * in AddFavoriteAction for more information on the action API.
 */
public class DeleteFavoriteAction extends DefaultAction
{
    /**
     * Deletes an item from the "favorites" menu. This method is
     * called by the IDE when the user clicks the "Delete Favorite"
     * menu command.
     *
     * The extension.xml specifies this class as the logic for the "Delete
     * Favorites" menu command.
     *
     * @param e The menu click event that occurred in the IDE.
     */
    public void actionPerformed(ActionEvent e)
    {
        // Start creation of a panel for display in a JOptionPane.
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Yo. Which link gets dissed?");
        JComboBox box = new JComboBox();
        box.setEditable(false);

        // Grab the node that contains all the user prefs for favorite links.
        Preferences prefs =
                Application.get().userNodeForPackage(Application.class).node("favorites");

        // Populate the JComboBox with the individual links
        int favnum = 0;
        while (prefs.get("favorites" + (++favnum), null) != null)
        {
            box.addItem(prefs.get("favorites" + favnum, null));
        }

        // Finish creation of the panel for display in the following JOptionPane.
        panel.add(label);
        panel.add(box);

        if (JOptionPane.showConfirmDialog(Application.getRootFrame(),
                panel,
                "Demo Remove Wizard",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null) == JOptionPane.OK_OPTION)
        {
            // Remove the selected link from the node.
            int index = box.getSelectedIndex() + 1;
            prefs.remove("favorites" + index);

            // Shift any remaining links up one in the indexing.
            while (prefs.get("favorites" + (++index), null) != null)
            {
                String value = prefs.get("favorites" + index, null);
                prefs.remove("favorites" + index);
                prefs.put("favorites" + (index - 1), value);
            }

            // Refresh the menu generator to remove the deleted entry.
            ActionSvc.get().refreshGenerator(FavoritesGenerator.class.getName());
        }
    }
}
