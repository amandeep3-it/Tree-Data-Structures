
import java.util.ArrayList;

public class BinarySearchTree<V extends Comparable<V>, D> {

	/*
		####################
		# Node of the Tree
		####################
	*/

	private class Node<V, D> {
		protected V value = null;
		protected D data = null;
		protected Node<V, D> parent = null, left = null, right = null;

		protected Node(V value) { this(value, null); }

		protected Node(V value, D data) {
			this.value = value;
			this.data = data;
			this.parent = this.left = this.right = null;
		}

		protected Node(V value, D data, Node<V, D> parent) {
			this(value, data);
			this.parent = parent;
			this.left = this.right = null;
		}

		/* Null Check */

		protected boolean isNull() { return (this.value == null); }

		protected boolean notNull() { return (this.value != null); }

		/* Data Check */

		protected boolean hasData() { return (this.data != null); }

		protected boolean noData() { return (this.data == null); }
	}

	private Node<V, D> root = null;

	public BinarySearchTree() { this.root = null; }

	public BinarySearchTree(V... values) {
		this.root = null;
		this.insert(values);
	}

	/*
		###############
		# Finding Node
		###############
	*/

	public boolean has(V... values) {
		for (int i = 0; i < values.length; i++)
			if (this.notNull(this.getNode(values[i]))) return true;
		return false;
	}

	/* Get Node */

	private Node<V, D> getNode(V value) {
		Node<V, D> at = this.root;
		while (this.notNull(at)) {
			if (at.value == value) return at;
			int c = value.compareTo(at.value);
			if (c < 0) at = at.left;
			else if (c > 0) at = at.right;
			else break;
		}
		return null;
	}

	/* Minimum and Maximum Value Node */

	private Node<V, D> minNode(Node<V, D> at) {
		if (this.isNull(at)) return at;
		while (this.notNull(at.left)) at = at.left;
		return at;
	}

	private Node<V, D> maxNode(Node<V, D> at) {
		if (this.isNull(at)) return at;
		while (this.notNull(at.right)) at = at.right;
		return at;
	}

	/* Inorder Successor and Predecessor Node */

	private Node<V, D> getInorderSuccessor(Node<V, D> at) { return this.minNode(at.right); }
	private Node<V, D> getInorderPredecessor(Node<V, D> at) { return this.maxNode(at.left); }

	/* Inorder Successor and Predecessor Height */

	private int getInorderSuccessorHeight(Node<V, D> at) {
		int h = 0;
		if (this.isNull(at, at.right)) return h;
		at = at.right;
		h++;
		while (this.notNull(at.left)) { at = at.left; h++; }
		return h;
	}

	private int getInorderPredecessorHeight(Node<V, D> at) {
		int h = 0;
		if (this.isNull(at, at.left)) return h;
		at = at.left;
		h++;
		while (this.notNull(at.right)) { at = at.right; h++; }
		return h;
	}

	/*
		##########
		# Insert
		##########
	*/

	public boolean insert(V... values) {
		boolean _return = true;
		for (int i = 0; i < values.length; i++)
			if (!this.insert(values[i], null)) _return = false;
		return _return;
	}

	public synchronized boolean insert(V value, D data) {
		Node<V, D> i = new Node<V, D>(value, data), at = this.root, p = null;

		while (this.notNull(at)) {
			p = at;
			if (at.value == i.value) {
				if (at.data != i.data) {
					at.data = i.data;
					return true;
				}
				return false;
			}
			int c = i.value.compareTo(at.value);
			at = (c < 0) ? at.left : at.right;
		}

		if (this.isNull(p)) this.root = i;
		else {
			int c = i.value.compareTo(p.value);
			if (c < 0) p.left = i;
			else p.right = i;
		}
		i.parent = p;

		return true;
	}

	/*
		##########
		# Remove
		##########
	*/

