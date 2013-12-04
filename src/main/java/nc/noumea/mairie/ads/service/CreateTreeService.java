package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class CreateTreeService implements ICreateTreeService {

	@Override
	public void createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode) {
		
		Revision newRevision = new Revision();
		newRevision.setIdAgent(revision.getIdAgent());
		newRevision.setDateEffet(revision.getDateEffet());
		newRevision.setDateDecret(revision.getDateDecret());
		newRevision.setDescription(revision.getDescription());
		newRevision.setDateModif(new DateTime().toDate());
		
		Noeud racine = buildCoreNoeuds(rootNode, newRevision);
		
		newRevision.persist();
		racine.persist();
	}
	
	protected Noeud buildCoreNoeuds(NoeudDto noeudDto, Revision revision) {
		
		Noeud newNode = new Noeud();
		newNode.setIdService(noeudDto.getIdService());
		newNode.setLabel(noeudDto.getLabel());
		newNode.setRevision(revision);
		newNode.setSigle(noeudDto.getSigle());

		for (NoeudDto enfantDto : noeudDto.getEnfants()) {
			Noeud enfant = buildCoreNoeuds(enfantDto, revision);
			enfant.addParent(newNode);
		}
		
		return newNode;
	}

}
