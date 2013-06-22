/* GetVelo
 * Copyright (C) 2013 Boris Fritscher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package ch.fritscher.getvelo.ui;

import ch.fritscher.getvelo.GetVeloApplication;
import ch.fritscher.getvelo.R;
import ch.fritscher.getvelo.model.Station;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import android.widget.RemoteViews;

public class GetVeloWidget extends AppWidgetProvider {

	public static String REFRESH_ACTION = "ch.fritscher.getvelo.widget.REFRESH_ACTION";
	
	@Override
    public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals(REFRESH_ACTION)) {    
			updateAppWidget(context, intent.getExtras().getInt("appWidgetId"));
		}	
		super.onReceive(context, intent);
	}
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            WidgetConfigActivity.deleteStationQueryPref(context, appWidgetIds[i]);
        }
    }
    
    static void updateAppWidget(Context context, int appWidgetId) {
        // To prevent any ANR timeouts, we perform the update in a service
    	String stationQuery = WidgetConfigActivity.loadStationQueryPref(context, appWidgetId);
    	if(stationQuery != null){
    		Intent i = new Intent(context, UpdateService.class);
        	i.putExtra("appWidgetId", appWidgetId);
            context.startService(i);	
    	}
    }

    public static class UpdateService extends Service {    	
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Build the widget update for today
        	int appWidgetId = intent.getExtras().getInt("appWidgetId");
        	String stationQuery = WidgetConfigActivity.loadStationQueryPref(this, appWidgetId);
            RemoteViews updateViews = buildUpdate(this, appWidgetId, stationQuery);

            // Push update for this widget to the home screen
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(appWidgetId, updateViews);
            
            return START_STICKY;
        }
        
        private Station findStationById(String id){
        	for(Station s : GetVeloApplication.stations){
        		if(s.getId().toString().equals(id)) return s;
        	}
        	return null;
        }

        /**
         * Build a widget update  Will block until the online API returns.
         */
        public RemoteViews buildUpdate(Context context, int appWidgetId, String stationQuery) {
        	
            // Build an update that holds the updated widget contents
        	RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);
        	
        	
        	
        	try {
        		//TODO do once for all widget not for each...
        		((GetVeloApplication) getApplication()).parser.update(context);
        		Station station = findStationById(stationQuery);
        		if(station != null){
            		updateViews.setTextViewText(R.id.item_station_distance, "");
            		
            		updateViews.setTextViewText(R.id.item_station_name, station.getName());
                    if(station.getStatus().equals(Station.ONLINE)){
                    	updateViews.setTextColor(R.id.item_station_name, context.getResources().getColor(android.R.color.primary_text_light));
                    }else if (station.getStatus().equals(Station.CONSTRUCTION)) {
                    	updateViews.setTextColor(R.id.item_station_name, context.getResources().getColor(R.color.orange));
            		}else {
            			updateViews.setTextColor(R.id.item_station_name, context.getResources().getColor(R.color.custom_red));
                    }
                    updateViews.setTextViewText(R.id.item_station_bikes, station.getBikes().toString());
                    if(station.getBikes() == 0){
                    	updateViews.setTextColor(R.id.item_station_bikes, context.getResources().getColor(R.color.custom_red));
                    }else{
                    	 updateViews.setTextColor(R.id.item_station_bikes, context.getResources().getColor(android.R.color.primary_text_light));       	
                    }
                    //do not display when we don't have rack information (-1)
                    if(station.getRacks() >= 0){
                    	updateViews.setTextViewText(R.id.item_station_racks, station.getRacks().toString());
                    }else{
                    	updateViews.setTextViewText(R.id.item_station_racks, "");
                    }
                    if(station.getRacks() == 0){
                    	updateViews.setTextColor(R.id.item_station_racks, context.getResources().getColor(R.color.custom_red));
                    }else{
                    	updateViews.setTextColor(R.id.item_station_racks, context.getResources().getColor(android.R.color.primary_text_light));
                    }
        		}
        		/*
        		 * TODO: handle boroadcast to do update...
        		Intent onClickIntent;
        		PendingIntent onClickPendingIntent;
        		onClickIntent = new Intent(context, GetVeloWidget.class);
        		onClickIntent.setAction(GetVeloWidget.REFRESH_ACTION);
        		onClickIntent.putExtra("appWidgetId", appWidgetId);
        		onClickPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        		updateViews.setOnClickPendingIntent(R.id.widget_click_refresh, onClickPendingIntent);
        		*/

    		} catch(Exception e) {
    			e.printStackTrace();
    			//TODO: error handling
    		}
            return updateViews;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
    }
}
