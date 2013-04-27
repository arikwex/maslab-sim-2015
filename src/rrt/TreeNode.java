package rrt;

import java.util.LinkedList;
import map.Point;

public class TreeNode {
	public Point loc;
	Tree tree;
	public TreeNode parent;
	LinkedList<TreeNode> children;
	
	public TreeNode(Point location) {
		this.loc = location;
		children = new LinkedList<TreeNode>();
	}

	public void addChild(TreeNode child) {
		children.add(child);
		tree.addNode(child);
		child.parent = this;
		child.tree = this.tree;
	}
}
