package rrt;

import java.util.LinkedList;

public class Tree {
	public LinkedList<TreeNode> nodes;
	TreeNode root;

	public Tree(TreeNode root) {
		nodes = new LinkedList<TreeNode>();
		this.root = root;
		nodes.add(root);
		root.tree = this;
	}

	public void addNode(TreeNode node) {
		nodes.add(node);
	}

}
