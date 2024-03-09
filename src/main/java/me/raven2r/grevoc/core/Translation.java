package me.raven2r.grevoc.core;

public class Translation implements Comparable<Translation>, Cloneable{
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

    public Translation setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public Translation setTarget(String target) {
        this.target = target;
        return this;
    }

    public int getCounter() {
        return counter;
    }

    public Translation setCounter(int counter) {
        if(counter < 0)
            throw new RuntimeException("Counter must be greater or equal to 0");

        this.counter = counter;
        return this;
    }

    public long getAdded() {
        return added;
    }

    public Translation setAdded(long added) {
        if(added < 0)
            throw new RuntimeException("Date must be greater or equal to 0");
        
        this.added = added;
        return this;
    }

    @Override
    public int compareTo(Translation other) {
        return this.getSource().compareTo(other.getSource());
    }

    @Override
    public Translation clone() {
        try {
            return (Translation)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            cloneNotSupportedException.printStackTrace();
        }

        return null;
    }

    public static Translation newSimple(String source, String target) {
        return new Translation(source, target, 1, System.currentTimeMillis());
    }

    public static Translation empty(String source) {
        return new Translation(source, null, 0, 0);
    }

    public static Translation withCounter(String source, String target, int counter) {
        return new Translation(source, target, counter, System.currentTimeMillis());
    }

    public static Translation withAdded(String source, String target, long added) {
        return new Translation(source, target, 1, added);
    }

    public boolean isEmpty() {
        if(null == target || 0 == counter || 0 == added)
            return true;

        return false;
    }

    public boolean isGhost() {
        if(0 == counter)
            return true;

        return false;
    }

    public Translation increaseCounter() {
        this.counter++;
        return this;
    }

    public Translation appendCounter(int counts) {
        if(counts <= 0)
            throw new IllegalArgumentException("counts is " + counts + ", must be positive");

        this.counter += counts;
        return this;
    }
}
