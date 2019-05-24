def fibonacci ():
    a,b = 0,1
    yield a
    yield b
    while True:
        a, b = b, a + b
        yield b

base=10
count=0
for i in fibonacci():
	if i > base:
		print count
		base = base * 10
		if base > pow(2,64): break
		# count = 0
	count = count + 1


