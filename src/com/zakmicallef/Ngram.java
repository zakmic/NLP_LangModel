package com.zakmicallef;

public class Ngram {
    String[] n_gram;
    int count;
    double probability;

    public Ngram(final int words) {
        this.count = 1;
        n_gram = new String[words];
        probability = 0;
    }

    public Ngram() {
    }

    public String[] getN_gram() {
        return n_gram;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public Ngram clone() {
        Ngram n = new Ngram();
        n.n_gram = this.n_gram.clone();
        n.count = this.count;
        n.probability = this.probability;
        return n;
    }
}
