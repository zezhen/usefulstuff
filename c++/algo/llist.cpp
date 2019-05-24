#include "llist.h"
#include <stdio.h>

void llist::print(LNode *head) {
    LNode *t = head;
    while(t != NULL) {
        printf("%d->", t->value);
        t = t->next;
    }
    printf("\n");
}

llist::LNode* llist::createList(int *value, int n) {
    LNode *node, *head;
    head = new LNode();
    head->value = value[n-1];
    head->next=NULL;
    for(int i=n-2; i >= 0; i--) {
        node = new LNode();
        node->value = value[i];
        node->next=head;
        head=node;
    }
    return head;
}

void llist::reverse(LNode** head) {
    if(*head == NULL) return;
    LNode *p = *head, *q=p->next, *r;
    p->next=NULL;
    while(q != NULL) {
        r = q->next;
        q->next = p;
        p=q;
        q=r;
    }
    *head = p;
}


void llist::test() {

}
