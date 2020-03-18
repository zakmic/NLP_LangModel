package com.zakmicallef;

public class Ngram {
    final String[] n_gram;
    int count;
    double smoothProbability;

    public Ngram(int count) {
        this.count = count;
        n_gram = new String[count];
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
}
