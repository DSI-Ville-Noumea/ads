package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.List;

public class MailADSDto {
	List<String>	listeDestinataire	= new ArrayList<String>();
	List<String>	listeCopie			= new ArrayList<String>();
	List<String>	listeCopieCachee	= new ArrayList<String>();

	public MailADSDto(List<String> listDest, List<String> listCopie, List<String> listCopieCachee) {
		if (listDest != null) {
			listeDestinataire = listDest;
		}
		if (listCopie != null) {
			listeCopie = listCopie;
		}
		if (listCopieCachee != null) {
			listeCopieCachee = listCopieCachee;
		}
	}

	public List<String> getListeDestinataire() {
		return listeDestinataire;
	}

	public void setListeDestinataire(List<String> listeDestinataire) {
		this.listeDestinataire = listeDestinataire;
	}

	public List<String> getListeCopie() {
		return listeCopie;
	}

	public void setListeCopie(List<String> listeCopie) {
		this.listeCopie = listeCopie;
	}

	public List<String> getListeCopieCachee() {
		return listeCopieCachee;
	}

	public void setListeCopieCachee(List<String> listeCopieCachee) {
		this.listeCopieCachee = listeCopieCachee;
	}

}
