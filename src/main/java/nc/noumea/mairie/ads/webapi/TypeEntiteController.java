package nc.noumea.mairie.ads.webapi;


import java.util.List;

import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
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
@RequestMapping(value = "/api/typeEntite", produces = { "application/json", "application/xml" })
public class TypeEntiteController {

	private final Logger logger = LoggerFactory.getLogger(TypeEntiteController.class);

	@Autowired
	private IReferenceDataService referenceDataService;

	/**
	 * <strong>Service : </strong>Liste les différents types d'entité existants.<br/>
	 * <strong>Description : </strong>Ce service retourne la liste complète des types d'entité existants et pouvant être utilisés.<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<ReferenceDto> getTypesEntite() {

		logger.debug("entered GET [typeEntite/] => getTypesEntite");

		return referenceDataService.getReferenceDataListTypeEntite();
	}
	
	/**
	 * <strong>Service : </strong>Crée ou modifie un nouveau type d'entite.<br/>
	 * <strong>Description : </strong>Ce service crée ou modifie un nouveau type d'entite.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>ReferenceDto : le DTO du type d'entite</li>
	 * <li> Integer id : si null : création, sinon id du type d'entite à modifier</li>
	 * <li> String label : le libellé</li>
	 * <li> boolean actif : actif ou inactif</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	@ResponseBody
	public ReturnMessageDto saveTypeNoeuds(@RequestBody ReferenceDto referenceDto) {

		logger.debug("entered POST [typeNoeud/save] => saveTypeNoeuds");

		return referenceDataService.createOrModifyTypeEntite(referenceDto);
	}
	
	/**
	 * <strong>Service : </strong>Retourne un type d'entite.<br/>
	 * <strong>Description : </strong>Ce service retourne un type d'entite selon l'ID en paramètre.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idTypeEntite : ID du type d'entite</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{idTypeEntite}")
	@ResponseBody
	public ReferenceDto getTypeEntitesById(@PathVariable Integer idTypeEntite) {

		logger.debug("entered GET [typeEntite/] => getTypeEntitesById parameter idTypeEntite [{}]", idTypeEntite);

		return referenceDataService.getTypeEntiteById(idTypeEntite);
	}
	
	/**
	 * <strong>Service : </strong>Supprime ou desactive un type d'entite.<br/>
	 * <strong>Description : </strong>Ce service supprime un type d'entite si seulement celui-ci n'est plus utilisé par un ou des noeuds.<br/>
	 * Sinon on desactive le type d'entite
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idTypeEntite : ID du type d'entite à supprimer ou desactiver</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/deleteOrDisable/{idTypeEntite}")
	@ResponseBody
	public ReturnMessageDto deleteTypeEntitesById(@PathVariable Integer idTypeEntite) {

		logger.debug("entered GET [typeEntite/delete/] => deleteTypeEntitesById parameter idTypeEntite [{}]", idTypeEntite);
		
		try {
			return referenceDataService.deleteTypeEntiteById(idTypeEntite);
		} catch(JpaSystemException e) {
			return referenceDataService.disableTypeEntiteById(idTypeEntite);
		}
	}
}
