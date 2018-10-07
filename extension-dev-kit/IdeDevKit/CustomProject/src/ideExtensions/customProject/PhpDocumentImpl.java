package ideExtensions.customProject; 

import com.bea.ide.core.navigation.INavigationPoint;
import com.bea.ide.document.IDocument;
import com.bea.ide.document.IDocumentView;
import com.bea.ide.document.IDocumentViewInfo;
import com.bea.ide.lang.java.JavaDocument;
import com.bea.ide.lang.text.TextDocument;
import com.bea.ide.sourceeditor.EditorSvc;
import com.bea.ide.sourceeditor.driver.ISourceViewDriver;
import java.awt.Container;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.Icon;

/**
 * A simple document implementation to manage the contents of 
 * a PHP document, providing names for views of the document and 
 * deferring to TextDocument for all other functionality.
 */
public class PhpDocumentImpl extends TextDocument
{ 
    /**
     * Constructs an instance of this class with the 
     * handler class and a URI to the file that is a physical
     * instance of the document. This merely calls the 
     * TextDocument constructor.
     */
    public PhpDocumentImpl(Class handler, URI uriFile) throws IOException
    {
        super(handler, uriFile); 
    }

    /**
     * Called by the IDE to retrieve the views of a PHP 
     * document. This document implementation supports "two" views,
     * source view and design view, although the two are 
     * identical. The class used to define these views is
     * contained within this class.
     */    
    public IDocumentViewInfo[] getViewInfo()
    {
        IDocumentViewInfo[] views = new IDocumentViewInfo[2];    
        PhpViewInfo viewInfo = new PhpViewInfo(this);
        viewInfo.setName("PHP Source View");
        views[0] = viewInfo;
        viewInfo = new PhpViewInfo(this);   
        viewInfo.setName("PHP Design View");
        views[1] = viewInfo;
        return views;
    }

    /**
     * A simple way to provide a view of PHP files. 
     * The getViewInfo method returns instances of this class
     * to provide two view tabs: PHP Source View and 
     * PHP Design View. The views are identical, of course,
     * but this code illustrates how you respond to the IDE's
     * request for views of a document.
     */
    class PhpViewInfo implements IDocumentViewInfo
    {
        
        TextDocument m_doc;
        String m_name;
        
        /**
         * Constructs an instance of this view using the
         * document handler class that contains this class.
         * 
         * @param doc The document handler to build a 
         * view of.
         */
        public PhpViewInfo(TextDocument doc)
        {
            m_doc = doc;   
        }
        /**
         * Sets the name of the view as it should
         * appear on the view's tab in the IDE.
         * 
         * @param name The name to use for the view.
         */
        public void setName(String name)
        {
            m_name = name;   
        }
        /**
         * Called by the IDE to get the name of the view.
         */
        public String getName()
        {
            return m_name;   
        }
        /**
         * Called by the IDE to get the icon that should
         * be displayed for this view.
         */
        public Icon getIcon() { return null; }
        /**
         * Called by the IDE to get this view's description.
         */
        public String getDescription() { return "desc for php view...."; }
        /**
         * Called by the IDE to create an instance of this view.
         */
        public IDocumentView createView() 
        {
           return EditorSvc.get().createSourceView(m_doc);
        }
    }    

}
