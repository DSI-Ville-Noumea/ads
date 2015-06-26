package nc.noumea.mairie.ads.webapi;


import java.util.List;

import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/typeNoeud", produces = { "application/json", "application/xml" })
public class TypeNoeudController {

	private final Logger logger = LoggerFactory.getLogger(RevisionController.class);

	@Autowired
	private IReferenceDataService referenceDataService;

	/**
	 * <strong>Service : </strong>Liste les différents types de noeuds existants.<br/>
	 * <strong>Description : </strong>Ce service retourne la liste complète des types de noeuds existants et pouvant être utilisés.<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<ReferenceDto> getTypeNoeuds() {

		logger.debug("entered GET [typeNoeud/] => getTypeNoeuds");

		return referenceDataService.getReferenceDataListTypeNoeud();
	}
	
	/**
	 * <strong>Service : </strong>Crée ou modifie un nouveau type de noeuds.<br/>
	 * <strong>Description : </strong>Ce service crée ou modifie un nouveau type de noeuds.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>ReferenceDto : le DTO du type de noeud</li>
	 * <li> Integer id : si null : création, sinon id du type de noeud à modifier</li>
	 * <li> String label : le libellé</li>
	 * <li> boolean actif : actif ou inactif</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	@ResponseBody
	public void saveTypeNoeuds(@RequestBody ReferenceDto referenceDto) {

		logger.debug("entered POST [typeNoeud/save] => saveTypeNoeuds");

		referenceDataService.createOrModifyTypeNoeud(referenceDto);
	}
	
	/**
	 * <strong>Service : </strong>Retourne un type de noeuds.<br/>
	 * <strong>Description : </strong>Ce service retourne un type de noeuds selon l'ID en paramètre.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idTypeNoeud : ID du type de noeud</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{idTypeNoeud}")
	@ResponseBody
	public ReferenceDto getTypeNoeudsById(@PathVariable Integer idTypeNoeud) {

		logger.debug("entered GET [typeNoeud/] => getTypeNoeudsById parameter idTypeNoeud [{}]", idTypeNoeud);

		return referenceDataService.getTypeNoeudById(idTypeNoeud);
	}
	
	/**
	 * <strong>Service : </strong>Supprime ou desactive un type de noeuds.<br/>
	 * <strong>Description : </strong>Ce service supprime un type de noeuds si seulement celui-ci n'est plus utilisé par un ou des noeuds.<br/>
	 * Sinon on desactive le type de noeud
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idTypeNoeud : ID du type de noeud à supprimer ou desactiver</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/deleteOrDisable/{idTypeNoeud}")
	@ResponseBody
	public void deleteTypeNoeudsById(@PathVariable Integer idTypeNoeud) {

		logger.debug("entered GET [typeNoeud/delete/] => deleteTypeNoeudsById parameter idTypeNoeud [{}]", idTypeNoeud);
		
		try {
			referenceDataService.deleteTypeNoeudById(idTypeNoeud);
		} catch(JpaSystemException e) {
			referenceDataService.disableTypeNoeudById(idTypeNoeud);
		}
	}
}
