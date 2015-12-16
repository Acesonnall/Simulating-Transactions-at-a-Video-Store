package edu.iastate.cs228.hw5;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Omar Taylor
 *
 */

/**
 * 
 * This class implements a splay tree. Add any helper methods or implementation
 * details you'd like to include.
 *
 */

public class SplayTree<E extends Comparable<? super E>> extends AbstractSet<E> {
	protected Node root;
	protected int size;

	private class Node {
		public E data;
		public Node left;
		public Node parent;
		public Node right;

		public Node(E data) {
			this.data = data;
		}

		@Override
		public Node clone() {
			return new Node(data);
		}
	}

	/**
	 * Default constructor constructs an empty tree.
	 */
	public SplayTree() {
		size = 0;
		this.root = null;
	}

	/**
	 * Needs to call addBST() later on to complete tree construction.
	 */
	public SplayTree(E data) {
		this.root = new Node(data);
		size++;
	}

	/**
	 * Copies over an existing splay tree. The entire tree structure must be
	 * copied. No splaying.
	 * 
	 * @param tree
	 */
	public SplayTree(SplayTree<E> tree) {
		root = tree.clone(tree.root);
		size = tree.size;
	}

	/**
	 * This function is here for grading purpose. It is not a good programming
	 * practice. This method is fully implemented and should not be modified.
	 * 
	 * @return root of the splay tree
	 */
	public E getRoot() {
		return size() == 0 ? null : root.data;
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Clear the splay tree.
	 */
	@Override
	public void clear() {
		this.size = 0;
		this.root = null;
	}

	// ----------
	// BST method
	// ----------

	/**
	 * Adds an element to the tree without splaying. The method carries out a
	 * binary search tree addition. It is used for initializing a splay tree.
	 * 
	 * @param data
	 * @return true if addition takes place false otherwise
	 */
	public boolean addBST(E data) {
		if (this.root == null) {
			this.root = new Node(data);
			this.root.parent = null;
			++size;
		}

		Node current = this.root;
		while (true) {
			int comp = current.data.compareTo(data);
			if (comp == 0) {
				// key is already in the tree
				return false;
			} else if (comp > 0) {
				if (current.left != null) {
					current = current.left;
				} else {
					current.left = new Node(data);
					current.left.parent = current;
					++size;
					return true;
				}
			} else {
				if (current.right != null) {
					current = current.right;
				} else {
					current.right = new Node(data);
					current.right.parent = current;
					++size;
					return true;
				}
			}
		}
	}

	// ------------------
	// Splay tree methods
	// ------------------

	/**
	 * Inserts an element into the splay tree. In case the element was not
	 * contained, this creates a new node and splays the tree at the new node.
	 * If the element exists in the tree already, it splays at the node
	 * containing the element.
	 * 
	 * @param data
	 *            element to be inserted
	 * @return true if addition takes place false otherwise (i.e., data is in
	 *         the tree already)
	 */
	@Override
	public boolean add(E data) {
		Node potentialAdd = findEntry(data);

		if (potentialAdd != null) {
			int comp = potentialAdd.data.compareTo(data);
			if (comp == 0) {
				splay(potentialAdd);
				return false;
			} else if (comp > 0) {
				potentialAdd.left = new Node(data);
				potentialAdd.left.parent = potentialAdd;
				++size;
				splay(potentialAdd.left);
				return true;
			} else {
				potentialAdd.right = new Node(data);
				potentialAdd.right.parent = potentialAdd;
				++size;
				splay(potentialAdd.right);
				return true;
			}
		}
		root = new Node(data);
		++size;
		return true;
	}

	/**
	 * Determines whether the tree contains an element. Splays at the node that
	 * stores the element. If the element is not found, splays at the last node
	 * on the search path.
	 * 
	 * @param data
	 *            element to be determined whether to exist in the tree
	 * @return true if the element is contained in the tree false otherwise
	 */
	public boolean contains(E data) {
		Node n = findEntry(data);
		if (n.data.compareTo(data) == 0) {
			splay(n);
			return true;
		} else {
			splay(n);
			return false;
		}
	}

	/**
	 * Splays at a node containing data. Exists for coding convenience, code
	 * readability, and testing purpose.
	 * 
	 * @param data
	 */
	public void splay(E data) {
		contains(data);
	}

	/**
	 * Removes the node that stores an element. Splays at its parent node after
	 * removal (No splay if the removed node was the root.) If the node was not
	 * found, the last node encountered on the search path is splayed to the
	 * root.
	 * 
	 * @param data
	 *            element to be removed from the tree
	 * @return true if the object is removed false if it was not contained in
	 *         the tree
	 */
	public boolean remove(E data) {
		Node toRemove = findEntry(data);
		int comp = toRemove.data.compareTo(data);
		// Node to be removed exists
		if (comp == 0) {
			// Is not the root
			if (toRemove.parent != null) {
				// Is a right child
				if (toRemove.parent.right == toRemove) {
					// Has one or more children
					if (toRemove.left == null || toRemove.right == null) {
						// Has a right child
						if (toRemove.right != null) {
							toRemove.parent.right = toRemove.right;
							toRemove.right.parent = toRemove.parent;
						}
						// Has a left child
						else if (toRemove.left != null) {
							toRemove.parent.right = toRemove.left;
							toRemove.left.parent = toRemove.parent;
						}
						// Has no children
						else {
							toRemove.parent.right = null;
						}
					} else // Has two children
					{
						toRemove.left.parent = null;
						toRemove.right.parent = null;
						toRemove.parent.right = join(toRemove.left, toRemove.right);
					}
				}
				// Is a left child
				else {
					// Has one or more children
					if (toRemove.left == null || toRemove.right == null) {
						// Has a right child
						if (toRemove.right != null) {
							toRemove.parent.left = toRemove.right;
							toRemove.right.parent = toRemove.parent;
						}
						// Has a left child
						else if (toRemove.left != null) {
							toRemove.parent.left = toRemove.left;
							toRemove.left.parent = toRemove.parent;
						}
						// Has no children
						else {
							toRemove.parent.left = null;
						}
					}
					// Has two children
					else {
						toRemove.left.parent = null;
						toRemove.right.parent = null;
						toRemove.parent.left = join(toRemove.left, toRemove.right);
					}
				}
				--size;
				splay(toRemove.parent);
				return true;
			}
			// Is the root
			else {
				// Has one or more children
				if (toRemove.left == null || toRemove.right == null) {
					// Has a right child
					if (toRemove.right != null) {
						this.root = toRemove.right;
					}
					// Has a left child
					else if (toRemove.left != null) {
						this.root = toRemove.left;
					}
					// Has no children
					else {
						this.root = null;
					}
				}
				// Has two children
				else {
					toRemove.left.parent = null;
					toRemove.right.parent = null;
					this.root = join(toRemove.left, toRemove.right);
				}
				--size;
				return true;
			}
		}
		// Node to be removed is not found
		splay(toRemove);
		return false;
	}

	/**
	 * This method finds an element stored in the splay tree that is equal to
	 * data as decided by the compareTo() method of the class E. This is useful
	 * for retrieving the value of a pair <key, value> stored at some node
	 * knowing the key, via a call with a pair <key, ?> where ? can be any
	 * object of E.
	 * 
	 * Splays at the node containing the element or the last node on the search
	 * path.
	 * 
	 * @param data
	 * @return element such that element.compareTo(data) == 0
	 */
	public E findElement(E data) {
		Node potentialFind = findEntry(data);

		if (size == 0) {
			return null;
		} else if (potentialFind.data.compareTo(data) == 0) {
			splay(potentialFind);
			return potentialFind.data;
		} else {
			splay(potentialFind);
			return null;
		}
	}

	/**
	 * Finds the node that stores an element. It is called by several methods
	 * including contains(), add(), remove(), and findElement().
	 * 
	 * No splay at the found node.
	 *
	 * @param data
	 *            element to be searched for
	 * @return node if found or the last node on the search path otherwise null
	 *         if size == 0.
	 */
	protected Node findEntry(E data) {
		Node current = root;
		while (current != null) {
			int comp = current.data.compareTo(data);
			if (comp > 0 && current.left != null) {
				current = current.left;
			} else if (comp < 0 && current.right != null) {
				current = current.right;
			} else
				break;
		}

		return current;
	}

	/**
	 * Join the two subtrees T1 and T2 rooted at root1 and root2 into one. It is
	 * called by remove().
	 * 
	 * Precondition: All elements in T1 are less than those in T2.
	 * 
	 * Access the largest element in T1, and splay at the node to make it the
	 * root of T1. Make T2 the right subtree of T1. The method is called by
	 * remove().
	 * 
	 * @param root1
	 *            root of the subtree T1
	 * @param root2
	 *            root of the subtree T2
	 * @return the root of the joined subtree
	 */
	protected Node join(Node root1, Node root2) {
		while (root1.right != null) {
			root1 = root1.right;
		}
		splay(root1);

		root1.right = root2;
		root2.parent = root1;
		return root1;
	}

	/**
	 * Splay at the current node. This consists of a sequence of zig, zigZig, or
	 * zigZag operations until the current node is moved to the root of the
	 * tree.
	 * 
	 * @param current
	 *            node to splay
	 */
	protected void splay(Node current) {
		while (current != null && current.parent != null) {
			if (current.parent.parent == null) {
				zig(current);
			} else if ((current == current.parent.left) && (current.parent == current.parent.parent.left)
					|| (current == current.parent.right) && (current.parent == current.parent.parent.right)) {
				zigZig(current);
			} else
				zigZag(current);
		}
	}

	/**
	 * This method performs the zig operation on a node. Calls leftRotate() or
	 * rightRotate().
	 * 
	 * @param current
	 *            node to perform the zig operation on
	 */
	protected void zig(Node current) {
		rotate(current);
	}

	/**
	 * This method performs the zig-zig operation on a node. Calls leftRotate()
	 * or rightRotate().
	 * 
	 * @param current
	 *            node to perform the zig-zig operation on
	 */
	protected void zigZig(Node current) {
		rotate(current.parent);
		rotate(current);
	}

	/**
	 * This method performs the zig-zag operation on a node. Calls leftRotate()
	 * or rightRotate() or both.
	 * 
	 * @param current
	 *            node to perform the zig-zag operation on
	 */
	protected void zigZag(Node current) {
		rotate(current);
		rotate(current);
	}

	/**
	 * If a left child: Carries out a left rotation at a node such that after
	 * the rotation its former parent becomes its left child.
	 * 
	 * If a right child: Carries out a right rotation at a node such that after
	 * the rotation its former parent becomes its right child.
	 * 
	 * @param current
	 */
	private void rotate(Node current) {
		Node x = current.parent;
		Node y = x.parent;

		if (current == current.parent.left) {
			// Right rotate
			// Move "current"'s right subtree to its former parent.
			x.left = current.right;
			if (current.right != null)
				current.right.parent = x;

			// Make current's parent become a child of "current".
			current.right = x;
			x.parent = current;

			// Make "current" become a child of parents former parent.
			current.parent = y;
			if (y == null) {
				this.root = current;
			} else if (y.right == x) {
				y.right = current;
			} else {
				y.left = current;
			}
		} else {
			// Left rotate
			// Move "current"'s left subtree to its former parent.
			x.right = current.left;
			if (current.left != null) {
				current.left.parent = x;
			}

			// Make currents parent become a child of "current".
			current.left = x;
			x.parent = current;

			// Make "current" become a child of currents parents former parent.
			current.parent = y;
			if (y == null) {
				root = current;
			} else if (y.right == x) {
				y.right = current;
			} else {
				y.left = current;
			}
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new SplayTreeIterator();
	}

	/**
	 * Write the splay tree according to the format specified in Section 2.2 of
	 * the project description.
	 * 
	 * Calls toStringRec().
	 *
	 */
	@Override
	public String toString() {
		return toStringRec(root, 0);
	}

	private String toStringRec(Node n, int depth) {
		String string = "";
		for (int i = 0; i < 4 * depth; ++i) {
			string += " ";
		}

		if (n == null) {
			return string += "null\n";
		}

		if (n.left == null && n.right == null) {
			return string += n.data + "\n";
		} else {
			string += n.data + "\n" + toStringRec(n.left, depth + 1) + toStringRec(n.right, depth + 1);
		}
		return string;
	}

	/**
	 *
	 * Iterator implementation for this splay tree. The elements are returned in
	 * ascending order according to their natural ordering. All iterator methods
	 * are exactly the same as those for a binary search tree --- no splaying at
	 * any node as the cursor moves.
	 *
	 */
	private class SplayTreeIterator implements Iterator<E> {
		Node cursor;
		Node pending;

		public SplayTreeIterator() {
			// start out at smallest value
			this.cursor = root;
			if (this.cursor != null) {
				while (this.cursor.left != null) {
					this.cursor = this.cursor.left;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return cursor != null;
		}

		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			pending = cursor;
			cursor = successor(cursor);
			return pending.data;
		}

		@Override
		public void remove() {
			if (pending == null)
				throw new IllegalStateException();

			// Remember, current points to the successor of
			// pending, but if pending has two children, then
			// unlinkNode(pending) will copy the successor's data
			// into pending and delete the successor node.
			// So in this case we want to end up with current
			// pointing to the pending node.
			if (pending.left != null && pending.right != null) {
				cursor = pending;
			}
			unlinkNode(pending);
			pending = null;
		}
	}

	// Helper
	private Node successor(Node n) {
		if (n == null) {
			return null;
		} else if (n.right != null) {
			// leftmost entry in right subtree
			Node current = n.right;
			while (current.left != null) {
				current = current.left;
			}
			return current;
		} else {
			// we need to go up the tree to the closest ancestor that is
			// a left child; its parent must be the successor
			Node current = n.parent;
			Node child = n;
			while (current != null && current.right == child) {
				child = current;
				current = current.parent;
			}

			// either current is null, or child is left child of current
			return current;
		}
	}

	private void unlinkNode(Node n) {

		// first deal with the two-child case; copy
		// data from successor up to n, and then delete successor
		// node instead of given node n
		if (n.left != null && n.right != null) {
			Node s = successor(n);
			n.data = s.data;
			n = s; // causes s to be deleted in code below
		}

		// n has at most one child
		Node replacement = null;
		if (n.left != null) {
			replacement = n.left;
		} else if (n.right != null) {
			replacement = n.right;
		}

		// link replacement into tree in place of node n
		// (replacement may be null)
		if (n.parent == null) {
			root = replacement;
		} else {
			if (n == n.parent.left) {
				n.parent.left = replacement;
			} else {
				n.parent.right = replacement;
			}
		}

		if (replacement != null) {
			replacement.parent = n.parent;
		}

		--size;
	}

	private Node clone(Node t) {
		if (t == null)
			return null;

		Node n = new Node(t.data);
		n.left = clone(t.left);
		n.right = clone(t.right);

		return n;
	}
}
