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
package ch.fritscher.getvelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import ch.fritscher.getvelo.io.MainParser;
import ch.fritscher.getvelo.model.Station;
import ch.fritscher.getvelo.ui.HomeActivity;


import greendroid.app.GDApplication;

public class GetVeloApplication extends GDApplication {

	public static final String PREFS_DEFAULT_NETWORK = "default_network";
	public static final String PREFS_SORT_ORDER = "sort_order";	
	public static final String PREFS_NETWORK_NAME = "network_name";
    public static List<Station> stations = new ArrayList<Station>();
    public static Map<String, String> networkTable = new LinkedHashMap<String, String>();
	public MainParser parser;
	
    @Override
	public void onCreate() {
		super.onCreate();
		parser = new MainParser();
	}
    
	@Override
    public Class<?> getHomeActivityClass() {
        return HomeActivity.class;
    }	
}
