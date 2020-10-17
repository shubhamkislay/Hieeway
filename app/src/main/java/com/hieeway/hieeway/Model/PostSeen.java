package com.hieeway.hieeway.Model;


import androidx.annotation.Nullable;

public class PostSeen {

    private String seen;
    private int number;

    public PostSeen() {
    }

    public PostSeen(String seen, int number) {
        this.seen = seen;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        PostSeen o = (PostSeen) obj;
        return this.seen.equals(o.seen);
    }
}
