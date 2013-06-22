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
package ch.fritscher.getvelo.io;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import ch.fritscher.getvelo.GetVeloApplication;
import ch.fritscher.getvelo.R;
import ch.fritscher.getvelo.model.Network;

import org.codehaus.jackson.map.DeserializationConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainParser {
    
    public Boolean update(Context ctx) throws JsonParseException, JsonMappingException, MalformedURLException, IOException{
    	
    	//Parse publibike
		GetVeloApplication.stations.clear();
		GetVeloApplication.networkTable.clear();
    	GetVeloApplication.networkTable.put("", "-- " + ctx.getString(R.string.network_all) + " --");
    	
		for(Network n : getPubliBike()){
			GetVeloApplication.stations.addAll(n.getStations());
			GetVeloApplication.networkTable.put(n.getId().toString(), n.getName());
		}
		
		GetVeloApplication.networkTable.put("1", "Biel/Bienne");
		for(Network n : getVeloSpot()){
			GetVeloApplication.stations.addAll(n.getStations());
		}
    	return true;
    }
    
    private List<Network> getPubliBike() throws JsonParseException, JsonMappingException, MalformedURLException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(new URL("http://www.fritscher.ch/getvelo/velopass.php"), new TypeReference<ArrayList<Network>>() { });
    }
    
    private List<Network> getVeloSpot() throws JsonParseException, JsonMappingException, MalformedURLException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(new URL("http://marmix.unil.ch/velo/json"), new TypeReference<ArrayList<Network>>() { });
    }
}
