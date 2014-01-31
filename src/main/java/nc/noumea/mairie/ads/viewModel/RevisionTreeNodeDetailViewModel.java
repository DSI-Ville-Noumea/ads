package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RevisionTreeNodeDetailViewModel {

	@WireVariable
	private IReferenceDataService referenceDataService;
	
	private NoeudDto selectedNoeud;

	public NoeudDto getSelectedNoeud() {
		return selectedNoeud;
	}

	public void setSelectedNoeud(NoeudDto selectedNoeud) {
		this.selectedNoeud = selectedNoeud;
	}

	public ReferenceDto getSelectedType() {
		
		if (selectedNoeud == null || selectedNoeud.getIdTypeNoeud() == null)
			return null;
		
		for (ReferenceDto ref : dataList) {
			if (ref.id  == selectedNoeud.getIdTypeNoeud())
				return ref;
		}
		
		return null;
	}

	public void setSelectedType(ReferenceDto selectedType) {
		if (selectedNoeud != null)
			selectedNoeud.setIdTypeNoeud(selectedType.getId());
	}
	
	private List<ReferenceDto> dataList;

	public List<ReferenceDto> getDataList() {
		return dataList;
	}

	public void setDataList(List<ReferenceDto> dataList) {
		this.dataList = dataList;
	}
	
	public RevisionTreeNodeDetailViewModel() {
		dataList = new ArrayList<ReferenceDto>();
	}
	
	@Init
	@NotifyChange("dataList")
	public void initViewModel() {
		dataList = referenceDataService.getReferenceDataListTypeNoeud();
	}
	
	@GlobalCommand
	@NotifyChange({"selectedNoeud", "selectedType"})
	public void revisionTreeNodeSelectedChangeCommand(@BindingParam("treeNode") NoeudDto treeNode) {
		this.setSelectedNoeud(treeNode);
	}

	@GlobalCommand
	@NotifyChange({"selectedNoeud", "selectedType"})
	public void updateSelectedRevision() {
		// This global command is executed here in order to clear the display of
		// a previously selected node of a different revision
		this.setSelectedNoeud(null);
	}
	
	@Command
	public void createNewTypeCommand() {
		Executions.createComponents("newReferenceData.zul", null, null);
	}
	
	@GlobalCommand
	@NotifyChange({"dataList", "selectedType"})
	public void typeNoeudListChangedGlobalCommand() {
		initViewModel();
	}
}
