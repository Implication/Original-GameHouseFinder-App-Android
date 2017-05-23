package trajon.felton.gamehousefinder;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Trajon Felton on 11/5/2016.
 */

public class Event  implements Serializable{
        int eventID;
        int storeID;
        String eventName;
        String eventDate;
        String eventDescription;

        public Event(int id, int storeID, String n, String date, String desc) {
            this.eventID = id;
            this.storeID = storeID;
            this.eventName = n;
            this.eventDate = date;
            this.eventDescription = desc;
        }
}
