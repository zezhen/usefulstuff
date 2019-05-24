#include "larray.h"
#include "ltree.h"
#include "lstring.h"
#include "lsearch.h"
#include "llist.h"

int main(int argc, char* argv[])
{

	llist::test();
	larray::test();
	lsearch::test();
	ltree::test();
	lstring::test();

	return 0;
}
