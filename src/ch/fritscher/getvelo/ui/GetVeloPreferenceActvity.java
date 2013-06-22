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

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.fritscher.getvelo.GetVeloApplication;
import ch.fritscher.getvelo.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class GetVeloPreferenceActvity extends PreferenceActivity {
	public static Map<String, String> networkList;
	private GetVeloApplication app;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        ListPreference defaultNetworkList = (ListPreference) findPreference(GetVeloApplication.PREFS_DEFAULT_NETWORK);
       
        
    	try{   		
    		app = (GetVeloApplication) getApplication();
    		if(app.parser.update(GetVeloPreferenceActvity.this)){
    			SortedMap<String, String> networkList = new TreeMap<String, String>(new GetVeloPreferenceActvity.ValueComparer(GetVeloApplication.networkTable));
    			networkList.putAll(GetVeloApplication.networkTable);
    		    int count = networkList.size();
    		    defaultNetworkList.setEntries(networkList.values().toArray(new String[count]));
    		    defaultNetworkList.setEntryValues(networkList.keySet().toArray(new String[count]));    			
    		}else{
    			throw new Exception();
    		}

	        defaultNetworkList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GetVeloPreferenceActvity.this);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(GetVeloApplication.PREFS_NETWORK_NAME, GetVeloApplication.networkTable.get(newValue));
					editor.commit();
					
					return true;
				}
			});
    		
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		finish();
    		Toast.makeText(this, getString(R.string.error_loading_list), Toast.LENGTH_SHORT).show();
    	}finally{
    		if(HomeActivity.dialog != null){
    			HomeActivity.dialog.dismiss();
    		}
    	}
        
    }
	
	/** inner class to do soring of the map **/
	private static class ValueComparer implements Comparator<String> {
		private Map<String, String>  _data = null;
		public ValueComparer (Map<String, String> data){
			super();
			_data = data;
		}

	     @Override
		public int compare(String o1, String o2) {
	    	 String e1 = _data.get(o1);
	         String e2 = _data.get(o2);
	         return e1.compareTo(e2);
	     }
	}
}
