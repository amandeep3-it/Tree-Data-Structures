
import java.util.ArrayList;

public class MaxHeapTree<V extends Comparable<V>, D> {

	/*
		####################
		# Node of the Tree
		####################
	*/

	private class Node<V, D> {
		protected V value = null;
		protected D data = null;

		protected Node(V value) { this(value, null); }

		protected Node(V value, D data) {
			this.value = value;
			this.data = data;
		}

		/* Null Check */

		protected boolean isNull() { return (this.value == null); }

		protected boolean notNull() { return (this.value != null); }

		/* Data Check */

		protected boolean hasData() { return (this.data != null); }

		protected boolean noData() { return (this.data == null); }
	}

	private ArrayList<Node<V, D>> heap = new ArrayList<Node<V, D>>();

	public MaxHeapTree() { this.heap = new ArrayList<Node<V, D>>(); }

	public MaxHeapTree(V... values) {
		this.heap = new ArrayList<Node<V, D>>();
		this.insert(values);
	}

	/*
		###############
		# Finding Node
		###############
	*/

	public boolean has(V... values) {
		for (int i = 0; i < values.length; i++) {
			boolean b = true;
			for (Node<V, D> node : this.heap)
				if (values[i] == node.value) b = false;
			if (b) return false;
		}
		return true;
	}

	/* Get Node */

	private Node<V, D> getNode(V value) {
		for (Node<V, D> node : this.heap) if (node.value == value) return node;
		return null;
	}

	/* Maximum and Minimum Value Node */

	private Node<V, D> maxNode() {
		if (this.isEmpty()) return null;
		return this.heap.get(0);
	}

	public D extractMax() {
		if (this.isEmpty()) return null;
		D v = this.heap.get(0).data;
		this.removeMax();
		return v;
	}

	private Node<V, D> minNode() {
		if (this.isEmpty()) return null;
		return this.heap.get(this.lastIndex());
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
		Node<V, D> i = new Node<V, D>(value, data);
		int index = this.getIndex(i);
		if (this.indexIsValid(index)) {
			Node<V, D> n = this.heap.get(index);
			if (n.data != i.data) {
				n.data = i.data;
				this.heap.set(index, n);
				return true;
			}
			return false;
		}
		this.heap.add(i);
		this.heapifyUp(this.lastIndex());
		return true;
	}

	/*
		##########
		# Remove
		##########
	*/

	public synchronized boolean remove(V... values) {
		for (int i = 0; i < values.length; i++) {
			boolean b = true;
			for (int x = 0; x < this.heap.size(); x++)
				if (values[i] == this.heap.get(x).value) {
					this.heap.set(x, this.heap.get(this.lastIndex()));
					this.heap.remove(this.heap.size() - 1);
					this.heapifyDown(x);
					b = false;
				}
			if (b) return false;
		}
		return true;
	}

	public synchronized boolean removeMax() {
		if (this.isEmpty()) return false;
		this.heap.set(0, this.heap.get(this.lastIndex()));
		this.heap.remove(this.heap.size() - 1);
		this.heapifyDown(0);
		return true;
	}

	/*
		###############
		# Heapify
		###############
	*/

	private void heapifyUp(int index) {
		while ((index >= 0) && this.indexIsValid(index)) {
			int p = this.parentIndex(index);
			if (this.indexIsValid(p) && (this.heap.get(p).value.compareTo(this.heap.get(index).value) < 0)) {
				Node<V, D> temp = this.heap.get(index);
				this.heap.set(index, this.heap.get(p));
				this.heap.set(p, temp);
				index = p;
			} else break;
		}
	}

	private void heapifyDown(int index) {
		while (index >= 0) {
			int swapIndex = index, l = this.leftIndex(index), r = this.rightIndex(index);
			if (this.indexIsValid(l) && (this.heap.get(l).value.compareTo(this.heap.get(index).value) > 0)) swapIndex = l;
			if (this.indexIsValid(r) && (this.heap.get(r).value.compareTo(this.heap.get(swapIndex).value) > 0)) swapIndex = r;

			if (swapIndex != index) {
				Node<V, D> temp = this.heap.get(index);
				this.heap.set(index, this.heap.get(swapIndex));
				this.heap.set(swapIndex, temp);
				index = swapIndex;
			} else break;
		}
	}

	/*
		##########
		# Print
		##########
	*/

	/* Show Heap */

	public void showHeap() {
		if (this.isEmpty()) return;
		System.out.println("\n---------- Min Heap ----------\n");
		for (int i = 0; i < this.heap.size(); i++) {
			System.out.println("Index " + i + " | Value " + this.heap.get(i).value + " -> Data " + this.heap.get(i).data);
		}
		System.out.println("\n------------------------------\n");
	}

	/* Post Order */

	public void printPostOrder() { System.out.println(this.postOrder()); }

	private void printPostOrder(int index) { System.out.println(this.postOrder(index)); }

	public String postOrder() { return this.postOrder(0); }

