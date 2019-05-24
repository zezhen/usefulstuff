# -*- coding: utf-8 -*-

#######
import profile
def test():
    total=1
    for i in xrange(10000):
        total += 1
    return total
profile.run('test()')

#######
from xml.sax import parse
from xml.sax.handler import ContentHandler
class XmlHandler(ContentHandler):

	def startElement(self, name, attrs):
		print name, attrs.keys()
	def endElement(self, name):
		print name
	def characters(self, string):
		print string
parse('test.xml', XmlHandler())

#######
import unittest
def square(x):
	return x*x
class MyTestCase(unittest.TestCase):
	def test(self):
		for x in range(10):
			self.failUnless(x*x == square(x), x)
unittest.main()

###### rename
num=0
for root, dirs, files in os.walk(path):
	for pname in files:	
		prefile = os.path.join(root, pname)
		dot = pname.rfind('.')
		newfile = os.path.join(root, str(names[num]) + pname[dot:])
		os.rename(prefile, newfile)
		num = num + 1

###### plot3d
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

####### plot 2d
from matplotlib import pyplot
x=range(1,10)
y=[1.0/(i+1)/(i+1) for i in x]
pyplot.plot(x,y,label='label')
pyplot.show()