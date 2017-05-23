package trajon.felton.gamehousefinder;

import java.io.Serializable;

/**
 * Created by Trajon Felton on 11/5/2016.
 */

public class Store implements Serializable{
    int storeID;
    String storeName;
    String storeAddr1;
    String storeAddr2;
    double storeLon;
    double storeLat;
    double distance;

    public Store(int id,String n,String a1,String a2,double lon,double lat,double dist){
        this.storeID = id;
        this.storeName = n;
        this.storeAddr1 = a1;
        this.storeAddr2 = a2;
        this.storeLon = lon;
        this.storeLat = lat;
        this.distance = dist;
    }
}
