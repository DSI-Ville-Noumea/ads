package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.DiffRevisionDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionAndTreeDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IRevisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/revision", produces = { "application/json", "application/xml" })
public class RevisionController {

	private final Logger logger = LoggerFactory.getLogger(RevisionController.class);

	@Autowired
	private IRevisionService revisionService;

	@Autowired
	private ICreateTreeService createTreeService;

	/**
	 * <strong>Service : </strong>Retourne la liste des révisions existantes.<br/>
	 * <strong>Description : </strong>Ce service retourne la liste complète de toutes les révisions de l'arbre triées par ordre de date d'effet descendantes et date de modification descendantes.<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<RevisionDto> getRevisions() {

		logger.debug("entered GET [revision/] => getRevisions");
		return revisionService.getRevisionsByDateEffetDesc();
	}

	/**
	 * <strong>Service : </strong>Retourne le détail d'une révision donnée.<br/>
	 * <strong>Description : </strong>Ce service retourne le détail d'une révision donnée en paramètre.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idRevision : L'id de la révision.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{idRevision}")
	@ResponseBody
	public RevisionDto getRevision(@PathVariable Long idRevision) {

		logger.debug("entered GET [revision/] => getRevision with parameter idRevision [{}]", idRevision);
		return revisionService.getRevisionById(idRevision);
	}

	/**
	 * <strong>Service : </strong>Sauve une nouvelle révision de l'arbre.<br/>
	 * <strong>Description : </strong>Ce service permet de créer une nouvelle révision de l'arbre qui viendra s'ajouter aux précédentes.<br/>
	 * Seule une révision avec une date d'effet supérieure à la dernière révision ajoutée peut être créée.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>revisionAndTreeDto : Les informations détaillées pour la nouvelle révision ainsi que la totalité de l'arbre des services.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "", consumes = "application/json")
	@ResponseBody
	public List<ErrorMessageDto> saveNewRevision(@RequestBody RevisionAndTreeDto revisionAndTreeDto) {

		logger.debug("entered POST [revision/] => saveNewRevision");
		return createTreeService.createTreeFromRevisionAndNoeuds(revisionAndTreeDto.getRevision(), revisionAndTreeDto.getTree());

	}

	/**
	 * <strong>Service : </strong>Rollback de l'arbre des services à une révision donnée.<br/>
	 * <strong>Description : </strong>Ce service permet de restaurer une ancienne version de l'arbre de services qui a déjà été appliquée (dont la date d'effet est dans le passé).
	 * Une nouvelle version est alors créée avec le contenu de l'arbre à l'identique de la version restaurée.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>revision : Les informations détaillées pour la nouvelle révision (note: la date de décrêt, date d'effet et description seront automatiquement générés).</li>
	 * <li>idRevision : L'ID de révision à restaurer.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "rollback/{idRevision}", consumes = "application/json")
	@ResponseBody
	public List<ErrorMessageDto> rollbackToRevision(@RequestBody RevisionDto revision, @PathVariable Long idRevision) {

		logger.debug("entered POST [revision/rollback/{}] => rollbackToRevision", idRevision);
		return revisionService.rollbackToPreviousRevision(revision, idRevision);

	}

	/**
	 * <strong>Service : </strong>Retourne le diff entre deux révision de l'arbre des services.<br/>
	 * <strong>Description : </strong>Ce service permet de lister les différences existantes entre deux versions d'un arbre.<br/>
	 * Les différences sont retournées sous la form d'une liste de services ajoutés, supprimés, modifiés et déplacés (dont le parent direct a changé).<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idRevision : La révision à partir de laquelle effectuer le diff.</li>
	 * <li>idRevisionTo : La révision cible avec laquelle effectuer le diff.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "{idRevision}/diff/{idRevisionTo}")
	@ResponseBody
	public DiffRevisionDto diffBetweenRevisions(@PathVariable Long idRevision, @PathVariable Long idRevisionTo) {

		logger.debug("entered GET [revision/{}/diff/{}] => diffBetweenRevisions", idRevision, idRevisionTo);
		return revisionService.getRevisionsDiff(idRevision, idRevisionTo);

	}
}
