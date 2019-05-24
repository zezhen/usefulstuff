#ifndef LTREE_H_INCLUDED
#define LTREE_H_INCLUDED

namespace ltree {

	struct BTNode {
		int value;
		BTNode *left;
		BTNode *right;
	};

	void visit(BTNode* node);

	// ���ݷ�������+��֦
	void backtracking(BTNode* root, int level);

	// ����(��)�ǵݹ����
	void preOrderNoRec(BTNode* root);

	// ����(��)�ǵݹ����
	void inOrderNoRec(BTNode* root);

	// ����(��)�ǵݹ����
	void postOrderNoRec(BTNode* root);

	// ����+����->����, �������һ��Ԫ��Ϊ�����, ��Ӧ���������ڵ�k��λ��, ��0-k-1Ϊ������, k+1-n-1Ϊ������
	void preIn2post_Order(char* pstr, char* istr, int n);

	// ����+����->����, �ɺ������һ��Ԫ��Ϊ�����, ��Ӧ���������ڵ�k��λ��, ��0-k-1Ϊ������, k+1-n-1Ϊ������
	void postIn2pre_Order(char* pstr, char* istr, int n);

	int treeDeep(BTNode *head);

	// Ѱ�ҽ��n1��n2����͹�������, �ٶ����ڵ�һ��������tree��
	// TODO ������ڵ㲻һ������������,����Ҫ����������, ͬʱ����Ҫ���ӱ�����ʶ�ҵ�������
	BTNode* sameAncestor(BTNode *tree, BTNode *n1, BTNode *n2) ;

	void test();

}

#endif // LTREE_H_INCLUDED
