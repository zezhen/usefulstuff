#! /usr/bin/python

from heapq import heappush, heappop
import sys, re

class SpellChecker:
    END = '$'
    VOWELS = ['a','e','i','o','u','A','E','I','O','U']

    def __init__(self):
        self.trie = {}

    def load(self, dictionary):
        with open(dictionary) as f:
            for line in f:
                self.register(line[:-1])

    # add word to trie tree
    def register(self, word):
        t = self.trie
        for c in word:
            if c not in t: t[c] = {}
            t = t[c]
        t[self.END] = {}

    # minute 1 actually as using priority queue
    # the less of the score the higher of the priority
    def __inc_score(self, score):
        return score - 1

    def __check_iter(self, word, exact_match=True):
        if not word or len(word) <= 0:
            yield None

        queue = [(0, word, '', '', self.trie)]
        while queue:
            score, word, path, prev, trie = heappop(queue)

            if word == '':
                if self.END in trie:
                    yield path
            else:
                letter = word[0]
                letter_case_rev = letter.lower() if letter.isupper() else letter.upper()
                remaining = word[1:]

                # letter match, increase the score and process in higher priority
                if letter in trie:
                    heappush(queue, (self.__inc_score(score), remaining, path+letter, letter, trie[letter]))

                # search more probabilities
                if letter in self.VOWELS:
                    [ heappush(queue, (score, remaining, path+k, k, trie[k])) for k in self.VOWELS if k in trie and k != letter ]
                elif letter_case_rev in trie:
                        heappush(queue, (score, remaining, path+letter_case_rev, letter_case_rev, trie[letter_case_rev]))
                
                if letter == prev or letter_case_rev == prev or (letter in self.VOWELS and prev in self.VOWELS):
                    heappush(queue, (score, remaining, path, prev, trie))

    def check(self, word):
        try:
            for ans in self.__check_iter(word):
                return ans
        except Exception as e:
            print e
            return None

# def words(text): return re.findall('[a-zA-Z]+', text)[0]

def main(dictionary):
    spchecker = SpellChecker()
    spchecker.load(dictionary)

    while True:
        print '> ',
        word = spchecker.check(sys.stdin.readline()[:-1])
        print word if word else 'NO SUGGESTION'

if __name__ == '__main__':
    dictionary='/usr/share/dict/words'
    if sys.argv and len(sys.argv) > 1:
        dictionary = sys.argv[1]
    main(dictionary)