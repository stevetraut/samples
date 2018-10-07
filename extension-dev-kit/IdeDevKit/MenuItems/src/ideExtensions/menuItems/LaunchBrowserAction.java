package ideExtensions.menuItems;

import com.bea.ide.ui.*;
import com.bea.ide.actions.DefaultAction;
import com.bea.ide.actions.IActionProxy;
import com.bea.ide.ui.browser.BrowserSvc;
import com.bea.ide.core.MessageSvc;

import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * An action to launch a browser with the URL for a "favorite"
 * in the "Favorites" menu. Along with LaunchBrowserAction1,
 * LaunchBrowserAction2, and LaunchBrowserAction3, this class
 * is specified by the extension.xml as the handler for one of the
 * predefined favorites. See the class FavoritesGenerator for
 * code that dynamically generates favorites based on user
 * preferences.
 */
public class LaunchBrowserAction extends DefaultAction
{
    /**
     * Launches a browser whose URL is the value specified in the
     * menu's label.
     *
     * @param e The menu click event that occurred in the IDE.
     */
    public void actionPerformed(ActionEvent e)
    {
        IActionProxy proxy = getProxy();
        try
        {
            // Get the value of the URL off the action's label attribute in the extension.xml
            URL url = new URL((String) proxy.getValue(IActionProxy.PROP_Label));
            // Use the browser service to display a browser with the URL.
            BrowserSvc.get().invokeBrowser(url, true);
        } catch (Exception ex)
        {
            // Display a debugging message in the IDE's Output window.
            MessageSvc.get().debugLog("Error while displaying favorite: "
                + ex.getLocalizedMessage());
        }
    }
}
