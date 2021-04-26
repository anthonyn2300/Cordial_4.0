package edu.fordham.cordial3;

import java.util.Date;

public class Message {
    private String mtext;
    private String mUid;
    private long mTimestamp;

    public Message()
    {

    }

    public Message(String message, String uid)
    {
        mtext = message;
        mUid = uid;
        mTimestamp = new Date().getTime();
    }

    public String getText()
    {
        return mtext;
    }

    public void setText(String m)
    {
        mtext = m;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String m) {
        mUid = m;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long m) {
        mTimestamp = m;
    }
}
