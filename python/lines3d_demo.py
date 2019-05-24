from matplotlib import pyplot
from mpl_toolkits.mplot3d import Axes3D 

fig = pyplot.figure()
ax = fig.gca(projection='3d')

n=5

def zero(t1, t2):
	return (t1+t2) > 0 and (2*n-t1-t2) > 0

def calc(t1, t2):
	return t1 * 1.0/(t1+t2) + (n-t1) * 1.0/ (2*n-t1-t2) if zero(t1, t2) else 0.5

x=sum([[i]*n for i in range(0, n+1)], [])
y=[i for i in range(0, n+1)]*n
z=[ calc(t1, t2) for (t1, t2) in zip(x,y)]


ax.plot(x, y, z, '*', label='need a label')
ax.legend()

pyplot.show()