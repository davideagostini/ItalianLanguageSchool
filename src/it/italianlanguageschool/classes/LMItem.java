package it.italianlanguageschool.classes;

import java.io.Serializable;

public class LMItem implements Serializable {

	private static final long serialVersionUID = -8310665582414232777L;
	private String id;
	private String titolo;
	private String indirizzo;
	private String descrizione;
	private String thumb_image;
	
	public LMItem(String id, String titolo, String indirizzo, String descrizione, String thumb_image) {
		this.id = id;
		this.titolo = titolo;
		this.indirizzo = indirizzo;
		this.descrizione = descrizione;
		this.thumb_image = thumb_image;
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

	public String getThumb_image() {
		return thumb_image;
	}

	public void setThumb_image(String thumb_image) {
		this.thumb_image = thumb_image;
	}
}
