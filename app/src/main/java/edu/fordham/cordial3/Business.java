package edu.fordham.cordial3;

import android.os.Parcel;
import android.os.Parcelable;

public class Business implements Parcelable {
    public static final Creator<Business> CREATOR = new Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

    // TODO: add more stuff, whatever info you want to store in Firebase Database; it has
    //  to match with the scheme of the database; below are some examples
    private String uName;
    private String uWebsite;

    public Business() {
    }

    public Business(String name, String website) {
        uName = name;
        uWebsite = website;
    }

    protected Business(Parcel in) {
        uName = in.readString();
        uWebsite = in.readString();
    }

    public String getName() {
        return uName;
    }

    public void setName(String uName) {
        this.uName = uName;
    }

    public String getWebsite() {
        return uWebsite;
    }

    public void setWebsite(String uWebsite) {
        this.uWebsite = uWebsite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uName);
        dest.writeString(uWebsite);
    }
}