	public synchronized boolean remove(V... values) {
		boolean _return = true;
		for (int i = 0; i < values.length; i++) {
			if (this.height(this.root) <= 1) {
				this.root = null;
				continue;
			} else {
				Node<V, D> r = this.getNode(values[i]);
				if (this.isNull(r) || this.isNull(r.parent, r.left, r.right)) {
					_return = false;
					continue;
				}

				int c = this.notNull(r.parent) ? r.value.compareTo(r.parent.value) : 0;

				if (this.isNull(r.left) && this.isNull(r.right)) {
					if (this.root == r) this.root = null;
					if (c < 0) r.parent.left = null;
					else r.parent.right = null;
				} else if (this.notNull(r.left) && this.isNull(r.right)) {
					if (this.root == r) this.root = r.left;
					r.left.parent = r.parent;
					if (this.notNull(r.parent)) {
						if (c < 0) r.parent.left = r.left;
						else r.parent.right = r.left;
					}
				} else if (this.isNull(r.left) && this.notNull(r.right)) {
					if (this.root == r) this.root = r.right;
					r.right.parent = r.parent;
					if (this.notNull(r.parent)) {
						if (c < 0) r.parent.left = r.right;
						else r.parent.right = r.right;
					}
				} else if (this.notNull(r.left) && this.notNull(r.right)) {
					if (this.getInorderPredecessorHeight(r.left) > this.getInorderSuccessorHeight(r.right)) {
						Node<V, D> s = this.getInorderPredecessor(r);

						if (this.root == r) this.root = s;

						if (s.parent != r) {
							s.parent.right = s.left;
							if (this.notNull(s.parent.right)) s.parent.right.parent = s.parent;
						}

						s.parent = r.parent;
						if (this.notNull(s.parent)) {
							c = s.value.compareTo(s.parent.value);
							if (c < 0) s.parent.left = s;
							else s.parent.right = s;
						}

						if (s != r.left) {
							s.left = r.left;
							s.left.parent = s;
						}

						s.right = r.right;
						if (this.notNull(s.right)) s.right.parent = s;
					} else {
						Node<V, D> s = this.getInorderSuccessor(r);

						if (this.root == r) this.root = s;

						if (s.parent != r) {
							s.parent.left = s.right;
							if (this.notNull(s.parent.left)) s.parent.left.parent = s.parent;
						}

						s.parent = r.parent;
						if (this.notNull(s.parent)) {
							c = s.value.compareTo(s.parent.value);
							if (c < 0) s.parent.left = s;
							else s.parent.right = s;
						}

						if (s != r.right) {
							s.right = r.right;
							s.right.parent = s;
						}

						s.left = r.left;
						if (this.notNull(s.left)) s.left.parent = s;
					}
				}
				if (!_return) _return = true;
			}
		}
		return _return;
	}

	/*
		##########
		# Print
		##########
	*/

	/* Post Order */

	public void printPostOrder() { System.out.println(this.postOrder()); }

	private void printPostOrder(Node<V, D> at) { System.out.println(this.postOrder(at)); }

	public String postOrder() { return this.postOrder(this.root); }

	private String postOrder(Node<V, D> at) {
		if (this.isNull(at)) return "";

		String d = null;
		try { d = at.data.toString(); } catch (Exception e) {}

		String v = (d == null) ? at.value.toString() : ('(' + at.value.toString() + " | " + d + ')'),
					 left = this.postOrder(at.left),
					 right = this.postOrder(at.right);

		if (!right.equals("")) v = right + " " + v;
		if (!left.equals("")) v = left + " " + v;
		return v;
	}

	/* In Order */

	public void printInOrder() { System.out.println(this.inOrder()); }

	private void printInOrder(Node<V, D> at) { System.out.println(this.inOrder(at)); }

	public String inOrder() { return this.inOrder(this.root); }

