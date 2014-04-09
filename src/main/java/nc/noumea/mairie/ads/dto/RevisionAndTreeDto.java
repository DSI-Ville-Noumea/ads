package nc.noumea.mairie.ads.dto;

public class RevisionAndTreeDto {

	private RevisionDto revision;
	private NoeudDto tree;

	public RevisionDto getRevision() {
		return revision;
	}

	public void setRevision(RevisionDto revision) {
		this.revision = revision;
	}

	public NoeudDto getTree() {
		return tree;
	}

	public void setTree(NoeudDto tree) {
		this.tree = tree;
	}
}
