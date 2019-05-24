#include <stdio.h>
#include <string.h>

void reverse_number(char *number, int n) {
    int l = 0, r = n - 1;
    char t;
    while(l < r) {
        t = number[r];
        number[r] = number[l];
        number[l] = t;
        l ++;
        r --;
    }
}

void add_or_minus(char *left, int n, char *right, int m, bool add) {


    int maxlen = (n > m ? n : m) + 1;
    char rst[maxlen+1];
    rst[maxlen] = 0;

    int lt = n - 1, rt = m - 1, index = maxlen;

    int carry = 0, lvalue;
    while(lt >= 0) {
        lvalue = (left[lt] - '0') + carry;

        char rvalue = rt >= 0 ? (right[rt--] - '0') : 0;

        if(! add) {
            if(lvalue < rvalue) {
                lvalue += 10;
                carry = -1;
            } else {
                carry = 0;
            }
            rst[--index] = lvalue - rvalue + '0';
        } else {
            lvalue += rvalue;
            rst[--index] = lvalue % 10 + '0';
            carry = lvalue / 10;
        }
        lt --;
    }

    if(carry > 0) {
        rst[--index] = carry + '0';
    }
    while(rst[index] == '0')
        index ++;

    int width = n > m + 1 ? n : m + 1;

    char line[width+1];
    memset(line, '-', sizeof(line));
    line[width]=0;

    if(index == 0) width = maxlen;

    printf("%*s\n", width, left);
    printf("%*c", width - m, add ? '+' : '-');
    printf("%s\n", right);
    printf("%*s\n", width, line);
    printf("%*s\n", width, rst+index);
}

void multiple(char *left, int n, char *right, int m) {

    char interm[m][n+2];

    int rt = m - 1, tmp;

    while(rt >= 0) {

        if(right[rt] == '0') {
            strcpy(interm[rt], "0");
            rt --;
            continue;
        }

        int lt = n - 1, index = 0, carry = 0;
        while(lt >= 0) {
            tmp = (left[lt --] - '0') * (right[rt] - '0') + carry;
            interm[rt][index++] = tmp % 10 + '0';
            carry = tmp / 10;
        }
        if(carry > 0) {
            interm[rt][index++] = carry + '0';
        }
        interm[rt][index] = 0;
        rt --;
    }

    int width = n > m + 1 ? n : m + 1;
    char line1[width];
    memset(line1, '-', sizeof(line1));
    line1[width]=0;


    char rst[n*m + 2];
    memset(rst, '0', n*m+1);

    rt = m - 1;
    while(rt >= 0) {
        int len = strlen(interm[rt]), intt = 0, bt = m - 1 - rt;

        int carry = 0;
        while(intt < len) {
            tmp = (interm[rt][intt] - '0') + (rst[bt] - '0') + carry;
            rst[bt] = tmp % 10 + '0';
            carry = tmp / 10;
            intt ++;
            bt ++;
        }

        while(carry > 0) {
            tmp = (rst[bt] - '0') + carry;
            rst[bt ++] = tmp % 10 + '0';
            carry = tmp / 10;
        }
        if(width < bt) width = bt;
        reverse_number(interm[rt], len);

        rt --;
    }
    rst[width] = 0;

    char line2[width];
    memset(line2, '-', sizeof(line2));
    line2[width]=0;

    printf("%*s\n", width, left);
    printf("%*c", width - m, '*');
    printf("%s\n", right);

    rt = m - 1;
    if(rt > 1) {
        printf("%*s\n", width, line1);
        while(rt >= 0) {
            printf("%*s\n", width - (m-1-rt), interm[rt]);
            rt --;
        }
    }
    printf("%*s\n", width, line2);
    reverse_number(rst, width);
    printf("%*s\n", width, rst);
}

int main(){


    int n;
    scanf("%d\n", &n);

    char input[1001];
    for(int i=0; i<n; i++) {
        scanf("%s", input);
        int k = 0;
        while(input[k] >= '0' && input[k] <= '9') k++;

        char op = input[k];

        input[k] = 0;
        char *left = input, *right = input+k+1;
        int n = strlen(left), m = strlen(right);

        switch(op) {
        case '+':
            add_or_minus(left, n, right, m, true);
            break;
        case '-':
            add_or_minus(left, n, right, m, false);
            break;
        case '*':
            multiple(left, n, right, m);
        default:
            break;
        }
        printf("\n");
    }

    return 0;
}
