__author__ = 'dowling'
from gensim.corpora.dictionary import Dictionary
from gensim.utils import tokenize
from gensim.models.word2vec import Word2Vec
from nltk.corpus import brown
import numpy as np



class BrownCorpus(object):
    def __iter__(self):
        sents = brown.sents()
        for sent in sents:
            yield map(self.normalize,sent)

    def normalize(self, word):
        word = word.lower()
        word = ("<NUMBER>" if word.isdigit() else word)
        return word

def build_dict(corpus):
    return Dictionary(corpus)

def train_w2v(c):
    w2v = Word2Vec(c, size=100)
    return w2v

def save_w2v(w2v):
    np.savetxt("../data/output/word2vec_test.csv", w2v.syn0, delimiter=",")
    with open("../data/output/word2vec_test_wordids.txt", "w") as f:
        for word, obj in sorted(w2v.vocab.items(), key=lambda (w, o): w2v.vocab[w].index):
            idx = w2v.vocab[word].index
            f.write("%s\t%s\n" % (word, idx))

if __name__ == "__main__":
    c = BrownCorpus()
    # d = build_dict(c)
    w2v = train_w2v(c)
    save_w2v(w2v)