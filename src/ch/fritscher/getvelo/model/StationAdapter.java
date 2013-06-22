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
package ch.fritscher.getvelo.model;

import java.util.Comparator;

import ch.fritscher.getvelo.GetVeloApplication;
import ch.fritscher.getvelo.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StationAdapter extends ArrayAdapter<Station> {
	
	public static final int SORT_ID = 0;
	public static final int SORT_LOCATION = 1;
	public static final int SORT_CITY = 2;
	public static final int SORT_ABC = 3;
	
	private LayoutInflater mInflater;
	private Context mContext;
	private LocationManager aLocationManager;
	private LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			notifyDataSetChanged();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}

	};
	private String providerName;
	
	private Comparator<Station> sortCity = new Comparator<Station>() {
		@Override
		public int compare(Station object1, Station object2) {
			int result = object1.getCity().compareTo(object2.getCity());
			if(result == 0){
				return object1.getName().compareTo(object2.getName());
			}else{
				return result;
			}
		}
		
	};
	
	private Comparator<Station> sortABC = new Comparator<Station>() {
		@Override
		public int compare(Station object1, Station object2) {
			String networkName1 = GetVeloApplication.networkTable.get(object1.getNetwork().getId().toString());
			String networkName2 = GetVeloApplication.networkTable.get(object2.getNetwork().getId().toString());
			int result = 0;
			//sort by network
			if(networkName1 != null && networkName2 !=null){
				result = networkName1.compareTo(networkName2);
			}
			if(result == 0){
				//sort by city
				if(object1.getCity() != null && object2.getCity() !=null){
					result = object1.getCity().compareTo(object2.getCity());
				}	
				//sort by station name
				if(result == 0){
					return object1.getName().compareTo(object2.getName());
				}
			}
			return result;
		}
		
	};
	
	private Comparator<Station> sortId = new Comparator<Station>() {
		@Override
		public int compare(Station object1, Station object2) {
			return object1.getId().compareTo(object2.getId());
		}
		
	};
    
	public StationAdapter(Context context) {
		super(context, R.layout.station_item);
		// Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        mContext = context;
        aLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        registerForLocationUpdate();
        
	}
	
	
	
	public void registerForLocationUpdate(){
		Criteria c=new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		providerName = aLocationManager.getBestProvider(c,true);
		//TODO: change numbers! handle providers
		if(providerName != null && locationListener !=null){
			//every 5min or 200m
			aLocationManager.removeUpdates(locationListener);
			aLocationManager.requestLocationUpdates(providerName, 300000, 200, locationListener);
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		//sort before display
		//disable to avoid sort calling
		setNotifyOnChange(false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		//bug: http://code.google.com/p/android/issues/detail?id=2096
		int sort = Integer.parseInt(prefs.getString( GetVeloApplication.PREFS_SORT_ORDER, String.valueOf(SORT_LOCATION) ));
			switch (sort) {
			case SORT_ID:
				sort(sortId);
				break;
				
			case SORT_CITY:
				sort(sortCity);
				break;
			case SORT_ABC:
				sort(sortABC);
				break;
			case SORT_LOCATION:
				//TODO: strategy to switch provider or use api > 2.2
		        if(providerName != null){
		        	final Location currentLocation = aLocationManager.getLastKnownLocation(providerName);
					sort(new Comparator<Station>() {
						@Override
						public int compare(Station object1, Station object2) {
							int result = object1.getDistance(currentLocation).compareTo(object2.getDistance(currentLocation));
							if(result == 0){
								return object1.getName().compareTo(object2.getName());
							}else{
								return result;
							}
						}
						
					});
		        }
				break;
			}
		super.notifyDataSetChanged();
	}
    
    public static class ViewHolder {
        public TextView name;
        public TextView distance;
        public TextView bikes;
        public TextView racks;
        public TextView city;
        public TextView network;
    	
    	public ViewHolder(View view) {
    		name = (TextView) view.findViewById(R.id.item_station_name);
            distance = (TextView) view.findViewById(R.id.item_station_distance);
            bikes = (TextView) view.findViewById(R.id.item_station_bikes);
            racks = (TextView) view.findViewById(R.id.item_station_racks);
            city = (TextView) view.findViewById(R.id.item_city_name);
            network = (TextView) view.findViewById(R.id.item_network_name);
		}
    	
    	public void update(Context mContext, Station station){
    		ViewHolder holder = this;
    		
    		holder.network.setVisibility(View.GONE);
    		holder.city.setVisibility(View.GONE);
    		holder.distance.setText("");
    		
    		holder.name.setText(station.getName());
            if(station.getStatus().equals(Station.ONLINE)){
            	holder.name.setTextColor(mContext.getResources().getColor(android.R.color.primary_text_light));
            }else if (station.getStatus().equals(Station.CONSTRUCTION)) {
            	holder.name.setTextColor(mContext.getResources().getColor(R.color.orange));
    		}else {
            	holder.name.setTextColor(mContext.getResources().getColor(R.color.custom_red));
            }
            holder.bikes.setText(station.getBikes().toString());
            if(station.getBikes() == 0){
            	holder.bikes.setTextColor(mContext.getResources().getColor(R.color.custom_red));
            }else{
            	holder.bikes.setTextColor(mContext.getResources().getColor(android.R.color.primary_text_light));        	
            }
            //do not display when we don't have rack information (-1)
            if(station.getRacks() >= 0){
            	holder.racks.setText(station.getRacks().toString());
            }else{
            	holder.racks.setText("");
            }
            if(station.getRacks() == 0){
            	holder.racks.setTextColor(mContext.getResources().getColor(R.color.custom_red));
            }else{
            	holder.racks.setTextColor(mContext.getResources().getColor(android.R.color.primary_text_light));
            }
    	}
    }
	
	
	public View getView(int position, View convertView, ViewGroup parent)
    {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        
		//Inflate the view
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.station_item, null);
         // Creates a ViewHolder and store references to the views
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
        	// Get the ViewHolder back to get fast access to the TextView
            holder = (ViewHolder) convertView.getTag();
        }
 
        //Get the current location object
        Station station = (Station) getItem(position);
        // Bind the data efficiently with the holder.
        holder.update(mContext, station);
        
        Location currentLocation = null;
        //TODO: strategy to switch provider + refactor to use location elsewhere
        if(providerName != null){
        	currentLocation = aLocationManager.getLastKnownLocation(providerName);
        }
        holder.distance.setText(station.getDistanceAndBearing(mContext, currentLocation));
        
        
        //decide to display network separation
        if(position > 0 && getItem(position-1).getNetwork().equals(station.getNetwork())){
        	holder.network.setVisibility(View.GONE);
        }else{
        	holder.network.setVisibility(View.VISIBLE);
        	String networkName = null;
        	if(GetVeloApplication.networkTable != null){
        		networkName = GetVeloApplication.networkTable.get(station.getNetwork().getId().toString());
        	}
        	if(networkName == null){
        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        		networkName = prefs.getString(GetVeloApplication.PREFS_NETWORK_NAME, "");
        	}
        	holder.network.setText(networkName);
        }
        
        
        
        //decide to display city separation 
        if((position == 0 && station.getCity().equals(station.getNetwork().getName())) || position > 0 && getItem(position-1).getCity().equals(station.getCity()) || station.getCity().equals("")){
        	holder.city.setVisibility(View.GONE);
        }else{
        	holder.city.setVisibility(View.VISIBLE);
        	holder.city.setText(station.getCity().toString());
        }

		return convertView;
    }
	


	
}