	private String inOrder(Node<V, D> at) {
		if (this.isNull(at)) return "";

		String d = null;
		try { d = at.data.toString(); } catch (Exception e) {}

		String v = (d == null) ? at.value.toString() : ('(' + at.value.toString() + " | " + d + ')'),
					 left = this.inOrder(at.left),
					 right = this.inOrder(at.right);

		if (!left.equals("")) v = left + " " + v;
		if (!right.equals("")) v += " " + right;
		return v;
	}

	/* Pre Order */

	public void printPreOrder() { System.out.println(this.preOrder()); }

	private void printPreOrder(Node<V, D> at) { System.out.println(this.preOrder(at)); }

	public String preOrder() { return this.preOrder(this.root); }

	private String preOrder(Node<V, D> at) {
		if (this.isNull(at)) return "";

		String d = null;
		try { d = at.data.toString(); } catch (Exception e) {}

		String v = (d == null) ? at.value.toString() : ('(' + at.value.toString() + " | " + d + ')'),
					 left = this.preOrder(at.left),
					 right = this.preOrder(at.right);

		if (!left.equals("")) v += " " + left;
		if (!right.equals("")) v += " " + right;
		return v;
	}

	/* Print Tree */

	public void print() { this.print(this.root); }

	public void print(Node<V, D> at) {
		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		ArrayList<Node<V, D>> nodes = new ArrayList<Node<V, D>>(),
													levels = new ArrayList<Node<V, D>>();

		nodes.add(at);
		int l = 1, widest = 0;

		// Build the printable node
		while (l != 0) {
			ArrayList<String> line = new ArrayList<String>();
			l = 0;

			for (Node<V, D> n : nodes) {
				if (this.isNull(n)) {
					line.add(null);

					levels.add(null);
					levels.add(null);
				} else {
					String d = null;
					try { d = n.data.toString(); } catch (Exception e) {}
					String s = "│ " + n.value.toString() + ((d == null) ? "" : (" | " + d)) + " │";
					line.add(s);
					if (s.length() > widest) widest = s.length();

					levels.add(n.left);
					levels.add(n.right);

					if (this.notNull(n.left)) l++;
					if (this.notNull(n.right)) l++;
				}
			}

			if ((widest % 2) == 1) widest++;

			lines.add(line);

			ArrayList<Node<V, D>> tmp = nodes;
			nodes = levels;
			levels = tmp;
			levels.clear();
		}

		int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
		for (int i = 0; i < lines.size(); i++) {
			ArrayList<String> line = lines.get(i);
			int hpw = ((int) Math.floor(perpiece / 2f)) - 1;

			if (i > 0) {
				for (int j = 0; j < line.size(); j++) {

					// Split Node
					char c = ' ';
					if ((j % 2) == 1) {
						if (line.get(j - 1) != null) {
							c = (line.get(j) != null) ? '┴' : '┘';
						} else {
							if ((j < line.size()) && (line.get(j) != null)) c = '└';
						}
					}
					System.out.print(c);

					// Lines and Spaces
					if (line.get(j) == null) {
						for (int k = 0; k < perpiece - 1; k++) System.out.print(" ");
					} else {
						for (int k = 0; k < hpw; k++) System.out.print(((j % 2) == 0) ? " " : "─");
						System.out.print(((j % 2) == 0) ? "┌" : "┐");
						for (int k = 0; k < hpw; k++) System.out.print(((j % 2) == 0) ? "─" : " ");
					}
				}
				System.out.println();
			}

			// Print the upper part of the Box of the printable node
			for (int j = 0; j < line.size(); j++) {
				String f = line.get(j);
				if (f == null) f = "";
				int left_gap = (int) Math.ceil((perpiece / 2f) - (f.length() / 2f)),
						right_gap = (int) Math.floor((perpiece / 2f) - (f.length() / 2f));

				for (int k = 0; k < left_gap; k++) System.out.print(" ");
				if (f.length() > 0) {
					System.out.print("┌");
					for (int k = 1; k < (f.length() - 1); k++) System.out.print("─");			// ▔
					System.out.print("┐");
				}
				for (int k = 0; k < right_gap; k++) System.out.print(" ");
			}
			System.out.println();

			// Print the printable node
			for (int j = 0; j < line.size(); j++) {
				String f = line.get(j);
				if (f == null) f = "";
				int left_gap = (int) Math.ceil((perpiece / 2f) - (f.length() / 2f)),
						right_gap = (int) Math.floor((perpiece / 2f) - (f.length() / 2f));

				for (int k = 0; k < left_gap; k++) System.out.print(" ");
				System.out.print(f);
				for (int k = 0; k < right_gap; k++) System.out.print(" ");
			}
			System.out.println();

			// Print the lower part of the Box of the printable node
			for (int j = 0; j < line.size(); j++) {
				String f = line.get(j);
				if (f == null) f = "";
				int left_gap = (int) Math.ceil((perpiece / 2f) - (f.length() / 2f)),
						right_gap = (int) Math.floor((perpiece / 2f) - (f.length() / 2f));

				for (int k = 0; k < left_gap; k++) System.out.print(" ");
				if (f.length() > 0) {
					System.out.print("└");
					for (int k = 1; k < (f.length() - 1); k++) System.out.print("─");			// ▁
					System.out.print("┘");
				}
				for (int k = 0; k < right_gap; k++) System.out.print(" ");
			}
			System.out.println();

			perpiece /= 2;
		}
	}

