// 6. Simple Arithmetics

#include <stdio.h>

void add(char *left, char *right) {
    printf("%d, %d\n", strlen(left), strlen(right));

}

int _main() {
    // �˷�Ҳ���ɼӷ����, ��Ҫ������ʱ���, ��ȷ���ܹ�λ��֮���ڸ�ʽ�����
    // ���������λȷ��λ��, �����ڼ�������б�����
    // �����ͼӷ���һ�µ�
    char left[50];
    char right[50];

    add(left, right);
    return 0;
}
