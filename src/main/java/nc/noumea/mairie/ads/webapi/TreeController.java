package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("arbre")
public class TreeController {

	private Logger logger = LoggerFactory.getLogger(TreeController.class);
	
	@Autowired
	private ITreeConsultationService treeConsultationService;
	
	@RequestMapping(value = "/", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public ResponseEntity<String> getWholeTreeFromRoot() {
		
		logger.debug(
				"entered GET [arbre/] => getWholeTreeFromRoot");
		
		NoeudDto result = treeConsultationService.getTreeOfLatestRevisionTree();
		
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class").deepSerialize(result), HttpStatus.OK);
	}
	
	/**
	 * Exports GRAPHML version of the latest ADS
	 * This export is customized for a YED use (label and rollover messages)
	 * @return
	 */
	@RequestMapping(value = "/graphml", produces = "application/graphml+xml", method = RequestMethod.GET)
	public ResponseEntity<byte[]> exportWholeTreeFromRootAsGraphMl() {
		
		logger.debug(
				"entered GET [arbre/graphml] => exportWholeTreeFromRoot");
		
		byte[] reponseData = treeConsultationService.exportTreeOfLatestRevisionToGraphMl();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("attachment; filename=\"ads.graphml\""));

		return new ResponseEntity<byte[]>(reponseData, headers, HttpStatus.OK);
	}
}
