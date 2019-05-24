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

// 回溯法遍历树+剪枝
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

// 先序(根)非递归遍历
void ltree::preOrderNoRec(BTNode* root) {
    std::stack<BTNode *> s;

    while(root!=NULL || !s.empty()) {
        while(root != NULL) {
            visit(root);		// 先序在结点第一次接触便可访问
            s.push(root);
            root=root->left;
        }

        if(!s.empty()) {
            root=s.top();
            s.pop();
            root=root->right;	// 接着处理右子树,将root指向右子树的根即可
        }
    }

}

// 中序(根)非递归遍历
void ltree::inOrderNoRec(BTNode* root) {
    std::stack<BTNode *> s;

    while(root!=NULL || !s.empty()) {
        while(root!=NULL) {
            s.push(root);
            root=root->left;
        }

        if(!s.empty()) {
            root=s.top();
            visit(root);	// 此时root的左子树为NULL或已访问过
            s.pop();
            root=root->right;
        }
    }
}

// 后序(根)非递归遍历
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
            if(root->right == NULL || root->right == prev) {	// 必须保证root的右子树为空或已访问过
                visit(root);
                prev=root;	// prev指向上一个访问的结点
                s.pop();
                root=NULL;
            } else
                root=root->right;
        }

    }
}

// 先序+中序->后序, 由先序第一个元素为根结点, 对应中序根结点在第k个位置, 则0-k-1为左子树, k+1-n-1为右子树
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

// 后序+中序->先序, 由后序最后一个元素为根结点, 对应中序根结点在第k个位置, 则0-k-1为左子树, k+1-n-1为右子树
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

// 寻找结点n1和n2的最低公共祖先, 假定两节点一定存在于tree中
// TODO 如果两节点不一定存在于树中,则需要遍历整棵树, 同时还需要增加变量标识找到结点个数
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

