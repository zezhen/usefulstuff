# -*- coding: utf-8 -*-


########### re #############
import re

re.sub('[abc]', 'o', 'caps') #oops

def search(matchObj):
    pass #print matchObj.group(0), # c a
re.sub('[abc]', search, 'caps')


########## profile ##############
import profile

def test():
    total=1
    for i in xrange(100000000l):
        total += 1
        
    return total

#profile.run('test()')


######  xml  #########
from xml.sax import parse
from xml.sax.handler import ContentHandler

class XmlHandler(ContentHandler):

	def startElement(self, name, attrs):
		print name, attrs.keys()
	def endElement(self, name):
		print name
	def characters(self, string):
		print string

#parse('test.xml', XmlHandler())


######  unittest  #########
import unittest

def square(x):
	return x*x

class MyTestCase(unittest.TestCase):
	def test(self):
		for x in range(10):
			self.failUnless(x*x == square(x), x)
# unittest.main()


#########class#########
class Clz:
	def __init__(self):
		self.pubic_var = 1
		self.__private_var = -1
		self.__class__.public_class_var = 2
		self.__class__.__private_class_var = -2
	def public_function(self):
		print 'public function'
	def __private_function(self):
		print 'private function'

c = Clz()
print c.pubic_var
try:
	print c.__private_var
except Exception, e:
	print 'can''t access private var'
print Clz.public_class_var
try:
	print Clz.__private_class_var
except Exception, e:
	print 'can''t access private class var'
c.public_function()
try:
	c.__private_function()
except Exception, e:
	print 'can\'t access private function'






if __name__ == '__main__':
	pass