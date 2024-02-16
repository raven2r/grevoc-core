package me.raven2r.grevoc.core;

public class Translation implements Comparable<Translation> {
    String source;
    String target;
    int counter;
    long added;

    public Translation(String source, String target, int counter, long added) {
        this.source = source;
        this.target = target;
        this.counter = counter;
        this.added = added;
    }
    
    Translation() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    @Override
    public int compareTo(Translation other) {
        return this.getSource().compareTo(other.getSource());
    }
}
