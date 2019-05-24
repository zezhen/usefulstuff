#ifndef LSTRING_H_INCLUDED
#define LSTRING_H_INCLUDED

namespace lstring {

	// 最小公约数
	int gcd(int n, int m);
	// 字符串左旋
	void left_rotate_gcd(char* str, int len, int m);
	// 字符串逆序
	void string_reverse(char* str, int len);
	// 字符串左旋, 三次反转
	void left_rotate_reverse(char* str, int len, int m);
	// 模式匹配递归实现
	bool is_pattern_match_recusive(char *str, char *pat);

	// 模式匹配非递归
	bool is_pattern_match(char *str, char *pat);

	// 字符串中查找最长回文, O(n)复杂度
	void longest_palindrome(char *str);

	void test();
}

#endif // LSTRING_H_INCLUDED
