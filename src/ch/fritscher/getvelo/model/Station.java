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

import java.io.Serializable;
import java.text.DecimalFormat;

import org.codehaus.jackson.annotate.JsonBackReference;

import android.content.Context;
import android.location.Location;

import ch.fritscher.getvelo.R;

import com.google.android.maps.GeoPoint;

public class Station  implements Serializable {
	
	public static final String ONLINE = "0";
	public static final String OFFLINE = "2";
	public static final String CONSTRUCTION = "3";
	
	private static final long serialVersionUID = 7225909951428924133L;
	private String id;
	private String city = "";
	private String name;
	private Integer bikes = 0;
	private Integer racks = -1;
	private String online;
	private Double posx;
	private Double posy;
	private Network network;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getBikes() {
		return bikes;
	}
	public void setBikes(Integer bikes) {
		this.bikes = bikes;
	}
	public Integer getRacks() {
		return racks;
	}
	public void setRacks(Integer racks) {
		this.racks = racks;
	}
	public String getStatus() {
		return online;
	}
	public void setStatus(String online) {
		this.online = online;
	}
	public Double getLat() {
		return posx;
	}
	public void setLat(Double posx) {
		this.posx = posx;
	}
	public Double getLong() {
		return posy;
	}
	public void setLong(Double posy) {
		this.posy = posy;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public Double getPosx() {
		return posx;
	}
	public void setPosx(Double posx) {
		this.posx = posx;
	}
	public Double getPosy() {
		return posy;
	}
	public void setPosy(Double posy) {
		this.posy = posy;
	}
	@JsonBackReference("network")
	public Network getNetwork() {
		return network;
	}
	@JsonBackReference("network")
	public void setNetwork(Network network) {
		this.network = network;
	}
	
	public GeoPoint getGeoPoint(){
		  return new GeoPoint(
		            (int) (posx * 1E6), 
		            (int) (posy * 1E6));
	}
	
	//TODO: refactor
	public Float getDistance(Location currentLocation){
		float[] results = new float[3];
		if(currentLocation != null){
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), this.getPosx(), this.getPosy(), results);
		}
		return results[0];
	}
	
	public String getDistanceAndBearing(Context mContext, Location currentLocation){
		String distance = "";
		String compass = "";
		float[] results = new float[3];
		if(currentLocation != null){
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), this.getPosx(), this.getPosy(), results);
			float bearing = results[1];
			if(bearing > -22.5 && bearing < 22.5){
				compass = mContext.getString(R.string.nav_n);
			}
			else if(bearing >= 22.5 && bearing <= 67.5){
				compass = mContext.getString(R.string.nav_ne);
			}
			else if(bearing > 67.5 && bearing < 112.5){
				compass = mContext.getString(R.string.nav_e);
			}
			else if(bearing >= 112.5 && bearing <= 157.5){
				compass = mContext.getString(R.string.nav_se);
			}
			else if(bearing > 157.5 || bearing < -157.5){
				compass = mContext.getString(R.string.nav_s);
			}
			else if(bearing >= -157.5 && bearing <= -112.5){
				compass = mContext.getString(R.string.nav_sw);
			}
			else if(bearing > -112.5 && bearing < -67.5){
				compass = mContext.getString(R.string.nav_w);
			}
			else{
				compass = mContext.getString(R.string.nav_nw);
			}
			
			distance = new DecimalFormat("@@").format(results[0]/1000);
			return new StringBuilder(distance).append(" km ").append(compass).toString();
		}
		return distance;
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append(getNetwork().getName()).append(" ").append(name).toString();
	}
}
