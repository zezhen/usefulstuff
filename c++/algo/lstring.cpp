
#include "lstring.h"
#include <stack>
#include <assert.h>
#include <string.h>
#include <iostream>

// 最小公约数
int lstring::gcd(int n, int m) {
    int r = n % m;
    while(r!=0) {
        n = m;
        m = r;
        r = n % m;
    }
    return m;
}
// 字符串左旋
void lstring::left_rotate_gcd(char* str, int len, int m) {
    int k = gcd(len, m);
    int groups = len / k;

    for(int i=0; i<k; i++) {
        char temp = str[i];
        int last = i;
        for(int j = (i + m)%len; j != i; j = (j+m)%len) {
            str[last]=str[j];
            last = j;
        }
        str[last]=temp;
    }
}
// 字符串逆序
void lstring::string_reverse(char* str, int len) {
    int i =0, j = len-1;

    while(i < j) {
        char tmp = str[i];
        str[i++] = str[j];
        str[j--] = tmp;
    }
}
// 字符串左旋, 三次反转
void lstring::left_rotate_reverse(char* str, int len, int m) {
    string_reverse(str, m);
    string_reverse(str+m, len-m);
    string_reverse(str, len);
}


// 模式匹配递归实现
bool lstring::is_pattern_match_recusive(char *str, char *pat) {
    if(!*str && !*pat) return true;	// 匹配完成

    while(*str && *pat) {
        if(*pat != '*') {	// 单字符匹配
            if(*pat != '?' && *pat != *str) return false;
            pat ++;
            str ++;
        } else {	// 多字符匹配
            pat ++;	// pat指向*的下一个字符
            while(*str) {
                while(*str && *str != *pat) str++;	// 在str中寻找*的下一个字符进行匹配
                if(is_pattern_match_recusive(str, pat)) return true;	// 匹配成功则返回, 不成功则继续寻找
                str ++;
            }
        }
    }
    if(*str || *pat) return false;
    return true;
}

// 模式匹配非递归
bool lstring::is_pattern_match(char *str, char *pat) {
    if(!*str && !*pat) return true;
    std::stack<char *> strstack;
    std::stack<char *> patstack;
    bool match=true;
    while(*str && *pat) {
        if(*pat != '*') {	// 单字符匹配
            if(*pat != '?' && *pat != *str)
                match=false;
            else {
                pat ++;
                str ++;
            }
        } else {	// 多字符匹配
            pat ++;
            while(*str && *str != *pat) str++;	// 在str中寻找*的下一个字符进行匹配
            if(*str || !*pat) {
                strstack.push(str);
                patstack.push(pat-1);
                if(*str) {
                    str ++;
                    pat ++ ;
                }
            } else
                match=false;
        }
        if((! match || (*str==0)^(*pat==0)) && !strstack.empty()) {
            str = strstack.top()+1;
            strstack.pop();
            pat = patstack.top();
            patstack.pop();
            match=true;
        }
    }
    if(*str || *pat) return false;
    return true;
}

// 字符串中查找最长回文, O(n)复杂度
void lstring::longest_palindrome(char *str) {
    if(!*str) return;

    int n = strlen(str);
    int *len = new int[2*n+1];
    int p=0, i=1, lp_len=1, lp_center=1, j, q, current_start, q_start;

    while(p < 2*n+1) {
        j=p-1;
        i=p+1;
        while(j >= 0) {
            // 要么都是特殊符号, 直接相等; 要么都是字符, 必须相等
            if(i%2==0 && j%2==0 || i%2 == 1 && j%2 == 1 && str[i/2]==str[j/2]) {
                i++;
                j--;
            } else
                break;
        }
        len[p]=2*(p-j)-1;
        //len[p]=2*i - p - 1;

        if(len[p] > lp_len) {
            lp_len=len[p];
            lp_center = p;
        }

        current_start=j+1;
        q=p-1;
        p++;
        while(q >= 0 && p < 2*n+1) {
            q_start = q-len[q]/2;
            if(q_start > current_start || i >= 2*n+1) {
                len[p]=len[q];
                p++;
                q--;
            } else
                break;
        }
    }
    for(i=0;i<2*n+1;i++)
        std::cout<<len[i]/2<<" ";
    std::cout<<std::endl;

}

void lstring::test() {

    //char str[] = "abcdefghijklmn";
    //int n=15, m=6, len=14;
    //printf("the gcd of %d and %d is %d\n", n, m, gcd(n,m));
    //
    //printf("string is %s \n", str);
    //string_reverse(str, len);
    //printf("reversed : %s\n", str);
    //left_rotate_gcd(str, len, m);
    //printf("left rotate with gcd by %d : %s\n", m, str);
    //left_rotate_reverse(str, len, len-m);
    //printf("left rorate with reverse by %d : %s\n", len-m, str);

    assert(is_pattern_match("aa", "aa") == true);
    assert(is_pattern_match("aa", "?a") == true);
    assert(is_pattern_match("aa", "*") == true);
    assert(is_pattern_match("ababab", "a*b") == true);
    assert(is_pattern_match("aaa", "aa") == false);
    assert(is_pattern_match("ab", "?*") == true);
    assert(is_pattern_match("aab", "*a*b") == true);
    assert(is_pattern_match("ababdbcab", "a*b*bc*b") == true);
    assert(is_pattern_match("aab", "*a*bc") == false);

    longest_palindrome("aaaaaaa");

    printf("-- lstring green --\n");
}

