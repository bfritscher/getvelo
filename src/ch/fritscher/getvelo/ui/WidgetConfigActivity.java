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
import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class WidgetConfigActivity extends Activity {
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static final String PREFS_NAME = "ch.fritscher.getvelo.ui.WidgetProvider";
	private static final String PREF_KEY = "gv_";
	private Spinner stationSpinner;
	private ArrayAdapter<Station> stationAdapter;
	private ProgressDialog dialog; 
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.widget_config);
        
        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        stationSpinner = (Spinner) findViewById(R.id.widgetConfigStationlist);
        stationAdapter = new ArrayAdapter<Station>(this, android.R.layout.simple_spinner_item);
        stationSpinner.setAdapter(stationAdapter);

        findViewById(R.id.widgetConfigChoose).setOnClickListener(mOnClickListener);
        new GetVeloStatusSync().execute();
    }

	public class GetVeloStatusSync extends AsyncTask<Void, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(WidgetConfigActivity.this, getString(R.string.network_list), getString(R.string.loading));
		}

		@Override
		protected  Boolean doInBackground(Void... urls) {	
			Boolean result = false;
        	try {
        		result = ((GetVeloApplication) getApplication()).parser.update(WidgetConfigActivity.this);

    		} catch(Exception e) {
    			e.printStackTrace();
    		}
	        return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(!result){
    			Toast toast = Toast.makeText(WidgetConfigActivity.this, WidgetConfigActivity.this.getString(R.string.error_loading_list), Toast.LENGTH_LONG);
    			toast.show();
			}else{
				stationAdapter.clear();			
				for (Station station : GetVeloApplication.stations) {
					stationAdapter.add(station);
				}
				stationAdapter.notifyDataSetChanged();	
			}
			dialog.dismiss();
		}
		
	}
	
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetConfigActivity.this;

            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            Station selectedStation = (Station) stationSpinner.getSelectedItem();
            if(selectedStation != null){
	            String stationQuery = selectedStation.getId().toString();
	            saveStationQueryPref(context, mAppWidgetId, stationQuery);
	
	            // Push widget update to surface with newly set prefix
	            GetVeloWidget.updateAppWidget(context, mAppWidgetId);
	
	            // Make sure we pass back the original appWidgetId
	            Intent resultValue = new Intent();
	            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	            setResult(RESULT_OK, resultValue);
            }
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveStationQueryPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putString(PREF_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadStationQueryPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(PREF_KEY + appWidgetId, null);
    }
    
    static void deleteStationQueryPref(Context context, int appWidgetId) {
    	SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
    	prefs.remove(PREF_KEY + appWidgetId);
    }


}
