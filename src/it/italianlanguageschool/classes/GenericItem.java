package it.italianlanguageschool.classes;

import java.io.Serializable;

public class GenericItem implements Serializable {
	
	private static final long serialVersionUID = -3410801180484638220L;
	private String id;
	private String title;
	
	public GenericItem(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