	private String postOrder(int index) {
		if (this.indexNotValid(index)) return "";

		String d = null;
		try { d = this.heap.get(index).data.toString(); } catch (Exception e) {}

		int li = this.leftIndex(index), ri = this.rightIndex(index);

		String v = (d == null) ? this.heap.get(index).value.toString() : ('(' + this.heap.get(index).value.toString() + " | " + d + ')'),
					 left = this.postOrder(li),
					 right = this.postOrder(ri);

		if (!right.equals("")) v = right + " " + v;
		if (!left.equals("")) v = left + " " + v;
		return v;
	}

	/* In Order */

	public void printInOrder() { System.out.println(this.inOrder()); }

	private void printInOrder(int index) { System.out.println(this.inOrder(index)); }

	public String inOrder() { return this.inOrder(0); }

	private String inOrder(int index) {
		if (this.indexNotValid(index)) return "";

		String d = null;
		try { d = this.heap.get(index).data.toString(); } catch (Exception e) {}

		int li = this.leftIndex(index), ri = this.rightIndex(index);

		String v = (d == null) ? this.heap.get(index).value.toString() : ('(' + this.heap.get(index).value.toString() + " | " + d + ')'),
					 left = this.inOrder(li),
					 right = this.inOrder(ri);

		if (!left.equals("")) v = left + " " + v;
		if (!right.equals("")) v += " " + right;
		return v;
	}

	/* Pre Order */

	public void printPreOrder() { System.out.println(this.preOrder()); }

	private void printPreOrder(int index) { System.out.println(this.preOrder(index)); }

	public String preOrder() { return this.preOrder(0); }

	private String preOrder(int index) {
		if (this.indexNotValid(index)) return "";

		String d = null;
		try { d = this.heap.get(index).data.toString(); } catch (Exception e) {}

		int li = this.leftIndex(index), ri = this.rightIndex(index);

		String v = (d == null) ? this.heap.get(index).value.toString() : ('(' + this.heap.get(index).value.toString() + " | " + d + ')'),
					 left = this.preOrder(li),
					 right = this.preOrder(ri);

		if (!left.equals("")) v += " " + left;
		if (!right.equals("")) v += " " + right;
		return v;
	}

	/* Print Tree */

	public void print() { this.print(0); }

	public void print(int index) {
		if (this.indexNotValid(index)) return;

		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		ArrayList<Node<V, D>> nodes = new ArrayList<Node<V, D>>(),
													levels = new ArrayList<Node<V, D>>();

		nodes.add(this.heap.get(index));
		int l = 1, widest = 0;

		// Build the printable node
		while (l != 0) {
			ArrayList<String> line = new ArrayList<String>();
			l = 0;

			for (Node<V, D> n : nodes) {
				int i = this.getIndex(n);
				if (this.isNull(n) || this.indexNotValid(i)) {
					line.add(null);

					levels.add(null);
					levels.add(null);
				} else {
					String s = "│ " + n.value.toString() + " │";
					line.add(s);
					if (s.length() > widest) widest = s.length();

					int li = this.leftIndex(i), ri = this.rightIndex(i);
					boolean liv = this.indexIsValid(li), riv = this.indexIsValid(ri);

					levels.add(liv ? this.heap.get(li) : null);
					levels.add(riv ? this.heap.get(ri) : null);

					if (liv && this.notNull(this.heap.get(li))) l++;
					if (riv && this.notNull(this.heap.get(ri))) l++;
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

	public void verticalPrint() { this.verticalPrint(0, false, ""); }

	public void verticalPrint(int index, boolean isLeft, String prefix) {
		if (this.indexIsValid(index)) {
			Node<V, D> node = this.heap.get(index);
			System.out.println(prefix + (isLeft ? "|-- " : "\\-- ") + "[ " + node.value.toString() + " ]");
			this.verticalPrint(this.leftIndex(index), true, prefix + (isLeft ? "|   " : "    "));
			this.verticalPrint(this.rightIndex(index), false, prefix + (isLeft ? "|   " : "    "));
		}
	}

	/*
		##########
		# Others
		##########
	*/

	/* Is Empty */

	public boolean isEmpty() { return this.heap.size() <= 0; }

	public boolean notEmpty() { return this.heap.size() > 0; }

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

	/* Check and Get Index */

	private boolean indexIsValid(int i) { return ((i >= 0) && (i <= this.lastIndex())); }

	private boolean indexNotValid(int i) { return ((i < 0) || (i > this.lastIndex())); }

	private int getIndex(Node<V, D> node) {
		if (this.isNull(node)) return -1;
		for (int i = 0; i < this.heap.size(); i++)
			if (node.value == this.heap.get(i).value) return i;
		return -1;
	}

	private int lastIndex() { return this.heap.size() - 1; }

	private int parentIndex(int child) { return (child == 0) ? -1 : ((child - 1) / 2); }
    
	private int leftIndex(int parentIndex) {
		int l = (parentIndex * 2) + 1;
		return (l < this.heap.size()) ? l : -1;
	}

	private int rightIndex(int parentIndex) {
		int r = (parentIndex * 2) + 2;
		return (r < this.heap.size()) ? r : -1;
	}

	/* Size */

	public int size() { return this.heap.size(); }
}
