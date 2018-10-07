package ideExtensions.dragDropSimple;

import com.bea.ide.core.MessageSvc;
import com.bea.ide.core.ResourceSvc;
import com.bea.ide.core.datatransfer.DefaultDragDropDriver;
import ideExtensions.dragDropSimple.SimpleTree.ItemInfo;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A drag/drop driver that supports copying data from the simple
 * tree to a document source. This drag/drop driver is registered by
 * the TreePanelView as the tree's driver for drag/drop operations.
 * This driver knows how to extract data from the tree that will be
 * copied to the drop source.
 */
public class SimpleTreeDragDropDriver extends DefaultDragDropDriver
{
    /**
     * Called by the IDE to get the IDragSourceInfo instance through which
     * the IDE can retrieve the data to copy from the drag source to
     * the drop target.
     *
     * @param component The drag source.
     * @param point The point representing the start of the drag.
     * @return The drag source info containing transfer data.
     */
    public IDragSourceInfo getDragInfo(Component component, Point point)
    {
        IDragSourceInfo dragInfo = null;
        try
        {
            /**
             * If there's anything selected in the tree, construct a draginfo
             * from the selected node. Note that this tree does not support
             * multiple selections; see SimpleTree for more information.
             */
            SimpleTree simpleTree = (SimpleTree) component;
            TreeSelectionModel selectionModel = simpleTree.getSelectionModel();
            int selectionCount = selectionModel.getSelectionCount();
            switch (selectionCount)
            {
                case (0):
                    break;
                case (1):
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) (selectionModel.getSelectionPath().getLastPathComponent());
                    dragInfo = new TreeViewDragInfo(node);
                    break;
                default:
            }
            return dragInfo;
        } catch (Exception e)
        {
            MessageSvc.get().displayError("Error while dragging: " +
                    e.getMessage(), 1);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A drag info class to support copying data from the simple tree
     * to a document.
     */
    class TreeViewDragInfo implements IDragSourceInfo
    {
        private String m_infoData = null;

        /**
         * Constructs an instance of this class with a node from the simple
         * tree, extracting data from the node for copying to the drop
         * target.
         *
         * @param selection The selected tree node.
         */
        public TreeViewDragInfo(DefaultMutableTreeNode selection)
        {
            // Retrieve the object that contains node data.
            Object itemInfo = selection.getUserObject();
            /**
             * If the node is a leaf (that is, if it has no children) then simply
             * get its data out as a string for copying to the drop target.
             */
            if (selection.isLeaf())
            {
                m_infoData = ((ItemInfo) itemInfo).getItemData();
           /**
            * If the node is not a leaf (it has children) then collect the
            * data from each of its children for copying to the drop target.
            */
            } else
            {
                StringBuffer infoBuffer = new StringBuffer();
                int count = selection.getChildCount();
                for (int i = 0; i < count; i++)
                {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) selection.getChildAt(i);
                    String infoData = ((ItemInfo) child.getUserObject()).getItemData();
                    infoBuffer.append(infoData + "\n\n");
                }
                m_infoData = infoBuffer.toString();
            }
        }

        /**
         * Called by the IDE to retrieve the image that should be displayed
         * with the pointer during dragging.
         *
         * @return The image to display.
         */
        public Image getDragImage()
        {
            return ResourceSvc.get().getImage("images/txt.gif");
        }

        /**
         * Called by the IDE to retrieve the icon image's offset from the
         * mouse pointer.
         *
         * @return The offset point.
         */
        public Point getIconOffset()
        {
            return new Point(0, 0);
        }

        /**
         * Called by the IDE to retrieve the Transferable instance
         * that contains data to be copied (transferred) from the
         * drag source to the drop target. This method uses the string
         * extracted from the selected simple tree node to construct
         * an instance of StringSelection, which implements Transferable.
         *
         * @return The Transferable instance.
         */
        public Transferable getTransferable()
        {
            Transferable dropData = new StringSelection(m_infoData);
            return dropData;
        }
    }
}
