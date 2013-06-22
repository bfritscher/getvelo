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
import java.util.List;

import org.codehaus.jackson.annotate.JsonManagedReference;

public class Network  implements Serializable{
	
	private static final long serialVersionUID = 1271372939067355358L;
	private Integer id;
	private String name;
	private List<Station> stations;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonManagedReference("network")
	public List<Station> getStations() {
		return stations;
	}
	@JsonManagedReference("network")
	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	
}
