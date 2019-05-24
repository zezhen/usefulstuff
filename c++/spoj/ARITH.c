// 6. Simple Arithmetics

#include <stdio.h>

void add(char *left, char *right) {
    printf("%d, %d\n", strlen(left), strlen(right));

}

int _main() {
    // 乘法也是由加法组成, 需要保存临时结果, 在确定总共位数之后在格式化输出
    // 或者由最高位确定位数, 这样在计算过程中便可输出
    // 减法和加法是一致的
    char left[50];
    char right[50];

    add(left, right);
    return 0;
}
