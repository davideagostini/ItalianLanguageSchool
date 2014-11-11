package it.italianlanguageschool.classes;

import java.io.Serializable;

public class NewsItem implements Serializable {

	private static final long serialVersionUID = 5179619413097178229L;
	private String url;
	private String titolo; 
	private String date;
	private String descrizione; 
	private String scuola; 
	private String thumb_image;
	
	public NewsItem(String url, String titolo, String date, String descrizione, String scuola, String thumb_image) {
		this.url = url;
		this.titolo = titolo;
		this.date = date;
		this.descrizione = descrizione;
		this.scuola = scuola;
		this.thumb_image = thumb_image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getScuola() {
		return scuola;
	}

	public void setScuola(String scuola) {
		this.scuola = scuola;
	}

	public String getThumb_image() {
		return thumb_image;
	}

	public void setThumb_image(String thumb_image) {
		this.thumb_image = thumb_image;
	}
}
