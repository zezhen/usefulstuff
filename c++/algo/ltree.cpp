#include "ltree.h"
#include <stack>
#include <queue>
#include <string.h>
#include <iostream>

void ltree::visit(BTNode* node) {
    if(node != NULL) {
        printf("%d\n", node->value);
    }
}

// ���ݷ�������+��֦
void ltree::backtracking(BTNode* root, int level) {

    if(root->left == NULL && root->right == NULL) {	// leaf
        // do something
        return;
    }

    if(root->left != NULL) {
        // do pruning
        backtracking(root->left, level+1);
    }

    if(root->right != NULL) {
        // do pruning
        backtracking(root->right, level+1);
    }

}

// ����(��)�ǵݹ����
void ltree::preOrderNoRec(BTNode* root) {
    std::stack<BTNode *> s;

    while(root!=NULL || !s.empty()) {
        while(root != NULL) {
            visit(root);		// �����ڽ���һ�νӴ���ɷ���
            s.push(root);
            root=root->left;
        }

        if(!s.empty()) {
            root=s.top();
            s.pop();
            root=root->right;	// ���Ŵ���������,��rootָ���������ĸ�����
        }
    }

}

// ����(��)�ǵݹ����
void ltree::inOrderNoRec(BTNode* root) {
    std::stack<BTNode *> s;

    while(root!=NULL || !s.empty()) {
        while(root!=NULL) {
            s.push(root);
            root=root->left;
        }

        if(!s.empty()) {
            root=s.top();
            visit(root);	// ��ʱroot��������ΪNULL���ѷ��ʹ�
            s.pop();
            root=root->right;
        }
    }
}

// ����(��)�ǵݹ����
void ltree::postOrderNoRec(BTNode* root) {
    std::stack<BTNode *> s;

    BTNode *prev;
    while(root != NULL || ! s.empty()) {
        while(root!=NULL) {
            s.push(root);
            root=root->left;
        }

        if(!s.empty()) {
            root=s.top();
            if(root->right == NULL || root->right == prev) {	// ���뱣֤root��������Ϊ�ջ��ѷ��ʹ�
                visit(root);
                prev=root;	// prevָ����һ�����ʵĽ��
                s.pop();
                root=NULL;
            } else
                root=root->right;
        }

    }
}

// ����+����->����, �������һ��Ԫ��Ϊ�����, ��Ӧ���������ڵ�k��λ��, ��0-k-1Ϊ������, k+1-n-1Ϊ������
void ltree::preIn2post_Order(char* pstr, char* istr, int n) {
    if(pstr == NULL || istr == NULL || n == 0) return;

    char root = pstr[0];
    int k = 0;
    while(k < n && istr[k] != root) k++;

    if(k > 1) {
        char *ileft = new char[k], *pleft = new char[k];
        strncpy(ileft, istr, k);
        strncpy(pleft, pstr, k);
        preIn2post_Order(pleft, ileft, k);

    }

    if(k < n-1) {
        char *iright=new char[n-k-1], *pright=new char[n-k-1];
        strncpy(iright, istr+k+1, n-k-1);
        strncpy(pright, pstr+k, n-k-1);
        preIn2post_Order(pright, iright, n-k-1);
    }

}

// ����+����->����, �ɺ������һ��Ԫ��Ϊ�����, ��Ӧ���������ڵ�k��λ��, ��0-k-1Ϊ������, k+1-n-1Ϊ������
void ltree::postIn2pre_Order(char* pstr, char* istr, int n) {
    if(pstr == NULL || istr == NULL || n == 0) return;

    char root = pstr[n-1];
    int k = 0;
    while(k < n && istr[k] != root) k++;

    if(k > 0) {
        char *ileft = new char[k], *pleft = new char[k];
        strncpy(ileft, istr, k);
        strncpy(pleft, pstr+1, k);
        preIn2post_Order(pleft, ileft, k);

    }

    if(k < n-1) {
        char *iright=new char[n-k-1], *pright=new char[n-k-1];
        strncpy(iright, istr+k+1, n-k-1);
        strncpy(pright, pstr+k+1, n-k-1);
        preIn2post_Order(pright, iright, n-k-1);
    }
}

int ltree::treeDeep(BTNode *head) {
    if(head == NULL) return NULL;
    std::queue<BTNode *> q;
    int deep = 0;
    BTNode *res = NULL;

    q.push(head);
    q.push(NULL);
    while(!q.empty()) {
        res = q.front();
        q.pop();
        if(res == NULL) {
            deep ++;
            q.push(NULL);
            continue;
        }

        if(res->left != NULL) {
            q.push(res->left);
        }
        if(res->right != NULL) {
            q.push(res->right);
        }

    }
    return deep;
}

// Ѱ�ҽ��n1��n2����͹�������, �ٶ����ڵ�һ��������tree��
// TODO ������ڵ㲻һ������������,����Ҫ����������, ͬʱ����Ҫ���ӱ�����ʶ�ҵ�������
ltree::BTNode* ltree::sameAncestor(BTNode *tree, BTNode *n1, BTNode *n2) {
    if(tree == NULL || n1 == NULL || n2 == NULL) return NULL;
    if(n1 == tree || n2 == tree) return tree;

    BTNode *la = sameAncestor(tree->left, n1, n2);
    BTNode *ra = sameAncestor(tree->right, n1, n2);
    if(la != NULL && ra != NULL)
        return tree;
    else if(la != NULL) return la;
    else if(ra != NULL) return ra;
    else return NULL;
}

void ltree::test() {

}

