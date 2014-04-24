package nc.noumea.mairie.ads.dto;

public class PairOfDiffNoeudDto {

	private DiffNoeudDto left;
	private DiffNoeudDto right;

	public PairOfDiffNoeudDto() {
	}

	public PairOfDiffNoeudDto(DiffNoeudDto left, DiffNoeudDto right) {
		this.left = left;
		this.right = right;
	}

	public DiffNoeudDto getLeft() {
		return left;
	}

	public void setLeft(DiffNoeudDto left) {
		this.left = left;
	}

	public DiffNoeudDto getRight() {
		return right;
	}

	public void setRight(DiffNoeudDto right) {
		this.right = right;
	}

	public static PairOfDiffNoeudDto of(DiffNoeudDto left, DiffNoeudDto right) {
		return new PairOfDiffNoeudDto(left, right);
	}
}
