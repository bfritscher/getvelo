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

import java.util.List;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;

import ch.fritscher.getvelo.R;
import ch.fritscher.getvelo.map.StationItemizedOverlay;
import ch.fritscher.getvelo.model.Station;
import ch.fritscher.getvelo.model.StationAdapter.ViewHolder;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class StationActivity extends GDMapActivity {

	private Station station;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.station); 
		station = (Station) getIntent().getExtras().getSerializable("station");
		setTitle(station.getName());
		
		/* No more images for now
		AsyncImageView mImageView = (AsyncImageView) findViewById(R.id.image_view);
		mImageView.setUrl(GetVeloApplication.SERVER_BASE_URL + "media/stations/" + station.getId() + ".png");
		*/
		MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    MapController mc = mapView.getController();
	    mc.setCenter(station.getGeoPoint());
	    mc.setZoom(17);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
	    StationItemizedOverlay itemizedoverlay = new StationItemizedOverlay(drawable, this);
	    for (Station nearStation : station.getNetwork().getStations()) {
	    	OverlayItem overlayitem = new OverlayItem(nearStation.getGeoPoint(), nearStation.getName(), "");
	    	itemizedoverlay.addOverlay(overlayitem);
		}
	    
	    mapOverlays.add(itemizedoverlay);
	    
	    ViewHolder holder = new ViewHolder(getContentView());
	    holder.update(getApplicationContext(), station);
	}
	
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		finish();
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
