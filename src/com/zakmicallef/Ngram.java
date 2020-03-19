package com.zakmicallef;

public class Ngram {
    String[] n_gram;
    int count;
    double smoothProbability;

    public Ngram(final int words) {
        this.count = 1;
        n_gram = new String[words];
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

    public double getSmoothProbability() {
        return smoothProbability;
    }

    public void setSmoothProbability(double smoothProbability) {
        this.smoothProbability = smoothProbability;
    }

    @Override
    public Ngram clone() {
        Ngram n = new Ngram();
        n.n_gram = this.n_gram.clone();
        n.count = this.count;
        n.smoothProbability = this.smoothProbability;
        return n;
    }
}
