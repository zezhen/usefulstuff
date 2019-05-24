#ifndef LTREE_H_INCLUDED
#define LTREE_H_INCLUDED

namespace ltree {

	struct BTNode {
		int value;
		BTNode *left;
		BTNode *right;
	};

	void visit(BTNode* node);

	// 回溯法遍历树+剪枝
	void backtracking(BTNode* root, int level);

	// 先序(根)非递归遍历
	void preOrderNoRec(BTNode* root);

	// 中序(根)非递归遍历
	void inOrderNoRec(BTNode* root);

	// 后序(根)非递归遍历
	void postOrderNoRec(BTNode* root);

	// 先序+中序->后序, 由先序第一个元素为根结点, 对应中序根结点在第k个位置, 则0-k-1为左子树, k+1-n-1为右子树
	void preIn2post_Order(char* pstr, char* istr, int n);

	// 后序+中序->先序, 由后序最后一个元素为根结点, 对应中序根结点在第k个位置, 则0-k-1为左子树, k+1-n-1为右子树
	void postIn2pre_Order(char* pstr, char* istr, int n);

	int treeDeep(BTNode *head);

	// 寻找结点n1和n2的最低公共祖先, 假定两节点一定存在于tree中
	// TODO 如果两节点不一定存在于树中,则需要遍历整棵树, 同时还需要增加变量标识找到结点个数
	BTNode* sameAncestor(BTNode *tree, BTNode *n1, BTNode *n2) ;

	void test();

}

#endif // LTREE_H_INCLUDED
