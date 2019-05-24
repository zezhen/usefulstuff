#ifndef LSEARCH_H_INCLUDED
#define LSEARCH_H_INCLUDED

#include <vector>

namespace lsearch {

	// 有序递增数组左移, 移位多少次不确定, 如1234567, 移位后5671234
	// 仍然采用二分, 但需要比较边界, 以判断要查找值再哪侧
	int bsearch_vary(int *arr, int n, int x);

	struct Pos {
		int x;
		int y;
		int g;
		int cost;

		Pos(int x, int y, int g, int h) :x(x), y(y),g(g) {
			cost=g+h;
		}
	};

	void add(std::vector<Pos*> &heap, Pos *p);

	Pos* min(std::vector<Pos*> &heap);

	void printPath(int **direction, int n);
	// 采用A* 算法寻找矩阵中的从左上角到右下角最短的一条路径, 0表示可通, 1表示不可通
	void findpath(int** matrix, int n);

	// 递归遍历(x,y)周围可能情况, 用direction表示是否已在路径上
	// 如果是要寻找topk条路径, 则需要一定策略的搜索, 需要用堆
	void recurse(int x, int y, int** matrix, int** direction, int n);

	// 解决多路径问题, 需要遍历所有可能, 故而无需所谓策略
	void findpath_recurse(int** matrix, int n);

	bool verifyHeap(std::vector<Pos*> heap, int n);

	void printHeap(std::vector<Pos *> heap);

	void test();

}

#endif // LSEARCH_H_INCLUDED
