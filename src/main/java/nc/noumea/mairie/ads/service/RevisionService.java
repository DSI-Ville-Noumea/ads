package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RevisionService implements IRevisionService {

	@Autowired
	private IRevisionRepository revisionRepository;

	@Override
	public List<RevisionDto> getRevisionsByDateEffetDesc() {

		List<RevisionDto> revisions = new ArrayList<RevisionDto>();

		for (Revision rev : revisionRepository.getAllRevisionsByDateEffetDesc()) {
			revisions.add(new RevisionDto(rev));
		}

		return revisions;
	}
}
