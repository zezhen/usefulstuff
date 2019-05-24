// 5. The Next Palindrome

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <algorithm>

void next_palin(char* num) {
	if(num == NULL) return;

	char number[10];
	strcpy(number, num);

	int n = strlen(number);

	if(n == 1) {
		if(number[0] < '9')
			printf("%c\n", number[0]+1);
		else
			printf("11\n");
		return;
	}

	int mid = -1, lmid, rmid;
	int l , r, tmp;
	if(n % 2 == 1) {
		mid = n/2;
		l = lmid = mid-1;
		r = rmid = mid+1;
	} else {
		r = rmid = n/2;
		l = lmid = r-1;
	}

	bool inc = false;

	while(l >= 0) {
		if(number[l] == number[r]) {
			l--;
			r++;
			if(l < 0) {
				if(mid > 0 && number[mid] < '9')
					number[mid] ++;
				else {
					if(mid > 0) number[mid] = '0';
					tmp = 0;

					while(lmid >= tmp && number[lmid - tmp] == '9') {
						number[lmid - tmp] = '0';
						number[rmid + tmp] = '0';
						tmp ++;
					}

					if(tmp > lmid) {
						printf("1");
						number[n-1] = '1';
						printf("%s\n",number);
						return;
					} else {
						number[lmid - tmp] ++;
						number[rmid + tmp]=number[lmid - tmp];
						l = lmid - tmp - 1;
						r = rmid + tmp + 1;
						inc = true;
						break;
					}
				}
			}
		} else if(number[l] > number[r]) {
			number[r] = number[l];
			inc = true;
			break;
		} else {
			if(mid > 0 && number[mid] < '9')
				number[mid] ++;
			else {
				if(mid > 0) number[mid] = '0';
				tmp = 0;
				while(number[lmid - tmp] == '9') {
					number[lmid - tmp] = '0';
					number[rmid + tmp] = '0';
					tmp ++;
				}
				number[lmid - tmp] ++;
				number[rmid + tmp]=number[lmid - tmp];
				l = lmid - tmp - 1;
				r = rmid + tmp + 1;
				inc = true;
			}
			break;
		}

	}

	while(l >= 0) {
		number[r] = number[l];
		r ++;
		l --;
	}

	printf("%s\n", number);

}

void check(char buf[10]) {

	int n = strlen(buf);
	int l=0, r=n-1;
	while(l <= r && buf[l] == buf[r]) {
		l++;r--;
	}
	if(l > r)
		printf("%s\n", buf);

}

int palin_main() {

	 int n;
	 scanf("%d", &n);

	 char number[1000000];
	 for(int i = 0; i<n; i++) {
	 	scanf("%s", number);
	 	next_palin(number);
	 }

	return 0;
}
