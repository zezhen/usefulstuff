#include "larray.h"
#include <stdio.h>

int larray::max_subarray_sum(int *arr, int n) {
    if(arr == NULL) return 0;

    int sum = 0, max=0;
    for(int i=0; i<n; i++) {
        sum += arr[i];
        if(sum < 0)
            sum = 0;
        else if(sum > max)
            max = sum;
    }
    return max;

}

void larray::test() {
}
