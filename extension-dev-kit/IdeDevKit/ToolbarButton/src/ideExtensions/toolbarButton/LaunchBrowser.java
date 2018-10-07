package ideExtensions.toolbarButton;

import com.bea.ide.actions.DefaultAction;
import com.bea.ide.ui.browser.BrowserSvc;
import com.bea.ide.ui.frame.FrameSvc;
import com.bea.ide.core.MessageSvc;

import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * An action behind a toolbar "Favorites" button. The button simply launches
 * a browser with the URL in the code below. This class extends DefaultAction,
 * which in turn implements IAction. Action extensions such as menu
 * commands and toolbar buttons implement the IAction interface's actionPerformed
 * method to provide logic for the action. The IDE calls actionPerformed
 * when the command is selected by the IDE's user.
 *
 * The extension.xml file provided in the META-INF folder of the ToolbarButton
 * sample specifies this class as the handler for the button action.
 */
public class LaunchBrowser extends DefaultAction
{
    /**
     * Launches a browser to the URL given in code. This method is
     * called by the IDE when the user clicks the "Favorites" button.
     *
     * @param e The menu click event that occurred in the IDE.
     */
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            BrowserSvc.get().invokeBrowser(new URL("http://dev2dev.bea.com"), true);
        }
        catch(Exception ex)
        {
            // Display a debugging message in the IDE's Output window.
            MessageSvc.get().debugLog("Error while displaying favorite: "
                + ex.getLocalizedMessage());
        }
    }
}
