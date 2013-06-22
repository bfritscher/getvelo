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
import ch.fritscher.getvelo.model.StationAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import greendroid.app.GDListActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.LoaderActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.ActionBarItem.Type;

@SuppressLint("NewApi")
public class HomeActivity extends GDListActivity {

	private LoaderActionBarItem loaderItem;
	private StationAdapter mAdapter;
	public static ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loaderItem = (LoaderActionBarItem) addActionBarItem(Type.Refresh, R.id.action_bar_refresh);
		addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(getResources(), R.drawable.gd_action_bar_edit)), R.id.action_bar_edit);
		mAdapter = (StationAdapter) getLastNonConfigurationInstance();
		if(mAdapter == null){
			mAdapter = new StationAdapter(this);
		 }
		setListAdapter(mAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_refresh:
	    	doRefresh();
	        return true;
	    case R.id.menu_settings:
	    	showSettings();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return mAdapter;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.registerForLocationUpdate();
		doRefresh();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, StationActivity.class);
		i.putExtra("station", mAdapter.getItem(position));
		startActivity(i);
		super.onListItemClick(l, v, position, id);
	}
	
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_edit:
            	showSettings();
                break;

            case R.id.action_bar_refresh:
            	doRefresh();
            	break;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
        return true;
    }
    
    private void showSettings(){
    	Intent myIntent = new Intent(this, GetVeloPreferenceActvity.class);
        startActivityForResult(myIntent, 0);
    }
    
    private void doRefresh(){
    	new GetVeloStatusSync().execute();
    }
    
	public class GetVeloStatusSync extends AsyncTask<Void, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			loaderItem.setLoading(true);
			super.onPreExecute();
		}

		@Override
		protected  Boolean doInBackground(Void... urls) {	
			Boolean result = false;
        	try {
        		result = ((GetVeloApplication) getApplication()).parser.update(HomeActivity.this);

    		} catch(Exception e) {
    			e.printStackTrace();
    		}
	        return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(!result){
    			Toast toast = Toast.makeText(HomeActivity.this, HomeActivity.this.getString(R.string.error_loading_list), Toast.LENGTH_LONG);
    			toast.show();
			}else{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        		String networkID = prefs.getString(GetVeloApplication.PREFS_DEFAULT_NETWORK, "");
				mAdapter.clear();
				for (Station station : GetVeloApplication.stations) {
					if(networkID.equals("") || station.getNetwork().getId() == Integer.parseInt(networkID)){
						mAdapter.add(station);	
					}
				}
				mAdapter.notifyDataSetChanged();
			}
			loaderItem.setLoading(false);
		}
		
	}
	
}
