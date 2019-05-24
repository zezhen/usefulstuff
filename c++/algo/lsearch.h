#ifndef LSEARCH_H_INCLUDED
#define LSEARCH_H_INCLUDED

#include <vector>

namespace lsearch {

	// ���������������, ��λ���ٴβ�ȷ��, ��1234567, ��λ��5671234
	// ��Ȼ���ö���, ����Ҫ�Ƚϱ߽�, ���ж�Ҫ����ֵ���Ĳ�
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
	// ����A* �㷨Ѱ�Ҿ����еĴ����Ͻǵ����½���̵�һ��·��, 0��ʾ��ͨ, 1��ʾ����ͨ
	void findpath(int** matrix, int n);

	// �ݹ����(x,y)��Χ�������, ��direction��ʾ�Ƿ�����·����
	// �����ҪѰ��topk��·��, ����Ҫһ�����Ե�����, ��Ҫ�ö�
	void recurse(int x, int y, int** matrix, int** direction, int n);

	// �����·������, ��Ҫ�������п���, �ʶ�������ν����
	void findpath_recurse(int** matrix, int n);

	bool verifyHeap(std::vector<Pos*> heap, int n);

	void printHeap(std::vector<Pos *> heap);

	void test();

}

#endif // LSEARCH_H_INCLUDED
