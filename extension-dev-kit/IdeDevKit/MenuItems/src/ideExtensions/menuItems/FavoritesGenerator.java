package ideExtensions.menuItems;

import com.bea.ide.Application;
import com.bea.ide.actions.ActionSvc;
import com.bea.ide.actions.IActionContainer;
import com.bea.ide.actions.IActionProxy;
import com.bea.ide.actions.IGenerator;
import com.bea.ide.core.PreferencesSvc;

/**
 * A generator class to generate "favorites" menu items. A generator
 * class implements IGenerator to create menu items dynamically.
 * This is in contrast to the menu commands created statically in
 * an extension.xml file.
 *
 * The extension.xml file specifies this class as the generator to use
 * for the "favorites" menu.
 */
public class FavoritesGenerator implements IGenerator
{
    /**
     * Called by the IDE to discover whether this generator's actions
     * should be visible. Some implementation may want to check
     * the IDE's context to determine the value to return.
     *
     * @return <code>true</code> to make the action visible; otherwise,
     * <code>false</code>.
     */
    public boolean isVisible()
    {
        return true;
    }

    /**
     * Populates the menu items from the entries stored in the "favorites"
     * node in the $USER_HOME/.workshop.pref file. The <em>menu</em>
     * received here is specified in this extension's extension.xml. There,
     * an <action-group> element's generator attribute assigns this class
     * as the generator to use for a submenu of the "favorites" menu.
     *
     * @param menu The menu that should be populated.
     * @return <code>true</code> if items were added; <code>false</code>
     * if none were added.
     */
    public boolean populate(IActionContainer menu)
    {
        /**
         * The preferences service provides easy access to the .workshop.pref
         * file so that extensions can use it to store settings.
         */
        PreferencesSvc.IPreferencePkg prefs =
                PreferencesSvc.get().getUserPreferences(Application.get(),
                        Application.class).getPackageForNode("favorites");

        /**
         * Iterate through the stored "favorites" preferences, adding each
         * as a menu command.
         */
        int favNum = 0;
        String url;
        while ((url = prefs.get("favorites" + (++favNum))) != null)
        {
            // Create a new UI object to represent "favorites" menu command.
            IActionProxy ap = new LaunchBrowserAction().getProxy();
            // Assign a label and URL to the menu UI.
            ap.putValue(IActionProxy.PROP_Label, url);
            // Add the new menu to menu received from the IDE.
            menu.add(ap);
        }

        /**
         * Create a "Delete Favorite" menu command and enable it if
         * there are any favorites.
         */
        IActionProxy toggle =
                ActionSvc.get().getAction(DeleteFavoriteAction.class.getName());
        if (favNum == 1)
        {
            toggle.setEnabled(false);
        } else
        {
            toggle.setEnabled(true);
        }

        return true;
    }
}
