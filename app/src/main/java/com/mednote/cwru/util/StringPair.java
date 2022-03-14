package com.mednote.cwru.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class StringPair extends Pair<String, String> implements Parcelable {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public StringPair(String first, String second) {
        super(first, second);
    }

    public StringPair(Pair<String , String> pair) {
        super(pair.first, pair.second);
    }

    protected StringPair(Parcel in) {
        this(in.readString(), in.readString());
    }

    public static final Creator<StringPair> CREATOR = new Creator<StringPair>() {
        @Override
        public StringPair createFromParcel(Parcel in) {
            return new StringPair(in);
        }

        @Override
        public StringPair[] newArray(int size) {
            return new StringPair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(first);
        parcel.writeString(second);
    }
}
