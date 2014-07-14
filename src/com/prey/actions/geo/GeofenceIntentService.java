package com.prey.actions.geo;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.drive.internal.AddEventListenerRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.prey.R;

public class GeofenceIntentService extends IntentService {
    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";

    public GeofenceIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            //todo error process
        } else {
            int transitionType = LocationClient.getGeofenceTransition(intent);
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);

            
               
                
                for (Geofence geofence : triggerList) {
                    generateNotification(geofence.getRequestId(), "("+getAddress(geofence.getRequestId())+")"+transitionMsg(transitionType));
                }
            }
        }
    }
    
    public String getAddress(String requestId){
    	ArrayList<Store> storeList=PreyScan.getInstance(getApplicationContext()).getStoreList();
    	String address="";
    	for(int i=0;storeList!=null&&i<storeList.size();i++){
    		Store store=storeList.get(i);
    		if (store.id.equals(requestId)){
    			address=store.address;
    			break;
    		}
    	}
    	return address;
    }
    public String transitionMsg(int transitionType){
    	String msg="";
    	switch (transitionType) {
		case Geofence.GEOFENCE_TRANSITION_ENTER:
			msg= "ENTER";
			break;
		case Geofence.GEOFENCE_TRANSITION_DWELL:
			msg= "DWELL";
			break;
		default:
			msg= "EXIT";
			break;
		}
    	return msg;
    }

    private void generateNotification(String locationId, String address) {
    	Toast.makeText(this, locationId+" "+address, Toast.LENGTH_LONG).show();;
    	 
        long when = System.currentTimeMillis();
        /*
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("id", locationId);
        notifyIntent.putExtra("address", address);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
*/
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(locationId)
                        .setContentText(address)
                      //  .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(when);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) when, builder.build());
       
    }
}

 