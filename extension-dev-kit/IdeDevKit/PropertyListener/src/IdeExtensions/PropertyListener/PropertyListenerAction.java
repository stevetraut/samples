package IdeExtensions.PropertyListener;

import com.bea.ide.Application;
import com.bea.ide.actions.DefaultAction;
import com.bea.ide.document.IDocument;
import com.bea.ide.ui.IURISelectionContext;
import com.bea.ide.ui.output.OutputMessage;
import com.bea.ide.ui.output.OutputSvc;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

/**
 * An action that 
 */
public class PropertyListenerAction extends DefaultAction
{
    private OutputSvc.IOutputWindow m_win;
    private OutputSvc.OutputWindowDescription m_desc;

    private static final Color DARK_CAMEL = new Color(221, 189, 127);
    private static final Color DARK_RED = new Color(123, 49, 14);

    private PropertyChangeListener m_focusedURIsListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            // list all the files that are selected in the application pane.
            Object o = e.getNewValue();
            if (o != null && o instanceof IURISelectionContext)
            {
                IURISelectionContext c = (IURISelectionContext) o;
                URI[] uris = c.getFileURIs();
                if (uris.length > 0)
                {
                    m_win.addMessage("The following files are selected in the Application tree:");
                    for (int i = 0; i < uris.length; i++)
                    {
                        m_win.addMessage(new OutputMessage(getAppRelativePath(uris[i]), DARK_CAMEL, null));
                    }
                    m_win.addMessage("");
                }
            }
        }
    };

    private PropertyChangeListener m_documentDirtyChangeListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            // report whether the given document is in a dirty or clean state.
            Object o = e.getNewValue();
            if (o != null && o instanceof IDocument)
            {
                IDocument doc = (IDocument) o;
                Boolean dirty = (Boolean) doc.getProperty(IDocument.PROP_DocumentDirty);

                m_win.addMessage("The following document was just " + (dirty.booleanValue() ? "dirtied" : "saved") + ":");
                m_win.addMessage(new OutputMessage(getAppRelativePath(doc.getURI()), DARK_RED, null));
                m_win.addMessage("");
            }
        }
    };

    /**
     * get the path of the uri relative to the application root.
     * assumes the URI represents a file.
     */
    private String getAppRelativePath(URI uri)
    {
        if (Application.getWorkspace() == null)
        {
            return "";
        }
        URI appURI = Application.getWorkspace().getDirectory().getAbsoluteURI();
        return "$APP_HOME/" + uri.toString().substring(appURI.toString().length());
    }

    public PropertyListenerAction()
    {
        // initialize the output window.
        m_desc = new OutputSvc.OutputWindowDescription();
        m_desc.title = "PropertyListener";
        m_desc.destination = "PropertyListener";
        m_win = OutputSvc.get().getWindow(m_desc, true, false);

        // add listeners for the properties we are interested in.
        Application.get().addPropertyChangeListener(Application.EVENT_DocumentDirtyChange, m_documentDirtyChangeListener);
        Application.get().addPropertyChangeListener(Application.PROP_FocusedURIs, m_focusedURIsListener);
    }

    public void actionPerformed(ActionEvent e)
    {
        m_win = OutputSvc.get().getWindow(m_desc, true, false);
    }
}
