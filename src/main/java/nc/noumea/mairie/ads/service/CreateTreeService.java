package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTreeService implements ICreateTreeService {

	@Autowired
	private ITreeRepository treeRepository;
	
	@Autowired
	private IAdsRepository adsRepository;
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode) {
		
		Revision newRevision = new Revision();
		newRevision.setIdAgent(revision.getIdAgent());
		newRevision.setDateEffet(revision.getDateEffet());
		newRevision.setDateDecret(revision.getDateDecret());
		newRevision.setDescription(revision.getDescription());
		newRevision.setDateModif(new DateTime().toDate());
		
		Noeud racine = buildCoreNoeuds(rootNode, newRevision);
		
		treeRepository.persistEntity(newRevision);
		treeRepository.persistEntity(racine);
	}
	
	protected Noeud buildCoreNoeuds(NoeudDto noeudDto, Revision revision) {
		
		Noeud newNode = new Noeud();
		newNode.setIdService(noeudDto.getIdService());
		if (newNode.getIdService().equals(0)) 
			newNode.setIdService(treeRepository.getNextServiceId());
		newNode.setLabel(noeudDto.getLabel());
		newNode.setRevision(revision);
		newNode.setSigle(noeudDto.getSigle());
		newNode.setTypeNoeud(adsRepository.get(TypeNoeud.class, noeudDto.getIdTypeNoeud()));

		for (NoeudDto enfantDto : noeudDto.getEnfants()) {
			Noeud enfant = buildCoreNoeuds(enfantDto, revision);
			enfant.addParent(newNode);
		}
		
		return newNode;
	}

	@Override
	public NoeudDto createNewDtoTreeFromLatestRevision() {
		// TODO Auto-generated method stub
		return new NoeudDto();
	}

}
