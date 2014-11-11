package it.italianlanguageschool.classes;

import java.io.Serializable;

public class SchoolItem implements Serializable {
	
	private static final long serialVersionUID = -1264237789347285134L;
	private String id;
	private String titolo;
	private String indirizzo;
	private String descrizione;
	private String thumbnail;
	
	public SchoolItem(String id, String titolo, String indirizzo, String descrizione, String thumbnail) {
		this.id = id;
		this.titolo = titolo;
		this.indirizzo = indirizzo;
		this.descrizione = descrizione;
		this.thumbnail = thumbnail;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
