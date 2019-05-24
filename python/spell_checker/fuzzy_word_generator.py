#! /usr/bin/python

import sys, random, re

class FuzzyWord:
    VOWELS = 'aeiou'

    def generate(self, word):
        fuzz = []
        duplicated_word = ''.join(map(self.__fuzz_duplicate, word))
        voweled_word = map(self.__fuzz_vowels, duplicated_word)
        cased_word = map(self.__fuzz_case, voweled_word)
        return ''.join(cased_word)

    def __fuzz_vowels(self, letter):
        return random.choice(self.VOWELS) if letter in self.VOWELS and self.__dice() else letter

    def __fuzz_case(self, letter):
        return letter.lower() if self.__dice() else letter.upper()

    def __fuzz_duplicate(self, letter):
        return letter * random.randint(1, 5)

    def __dice(self):
        return random.random() > 0.5

def main(dictionary, intractive):
    fuzzy = FuzzyWord()
    if not intractive:
        with open(dictionary) as f:
            for line in f:
                print fuzzy.generate(line[:-1])
    else:
        from spell_checker import SpellChecker
        spchecker = SpellChecker()
        spchecker.load(dictionary)
        while True:
            print '> ',
            fuzzy_word = fuzzy.generate(sys.stdin.readline()[:-1])
            print fuzzy_word,
            word = spchecker.check(fuzzy_word)
            print word if word else 'NO SUGGESTION'

if __name__ == '__main__':
    dictionary='/usr/share/dict/words'
    intractive = False
    if len(sys.argv) > 1:
        dictionary = sys.argv[1]
    if len(sys.argv) > 2:
        intractive = sys.argv[2] == 'true'
    main(dictionary, intractive)