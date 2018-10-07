package ideExtensions.dragDropSimple;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A Java Swing tree that displays example data for illustrating
 * drag and drop in the WebLogic Workshop IDE. This class builds
 * the tree using instances of DefaultMutableTreeNode, starting
 * with an instance as the tree's root and adding children along the
 * way. One useful aspect of this class is that it supports specifying
 * instances of a "user object" that can live in each node to hold the
 * node's data.
 */
public class SimpleTree extends JTree
{
    // Create the tree's root node.
    static DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("Item List");

    /**
     * Constructs an instance of the tree by starting with the root
     * node and adding child nodes.
     */
    public SimpleTree()
    {
        super(top);
        createNodes(top);

        // Only one node may be selected at a time.
        this.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    /**
     * A "user object" class to contain the data for each node in the tree.
     * A new instance of this class is constructed for each data-carrying
     * node in the tree.
     */
    public class ItemInfo
    {
        public String m_itemName;
        public String m_itemDescription;

        // Constructs the object with simple data.
        public ItemInfo(String item, String description)
        {
            m_itemName = item;
            m_itemDescription = description;
        }

        /**
         * Called to retrieve the data that should be displayed as label
         * for the tree node that this user object instance belongs to.
         *
         * @return The label string.
         */
        public String toString()
        {
            return m_itemName;
        }

        /**
         * Gets a string containing the node's label and its description.
         * This method is called to retrieve data for copying to a drop
         * target.
         *
         * @return The data for the node to which this user object
         * belongs.
         */
        public String getItemData()
        {
            return "Item name: " + m_itemName + "\n" +
                    "Item description: " + m_itemDescription;
        }
    }

    /**
     * Builds the portion of the tree beneath the root by adding
     * DefaultMutableTreeNode instances, each with separate
     * instances of the ItemInfo user object. Two subcategories are
     * also added. The nonsensical data represents item names and their
     * descriptions.
     *
     * @param top The tree's top node.
     */
    private void createNodes(DefaultMutableTreeNode top)
    {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode item = null;

        category = new DefaultMutableTreeNode("Category 1");
        top.add(category);

        item = new DefaultMutableTreeNode(new ItemInfo
                ("flangie",
                        "Fully-functional complement to our zinc-full protaic."));
        category.add(item);

        item = new DefaultMutableTreeNode(new ItemInfo
                ("callioscim",
                        "A must for any truly robust melianic hisc, whether " +
                            "in or out of frigid environs."));
        category.add(item);

        category = new DefaultMutableTreeNode("Category 2");
        top.add(category);

        item = new DefaultMutableTreeNode(new ItemInfo
                ("protaic",
                        "Affix this to a flangie to see an manifold increase " +
                            "in performant particulates."));
        category.add(item);

        item = new DefaultMutableTreeNode(new ItemInfo
                ("melianic hisc",
                        "Fully supports trending the calloscim to maximum " +
                            "post-pattern joining."));
        category.add(item);
    }
}
