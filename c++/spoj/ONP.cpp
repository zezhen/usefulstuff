// 4. Transform the Expression

#include <stdio.h>
#include <stack>
#include <string.h>


typedef struct Node {
	char value;
	Node *left;
	Node *right;
	Node *parent;
}Node;

#define POW (6)
#define DIV (5)
#define MUL (4)
#define MIN (3)
#define ADD (2)
#define LBR (1)
#define RBR (0)
#define WRD (-1)

int getPriority(char op) {
	switch(op) {
		case '^': return POW;
		case '/': return DIV;
		case '*': return MUL;
		case '-': return MIN;
		case '+': return ADD;
		case '(': return LBR;
		case ')': return RBR;
		default:  return WRD;
	}
}

Node* toNode(char *expression, int n) {
	int i=0;
	Node *cur = NULL;
	std::stack<Node *> st;
	while(i < n) {
		char c = *(expression+i);
		Node *nn = new Node();
		nn->value = c;
		int priority = getPriority(c);
		if(priority == LBR) {
			if(cur != NULL) {
				st.push(cur);
				cur = NULL;
			}
		} else if(priority == RBR) {
			if(cur == NULL) {
				printf("error : meet right bracket while current node is NULL");
				return NULL;
			}
			Node *right = cur;
			while(right->parent)
				right = right->parent;
			cur = st.top();
			st.pop();
			if(cur->right != NULL) {
				printf("error: current node's right child is not NULL");
				return NULL;
			}
			cur->right = right;
		} else if(priority != WRD) {
			int cur_pri = getPriority(cur->value);
			if(priority > cur_pri) {
				nn->left = cur->right;
				cur->right = nn;
			} else {
				nn->left = cur;
				cur = nn;
			}
		} else {
			cur->right = nn;
		}
	}
	while(cur->parent) {
		cur = cur->parent;
	}
	return cur;
}

void visit(Node *node) {
	printf("%c", node->value);
}

void postOrderNoRec(Node* root) {
	std::stack<Node *> s;
	
	Node *prev;
	while(root != NULL || ! s.empty()) {
		while(root!=NULL) {
			s.push(root);
			root=root->left;
		}

		if(!s.empty()) {	
			root=s.top();
			if(root->right == NULL || root->right == prev) {
				visit(root);
				prev=root;
				s.pop();
				root=NULL;
			} else
				root=root->right;
		}
	}
}

int main() {

	char *expression = "a+b+c+d";
	int len = strlen(expression);
	Node *root = toNode(expression, len);
	if(root != NULL)
		postOrderNoRec(root);


	return 0;
}