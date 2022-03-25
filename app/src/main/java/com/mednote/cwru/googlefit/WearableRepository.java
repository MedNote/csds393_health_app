package com.mednote.cwru.googlefit;

import com.google.android.gms.fitness.data.DataType;
import com.mednote.cwru.base.AbstractRepository;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WearableRepository extends AbstractRepository<GoogleFitDataContainer> {
    private static volatile WearableRepository instance;

    public static WearableRepository getInstance() {

        if (instance == null) {
            instance = new WearableRepository();
        }
        return instance;
    }

    public long getStartTime(DataType dataType) {
        // TODO: should this be dynamic?
        Calendar startTime = new GregorianCalendar(2022, Calendar.MARCH, 1);
        return startTime.getTimeInMillis();
    }

    public long getEndTime() {
        Calendar endTime = Calendar.getInstance();
        return endTime.getTimeInMillis();
    }

}