	/* Vertical Print Tree */

	public void verticalPrint() { this.verticalPrint(this.root, false, ""); }

	public void verticalPrint(Node<V, D> node, boolean isLeft, String prefix) {
		if (this.notNull(node)) {
			String d = null;
			try { d = node.data.toString(); } catch (Exception e) {}
			String s = "[ " + node.value.toString() + ((d == null) ? "" : (" | " + d)) + " ]";
			System.out.println(prefix + (isLeft ? "|-- " : "\\-- ") + s);
			this.verticalPrint(node.left, true, prefix + (isLeft ? "|   " : "    "));
			this.verticalPrint(node.right, false, prefix + (isLeft ? "|   " : "    "));
		}
	}

	/*
		##########
		# Others
		##########
	*/

	/* Is Empty */

	public boolean isEmpty() { return this.root == null; }

	public boolean notEmpty() { return this.root != null; }

	/* Is Null */

	private boolean isNull(Node<V, D>... nodes) {
		for (int i = 0; i < nodes.length; i++)
			if ((nodes[i] == null) || nodes[i].isNull()) return true;
		return false;
	}

	private boolean isNull(Node<V, D> node) { return ((node == null) || node.isNull()); }

	/* Is not Null */

	private boolean notNull(Node<V, D>... nodes) {
		for (int i = 0; i < nodes.length; i++)
			if ((nodes[i] != null) && nodes[i].notNull()) return true;
		return false;
	}

	private boolean notNull(Node<V, D> node) { return ((node != null) && node.notNull()); }

	/* Has Data */

	private boolean hasData(Node<V, D>... nodes) {
		for (int i = 0; i < nodes.length; i++)
			if ((nodes[i] != null) || nodes[i].hasData()) return true;
		return false;
	}

	private boolean hasData(Node<V, D> node) { return ((node != null) || node.hasData()); }

	/* Has no Data */

	private boolean noData(Node<V, D>... nodes) {
		for (int i = 0; i < nodes.length; i++)
			if ((nodes[i] == null) || nodes[i].noData()) return true;
		return false;
	}

	private boolean noData(Node<V, D> node) { return ((node == null) || node.noData()); }

	/* Height */

	public int height() { return this.height(this.root); }

	private int height(Node<V, D> at) {
		return (this.isNull(at) ? 0 : (1 + Math.max(this.height(at.left), this.height(at.right))));
	}
}
