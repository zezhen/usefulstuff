from bs4 import BeautifulSoup
from collections import namedtuple
import sys, re, urllib2

def fetch(url, local):
	proxy = urllib2.ProxyHandler({'http': 'http://10210240028:p@Ssw0rd@proxy.fudan.edu.cn:8080'})
	auth = urllib2.HTTPBasicAuthHandler()
	opener = urllib2.build_opener(proxy, auth, urllib2.HTTPHandler)
	urllib2.install_opener(opener)
	content = urllib2.urlopen(url).read()
	try:
		content=content.decode('gbk').encode('utf8')
	except Exception, e:
		pass

	# orig = sys.stdout
	# sys.stdout = open(local + '.html', 'w+')
	# print content
	# sys.stdout=orig

	return content

def extract(content, key, value):
	soup = BeautifulSoup(content)
	rst = soup.find(attrs={key:value})
	return rst

# dynamic: 360buy, china-pub, amazon
Site = namedtuple('Site', 'name,url,attr,value')
sites = [
	Site('tmall', 'http://book.tmall.com/', 'class', 'slidesContent'),
	Site('qq', 'http://buy.qq.com/book/', 'class', 'sliding_img'),
	Site('china-pub', 'http://www.china-pub.com/', 'class', 'num'),
	Site('suning', 'http://www.suning.com/emall/tcd_10052_22001_.html', 'id', 'flash_pic'),
	Site('amazon', 'http://www.amazon.cn/%E5%9B%BE%E4%B9%A6/b/ref=sa_menu_bo?ie=UTF8&node=658390051', 'id', 'csPanels'),
	Site('360buy', 'http://book.360buy.com/', 'class', 'slide-items')
]


def replace(matchObj):
	match = matchObj.group(0)
	return ' src= '

def main():
	html=[]

	for name,url,attr,value in sites:
		content = fetch(url, name)
		# content = open(name+'.html').read()
		html.append('<font size="10">' + url + '</font>')
		sales = str(extract(content, attr, value))
		html.append(re.sub('hidden|_*src\w*?=', replace, sales))
		html.append('<hr>')

	sys.stdout = open('sales.html', 'w+')
	print '<meta http-equiv="content-type" content="text/html; charset=UTF-8" />'
	print ''.join(html)

def test():
	name,url,attr,value = sites[0]
	#content = fetch(url, name)
	content = open(name+'.html').read()
	print extract(content, attr, value)


if __name__ == '__main__':
	main()
	# test()
