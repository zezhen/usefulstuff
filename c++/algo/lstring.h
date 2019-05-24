#ifndef LSTRING_H_INCLUDED
#define LSTRING_H_INCLUDED

namespace lstring {

	// ��С��Լ��
	int gcd(int n, int m);
	// �ַ�������
	void left_rotate_gcd(char* str, int len, int m);
	// �ַ�������
	void string_reverse(char* str, int len);
	// �ַ�������, ���η�ת
	void left_rotate_reverse(char* str, int len, int m);
	// ģʽƥ��ݹ�ʵ��
	bool is_pattern_match_recusive(char *str, char *pat);

	// ģʽƥ��ǵݹ�
	bool is_pattern_match(char *str, char *pat);

	// �ַ����в��������, O(n)���Ӷ�
	void longest_palindrome(char *str);

	void test();
}

#endif // LSTRING_H_INCLUDED
