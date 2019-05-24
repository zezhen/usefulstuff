#ifndef LSEARCH_CPP_INCLUDED
#define LSEARCH_CPP_INCLUDED


#include "lsearch.h"
#include <vector>
#include <string.h>
#include <iostream>

int lsearch::bsearch_vary(int *arr, int n, int x) {
    int l = 0, r=n-1, m;

    while(l<=r) {
        m=l+((r-l)>>1);
        if(arr[m]==x) return m;
        if(arr[m] < arr[r]) {	// ��Сֵ��m���

            if(x > arr[m]) {
                if(x > arr[r]) r = m-1;
                else l = m+1;
            } else
                r = m-1;
        } else {	// ��Сֵ��m�Ҳ�
            if(x < arr[m]) {
                if(x > arr[r]) r=m-1;
                else l=m+1;
            } else
                l=m+1;
        }
    }
    return -1;
}

void lsearch::add(std::vector<Pos*> &heap, Pos *p) {
    heap.push_back(p);
    int n = heap.size();
    while( n/2 > 0) {
        if(heap[n-1]->cost < heap[n/2-1]->cost) {
            Pos *t = heap[n-1];
            heap[n-1] = heap[n/2-1];
            heap[n/2-1] = t;
            n /= 2;
        } else
            break;
    }
}

lsearch::Pos* lsearch::min(std::vector<Pos*> &heap) {
    int n=1, size=heap.size();
    if(size == 0) return NULL;
    Pos* min = heap[n-1];
    Pos* t = heap[size-1];
    heap.pop_back();
    size=heap.size();
    if(size == 0) return min;

    heap[0]=t;
    while(2*n <= size) {
        int index=n;
        if(heap[2*n-1]->cost < heap[index-1]->cost)
            index = 2*n;
        if(2*n +1 <= size && heap[2*n]->cost < heap[index-1]->cost)
            index = 2*n+1;
        if(index != n) {
            t = heap[n-1];
            heap[n-1]=heap[index-1];
            heap[index-1]=t;
            n = index;
        } else
            break;
    }
    return min;
}

void lsearch::printPath(int **direction, int n) {
    int x=n-1, y=n-1;
    do {
        //printf("%d, %d\n", x, y);
        switch(direction[x][y]) {
        case 0:
            x=x-1;break;
        case 1:
            y=y-1;break;
        case 2:
            x=x+1;break;
        case 3:
            y=y+1;break;
        default:
            std::cout<<"error!\n";
        }
    } while(x != 0 || y !=  0);
    //printf("0, 0\n\n");
}

// ����A* �㷨Ѱ�Ҿ����еĴ����Ͻǵ����½���̵�һ��·��, 0��ʾ��ͨ, 1��ʾ����ͨ
void lsearch::findpath(int** matrix, int n) {
    std::vector<Pos*> heap;
    // ������priority_queue<Pos*, vector<Pos*>, pos_compare>����
    int **direction = new int*[n];
    for(int i=0; i < n; i++) {
        direction[i]=new int[n];
        memset(direction[i], -1, n*sizeof(int*));
    }
    add(heap, new Pos(0,0,1,2*(n-1)));
    while(!heap.empty()) {
        Pos *cur = min(heap);
        int x = cur->x;
        int y = cur->y;
        //printf("%d, %d, %d\n", x, y, cur->cost);
        if(x+1 < n && matrix[x+1][y] == 0 && direction[x+1][y] == -1) {
            direction[x+1][y]=0;
            if(x+1 == n-1 && y == n-1)
                printPath(direction, n);
            else
                add(heap, new Pos(x+1, y, cur->g+1, 2*(n-1)-(x+1)-y));
        }
        if(x-1 >= 0 && matrix[x-1][y] == 0 && direction[x-1][y] == -1) {
            direction[x-1][y]=2;
            if(x-1 == n-1 && y == n-1)
                printPath(direction, n);
            else
                add(heap, new Pos(x-1, y, cur->g+1, 2*(n-1)-(x-1)-y));
        }
        if(y+1 < n && matrix[x][y+1] == 0 && direction[x][y+1] == -1) {
            direction[x][y+1]=1;
            if(x == n-1 && y+1 == n-1)
                printPath(direction, n);
            else
                add(heap, new Pos(x, y+1, cur->g+1, 2*(n-1)-(y+1)-x));
        }
        if(y-1 >= 0 && matrix[x][y-1] == 0 && direction[x][y-1] == -1) {
            direction[x][y-1]=3;
            if(x == n-1 && y-1 == n-1)
                printPath(direction, n);
            else
                add(heap, new Pos(x, y-1, cur->g+1, 2*(n-1)-(y-1)-x));
        }
    }

}

// �ݹ����(x,y)��Χ�������, ��direction��ʾ�Ƿ�����·����
// �����ҪѰ��topk��·��, ����Ҫһ�����Ե�����, ��Ҫ�ö�
void lsearch::recurse(int x, int y, int** matrix, int** direction, int n) {
    std::vector<Pos*> heap;
    if(x+1 < n && matrix[x+1][y] == 0 && direction[x+1][y] == -1) {
        direction[x+1][y]=0;
        if(x+1 == n-1 && y == n-1)
            printPath(direction, n);
        else
            recurse(x+1, y, matrix, direction, n);
        direction[x+1][y]=-1;
    }
    if(x-1 >= 0 && matrix[x-1][y] == 0 && direction[x-1][y] == -1) {
        direction[x-1][y]=2;
        if(x-1 == n-1 && y == n-1)
            printPath(direction, n);
        else
            recurse(x-1, y, matrix, direction, n);
        direction[x-1][y]=-1;
    }
    if(y+1 < n && matrix[x][y+1] == 0 && direction[x][y+1] == -1) {
        direction[x][y+1]=1;
        if(x == n-1 && y+1 == n-1)
            printPath(direction, n);
        else
            recurse(x, y+1, matrix, direction, n);
        direction[x][y+1]=-1;
    }
    if(y-1 >= 0 && matrix[x][y-1] == 0 && direction[x][y-1] == -1) {
        direction[x][y-1]=3;
        if(x == n-1 && y-1 == n-1)
            printPath(direction, n);
        else
            recurse(x, y-1, matrix, direction, n);
        direction[x][y-1]=-1;
    }
}

// �����·������, ��Ҫ�������п���, �ʶ�������ν����
void lsearch::findpath_recurse(int** matrix, int n) {
    std::vector<Pos*> heap;

    int **direction = new int*[n];
    for(int i=0; i < n; i++) {
        direction[i]=new int[n];
        memset(direction[i], -1, n*sizeof(int*));
    }

    recurse(0, 0, matrix, direction, n);
}

bool lsearch::verifyHeap(std::vector<Pos*> heap, int n) {
    int size = heap.size();
    bool rst = true;
    if(n < size) {
        if(2*n < size) {
            if(heap[n-1]->cost <= heap[2*n-1]->cost)
                rst = verifyHeap(heap, 2*n);
            else
                rst = false;
        }
        if(2*n+1 < size) {
            if(heap[n-1]->cost <= heap[2*n]->cost)
                rst = verifyHeap(heap, 2*n+1);
            else
                rst = false;
        }
    }
    return rst;
}

void lsearch::printHeap(std::vector<Pos *> heap) {
    for(std::vector<Pos*>::iterator iter = heap.begin(); iter != heap.end(); iter++)
        std::cout<<(*iter)->cost<<",";
    std::cout<<std::endl;
}

void lsearch::test(){
}

#endif // LSEARCH_CPP_INCLUDED
