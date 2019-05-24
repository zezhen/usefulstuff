# -*- coding: utf-8 -*-

from collections import namedtuple
import re, string

DBPATH = r'db/'

Movie = namedtuple('Movie', 'id,name,cast,director,url,star')
Cast = namedtuple('Cast', 'id,name,movies,url,star')
Director = namedtuple('Director', 'id,name,movies,url,star')

def load_movies(movie_db_path):
	all_movies = []
	for item in open(movie_db_path):
		if item.startswith('#'): continue
		id, name, cast, directors, url, star = re.split(', ', item)
		clst = re.split('\|', cast)
		dlst = re.split('\|', directors)
		movie = Movie(id=id, name=name, cast=clst, director=dlst, url=url, star=star)
		all_movies.append(movie)
	return all_movies

def load_cast(cast_db_path):
	all_cast = []
	for item in open(cast_db_path):
		if item.startswith('#'): continue
		id, name, movies, url,star = re.split(', ', item)
		mlst = re.split('\|', movies)
		cast = Cast(id=id, name=name, movies=mlst, url=url, star=star)
		all_cast.append(cast)
	return all_cast

def load_directors(director_db_path):
	all_directors = []
	for item in open(director_db_path):
		if item.startswith('#'): continue
		id, name, movies, url, star = re.split(', ', item)
		mlst = re.split('\|', movies)
		director = Director(id=id, name=name, movies=mlst, url=url, star=star)
		all_directors.append(director)
	return all_directors

def load_db(db_path):
	directors = load_directors(db_path + 'directors')
	cast = load_cast(db_path + 'cast')
	movies = load_movies(db_path + 'movies')
	return movies, directors, cast

def store(html, path):
	f=open(path, 'w')
	f.write(html)
	f.close()
	# print html

def get_cast_names(cast_ids, all_cast):
	cast_names=[]
	for cid in cast_ids:
		cast_names.append(next(cast.name for cast in all_cast if cast.id == cid))
	return cast_names

def get_movie_names(movie_ids, all_movies):
	movie_names=[]
	for mid in movie_ids:
		movie_names.append(next(mv.name for mv in all_movies if mv.id == mid))
	return movie_names

def get_director_names(director_ids, all_directors):
	drc_names=[]
	for did in director_ids:
		drc_names.append(next(dr.name for dr in all_directors if dr.id == did))
	return drc_names

def make_url(url, value):
	return '<a href="' + url + '">' + value +'</a>'

def make_font(value, star, url):
	return '<font color=red' + (' size=5' if star == '1' else '') + '>' + make_url(url, value) + '</font>'



def to_movie_html(movies, directors, cast, path):
	html = ['<html><ol>']
	for movie in movies:
		item = []
		cast_names = get_cast_names(movie.cast, cast)
		director_names = get_director_names(movie.director, directors)
		item.append(make_font(movie.name, movie.star, movie.url))
		item.append(','.join(cast_names))
		item.append(','.join(director_names))
		html.append('<li>' + string.join(item, '<br />') + '</li>')
	html.append('</ol></html>')
	store(string.join(html, ''), path)

def to_cast_html(casts, movies, path):
	html = ['<html><ol>']
	for cast in casts:
		item = []
		movie_names = get_movie_names(cast.movies, movies)
		item.append(make_font(cast.name, cast.star, cast.url))
		item.append(','.join(movie_names))
		html.append('<li>' + string.join(item, '<br />') + '</li>')
	html.append('</ol></html>')
	store(string.join(html, ''), path)

def to_director_html(directors, movies, path):
	html = ['<html><ol>']
	for director in directors:
		item = []
		movie_names = get_movie_names(director.movies, movies)
		item.append(make_font(director.name, director.star, director.url))
		item.append(','.join(movie_names))
		html.append('<li>' + string.join(item, '<br />') + '</li>')
	html.append('</ol></html>')
	store(str.join('', html), path)


def to_html(movies, directors, cast, path):
	to_movie_html(movies, directors, cast, path+'movie.html')
	to_director_html(directors, movies, path+'director.html')
	to_cast_html(cast, movies, path+'cast.html');

def main():
	movies, directors, cast = load_db(r'db/')
	to_html(movies, directors, cast, '')


if __name__ == '__main__':
	main()
