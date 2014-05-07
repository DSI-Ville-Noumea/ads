package nc.noumea.mairie.ads.dto;

public class PairOfDiffNoeudDto {

	private DiffNoeudDto before;
	private DiffNoeudDto after;

	public PairOfDiffNoeudDto() {
	}

	public PairOfDiffNoeudDto(DiffNoeudDto left, DiffNoeudDto right) {
		this.before = left;
		this.after = right;
	}

	public DiffNoeudDto getBefore() {
		return before;
	}

	public void setBefore(DiffNoeudDto before) {
		this.before = before;
	}

	public DiffNoeudDto getAfter() {
		return after;
	}

	public void setAfter(DiffNoeudDto after) {
		this.after = after;
	}

	public static PairOfDiffNoeudDto of(DiffNoeudDto left, DiffNoeudDto right) {
		return new PairOfDiffNoeudDto(left, right);
	}
}
