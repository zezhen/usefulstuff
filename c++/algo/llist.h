#ifndef LLIST_H_INCLUDED
#define LLIST_H_INCLUDED

namespace llist {

struct LNode
{
    int value;
    LNode *next;
};

void print(LNode *head);
void reverse(LNode **head);
LNode* createList(int *value, int n);

void test();

}

#endif // LLIST_H_INCLUDED
