#! /usr/bin/python

import unittest
from spell_checker import SpellChecker

class TestStringMethods(unittest.TestCase):

    def setUp(self):
        self.spchecker = SpellChecker()
        self.spchecker.register('job')
        self.spchecker.register('jaob')

    def test_no_suggesstion(self):
        self.assertIsNone(self.spchecker.check(None))
        self.assertIsNone(self.spchecker.check(''))
        self.assertIsNone(self.spchecker.check('joe'))
        self.assertIsNone(self.spchecker.check('jobs'))
        self.assertIsNone(self.spchecker.check('job1'))
        self.assertIsNone(self.spchecker.check('spell_checker'))
        self.assertIsNone(self.spchecker.check('spell checker'))
        self.assertIsNone(self.spchecker.check(1))
        self.assertIsNone(self.spchecker.check(['job']))

    def test_case_error(self):
        self.assertEqual(self.spchecker.check('JOB'), 'job')
        self.assertEqual(self.spchecker.check('JoB'), 'job')
        self.assertEqual(self.spchecker.check('JaoB'), 'jaob')

    def test_vowel_error(self):
        self.assertEqual(self.spchecker.check('jab'), 'job')
        self.assertEqual(self.spchecker.check('jaAb'), 'jaob')

    def test_duplicate_error(self):
        self.assertEqual(self.spchecker.check('jooooobbbb'), 'job')
        self.assertEqual(self.spchecker.check('jjjjjaaaaaab'), 'jaob')

    def test_combination_error(self):
        self.assertEqual(self.spchecker.check('jJoOoAEIbBbB'), 'job')
        self.assertEqual(self.spchecker.check('jJJaAeIObBB'), 'jaob')

if __name__ == '__main__':
    unittest.main()