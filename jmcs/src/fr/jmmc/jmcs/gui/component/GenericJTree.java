/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This custom JTree implementation provides several utility methods to manipulate
 * DefaultMutableTreeNode and visual representation of nodes
 * @param <E> type of the user object 
 *
 * @author bourgesl
 */
public abstract class GenericJTree<E> extends JTree {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class logger */
    protected static final Logger logger = LoggerFactory.getLogger(GenericJTree.class.getName());
    /* members */
    /** class corresponding to <E> generic type */
    private final Class<E> classType;

    /**
     * Public constructor changing default values : SINGLE_TREE_SELECTION
     * 
     * @param classType class corresponding to <E> generic type
     */
    public GenericJTree(final Class<E> classType) {
        super(new DefaultMutableTreeNode("GenericJTree"), false);

        this.classType = classType;

        // single tree selection :
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    /**
     * Return the tree model
     * @return tree model
     */
    public final DefaultTreeModel getTreeModel() {
        return ((DefaultTreeModel) this.getModel());
    }

    /**
     * Create a new node using the given user object and add it to the given parent node
     * @param parentNode parent node
     * @param userObject user object to create the new node
     * @return new created node
     */
    public final DefaultMutableTreeNode addNode(final DefaultMutableTreeNode parentNode, final E userObject) {
        final DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(userObject);

        parentNode.add(modelNode);

        return modelNode;
    }

    /**
     * Create a new node using the given user object and add it to the given parent node
     * and Fire node structure changed on the parent node
     * @param parentNode parent node
     * @param userObject user object to create the new node
     */
    public final void addNodeAndRefresh(final DefaultMutableTreeNode parentNode, final E userObject) {
        final DefaultMutableTreeNode newNode = this.addNode(parentNode, userObject);

        // fire node structure changed :
        this.fireNodeChanged(parentNode);

        // Select the new node = model :
        this.selectPath(new TreePath(newNode.getPath()));
    }

    /**
     * Remove the given current node from the parent node
     * and Fire node structure changed on the parent node
     * @param parentNode parent node
     * @param currentNode node to remove
     */
    public final void removeNodeAndRefresh(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode currentNode) {
        this.removeNodeAndRefresh(parentNode, currentNode, true);
    }

    /**
     * Remove the given current node from the parent node
     * and Fire node structure changed on the parent node
     * @param parentNode parent node
     * @param currentNode node to remove
     * @param doSelectParent flag to indicate to select the parent node once the node removed
     */
    public final void removeNodeAndRefresh(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode currentNode, final boolean doSelectParent) {
        parentNode.remove(currentNode);

        // fire node structure changed :
        this.fireNodeChanged(parentNode);

        if (doSelectParent) {
            // Select the parent node = target :
            this.selectPath(new TreePath(parentNode.getPath()));
        }
    }

    /**
     * Fire node structure changed on the given tree node
     * @param node changed tree node
     */
    public final void fireNodeChanged(final TreeNode node) {
        // fire node structure changed :
        this.getTreeModel().nodeStructureChanged(node);
    }

    /**
     * Return the root node
     * @return root node
     */
    public final DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) getTreeModel().getRoot();
    }

    /**
     * Return the parent node of the given node
     * @param node node to use
     * @return parent node
     */
    public final DefaultMutableTreeNode getParentNode(final DefaultMutableTreeNode node) {
        return (DefaultMutableTreeNode) node.getParent();
    }

    /**
     * Return the node corresponding to the last selected path in the tree
     * @return node or null
     */
    public final DefaultMutableTreeNode getLastSelectedNode() {
        return (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
    }

    /**
     * Find the first tree node having the given user object
     * @param userObject user object to locate in the tree
     * @return tree node or null
     */
    public final DefaultMutableTreeNode findTreeNode(final E userObject) {
        return findTreeNode(getRootNode(), userObject);
    }

    /**
     * Find the first tree node having the given user object recursively
     *
     * @param node current node to traverse
     * @param userObject user object to locate in the tree
     * @return tree node or null
     */
    public static DefaultMutableTreeNode findTreeNode(final DefaultMutableTreeNode node, final Object userObject) {
        if (node.getUserObject() == userObject) {
            return node;
        }

        final int size = node.getChildCount();
        if (size > 0) {
            DefaultMutableTreeNode result = null;

            DefaultMutableTreeNode childNode;
            for (int i = 0; i < size; i++) {
                childNode = (DefaultMutableTreeNode) node.getChildAt(i);

                result = findTreeNode(childNode, userObject);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Select the first child node
     * @param rootNode root node
     */
    public final void selectFirstChildNode(final DefaultMutableTreeNode rootNode) {
        if (rootNode.isLeaf()) {
            return;
        }

        // first child = target :
        final DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) rootNode.getFirstChild();
        this.selectPath(new TreePath(firstChild.getPath()));

        // expand node if there is at least one child node :
        if (!firstChild.isLeaf()) {
            final DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) firstChild.getFirstChild();

            this.scrollPathToVisible(new TreePath(secondChild.getPath()));
        }
    }

    /**
     * Change the selected path in the tree
     * This will send a selection event changed that will refresh the UI
     *
     * @param path tree path
     */
    public final void selectPath(final TreePath path) {
        this.setSelectionPath(path);
        this.scrollPathToVisible(path);
    }

    /**
     * Expand or collapse all nodes in the tree
     * @param expand true to expand all or collapse all
     */
    public void expandAll(final boolean expand) {
        // Traverse tree from root
        expandAll(new TreePath(getRootNode()), expand, false);
    }

    /**
     * Expand or collapse all nodes starting from the given parent (path)
     * @param parent parent path
     * @param expand true to expand all or collapse all
     * @param process flag to process this parent node (useful for root node)
     */
    public void expandAll(final TreePath parent, final boolean expand, final boolean process) {
        // Traverse children
        final TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            TreePath path;
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                path = parent.pathByAddingChild(e.nextElement());

                // recursive call:
                expandAll(path, expand, true);
            }
        }

        if (process) {
            // Expansion or collapse must be done bottom-up
            if (expand) {
                expandPath(parent);
            } else {
                collapsePath(parent);
            }
        }
    }

    /**
     * Called by the renderers to convert the specified value to
     * text. This implementation returns <code>value.toString</code>, ignoring
     * all other arguments. To control the conversion, subclass this
     * method and use any of the arguments you need.
     *
     * @param value the <code>Object</code> to convert to text
     * @param selected true if the node is selected
     * @param expanded true if the node is expanded
     * @param leaf  true if the node is a leaf node
     * @param row  an integer specifying the node's display row, where 0 is
     *             the first row in the display
     * @param hasFocus true if the node has the focus
     * @return the <code>String</code> representation of the node's value
     */
    @Override
    @SuppressWarnings("unchecked")
    public final String convertValueToText(
            final Object value,
            final boolean selected,
            final boolean expanded, final boolean leaf, final int row,
            final boolean hasFocus) {

        if (value != null) {
            String sValue = null;

            if (value instanceof DefaultMutableTreeNode) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final Object userObject = node.getUserObject();

                if (userObject != null) {
                    sValue = convertObjectToString(userObject);
                }

            } else {
                logger.error("unsupported class type = {}", value.getClass());

                sValue = value.toString();
            }

            if (sValue != null) {
                return sValue;
            }

        }
        return "";
    }

    /**
     * Convert a non-null value object to string
     * @param userObject user object to convert
     * @return string representation of the user object
     */
    @SuppressWarnings("unchecked")
    private final String convertObjectToString(final Object userObject) {
        // Check first for string (root node and default tree model):
        if (userObject instanceof String) {
            return userObject.toString();
        }
        // Check if the class type matches (exact class comparison):
        if (this.classType == null || this.classType.isAssignableFrom(userObject.getClass())) {
            return convertUserObjectToString((E) userObject);
        }
        return toString(userObject);
    }

    /**
     * Default toString() conversion
     * @param userObject user object to convert
     * @return string representation of the user object
     */
    protected final String toString(final Object userObject) {
        if (!(userObject instanceof String)) {
            logger.warn("Unsupported class type = {}", userObject.getClass());
        }
        // String representation :
        return userObject.toString();
    }

    /**
     * Convert a non-null value object to string
     * @param userObject user object to convert
     * @return string representation of the user object
     */
    protected abstract String convertUserObjectToString(final E userObject);
}
