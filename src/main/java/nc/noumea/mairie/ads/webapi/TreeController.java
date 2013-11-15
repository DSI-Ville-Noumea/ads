package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("arbre")
public class TreeController {

	@Autowired
	private ITreeConsultationService treeConsultationService;
	
	@RequestMapping(value = "/", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public ResponseEntity<String> getWhileTreeFromRoot() {
		
		NoeudDto result = treeConsultationService.getTreeOfLatestRevisionTree();
		
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class").deepSerialize(result), HttpStatus.OK);
	}
}
