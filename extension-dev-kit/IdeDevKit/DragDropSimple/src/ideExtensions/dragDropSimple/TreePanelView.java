package ideExtensions.dragDropSimple;

import com.bea.ide.core.datatransfer.DataTransferSvc;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

/**
 * A frame view that displays a tree with example data for dragging
 * and dropping. This view registers the tree as a participant in
 * drag/drop operations using the data transfer service.
 */
public class TreePanelView extends JPanel
        implements com.bea.ide.ui.frame.IFrameView
{
    /**
     * Called by the IDE to retrieve the user interface that should be
     * displayed in the frame.
     *
     * @param viewId The id, if any, for the frame specified in the frame's
     * extension.xml file.
     * @return The user interface to display within the frame's
     * boundaries.
     */
    public Component getView(String viewId)
    {
        return this;
    }

    /**
     * Called by the IDE to discover whether this frame is available
     * for display; only called if the frame's extension.xml includes
     * the askavailable attribute set to "true".
     *
     * @return <code>true</code> if the frame should be available;
     * <code>false</code> if it should not be.
     */
    public boolean isAvailable()
    {
        return true;
    }

    /**
     * Constructs this view panel by creating a new instance of the
     * SimpleTree and placing it within a scrolling pane. This code
     * also registers the tree contained by this panel as supporting
     * drag/drop actions, specifying the drag/drop driver to use.
     */
    public TreePanelView()
    {
        super(new GridLayout(1, 0));
        SimpleTree tree = new SimpleTree();
        JScrollPane treePane = new JScrollPane(tree);
        this.add(treePane);

        /**
         * Register a drag/drop driver for the tree, specifying that
         * this is an action to copy data from source to target (rather
         * than, say, a move action) and that a drag image should be
         * used.
         */
        DataTransferSvc.get().registerDnDSupport(tree,
                new SimpleTreeDragDropDriver(),
                javax.swing.TransferHandler.COPY,
                true);
    }
}
