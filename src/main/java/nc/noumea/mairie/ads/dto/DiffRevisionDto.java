package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Revision;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class DiffRevisionDto {

	private RevisionDto sourceRevision;
	private RevisionDto targetRevision;

	private List<DiffNoeudDto> addedNodes;
	private List<DiffNoeudDto> removedNodes;
	private List<PairOfDiffNoeudDto> movedNodes;
	private List<PairOfDiffNoeudDto> modifiedNodes;

	public DiffRevisionDto(Revision revSource, Revision revTarger) {
		sourceRevision = new RevisionDto(revSource);
		targetRevision = new RevisionDto(revTarger);
		addedNodes = new ArrayList<>();
		removedNodes = new ArrayList<>();
		movedNodes = new ArrayList<>();
		modifiedNodes = new ArrayList<>();
	}

	public RevisionDto getSourceRevision() {
		return sourceRevision;
	}

	public void setSourceRevision(RevisionDto sourceRevision) {
		this.sourceRevision = sourceRevision;
	}

	public RevisionDto getTargetRevision() {
		return targetRevision;
	}

	public void setTargetRevision(RevisionDto targetRevision) {
		this.targetRevision = targetRevision;
	}

	public List<DiffNoeudDto> getAddedNodes() {
		return addedNodes;
	}

	public void setAddedNodes(List<DiffNoeudDto> addedNodes) {
		this.addedNodes = addedNodes;
	}

	public List<DiffNoeudDto> getRemovedNodes() {
		return removedNodes;
	}

	public void setRemovedNodes(List<DiffNoeudDto> removedNodes) {
		this.removedNodes = removedNodes;
	}

	public List<PairOfDiffNoeudDto> getMovedNodes() {
		return movedNodes;
	}

	public void setMovedNodes(List<PairOfDiffNoeudDto> movedNodes) {
		this.movedNodes = movedNodes;
	}

	public List<PairOfDiffNoeudDto> getModifiedNodes() {
		return modifiedNodes;
	}

	public void setModifiedNodes(List<PairOfDiffNoeudDto> modifiedNodes) {
		this.modifiedNodes = modifiedNodes;
	}
}
