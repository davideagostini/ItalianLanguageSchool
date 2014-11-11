package it.italianlanguageschool.classes;

public class MapMarker {
	
	private String id;
	private String lat;
	private String lng;
	private String title;
	private String country;
	
	public MapMarker(String id, String lat, String lng, String title, String country) {
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.title = title;
		this.country = country;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
