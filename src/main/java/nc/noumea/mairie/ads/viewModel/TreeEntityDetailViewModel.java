package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.service.IReferenceDataService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.converter.ObjectBooleanConverter;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TreeEntityDetailViewModel {

	@WireVariable
	private IReferenceDataService referenceDataService;

	@WireVariable
	private ViewModelHelper viewModelHelper;

	private EntiteDto selectedEntite;

	private StatutEntiteEnum selectedStatut;

	public EntiteDto getSelectedEntite() {
		return selectedEntite;
	}

	public void setSelectedEntite(EntiteDto selectedEntite) {
		this.selectedEntite = selectedEntite;
	}

	public ReferenceDto getSelectedType() {

		if (selectedEntite == null || selectedEntite.getTypeEntite() == null
				|| selectedEntite.getTypeEntite().getId() == null)
			return null;

		for (ReferenceDto ref : dataList) {
			if (ref.getId().equals(selectedEntite.getTypeEntite().getId()))
				return ref;
		}

		return null;
	}

	public void setSelectedType(ReferenceDto selectedType) {
		if (selectedEntite != null)
			selectedEntite.setTypeEntite(selectedType);
	}

	private List<ReferenceDto> dataList;

	private List<StatutEntiteEnum> statutList;

	public List<ReferenceDto> getDataList() {
		return dataList;
	}

	public void setDataList(List<ReferenceDto> dataList) {
		this.dataList = dataList;
	}

	private boolean editMode;

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public TreeEntityDetailViewModel() {
		dataList = new ArrayList<>();
		statutList = new ArrayList<>();
	}

	ObjectBooleanConverter actifConverter = new ObjectBooleanConverter();

	@Init
	@NotifyChange({ "dataList", "statutList" })
	public void initViewModel() {
		dataList = referenceDataService.getReferenceDataListTypeEntite();
		statutList = StatutEntiteEnum.getAllStatutEntiteEnum();
	}

	@GlobalCommand
	@NotifyChange({ "selectedEntite", "selectedType", "selectedStatut" })
	public void revisionTreeEntitySelectedChangeCommand(@BindingParam("treeNode") EntiteDto treeNode) {
		this.setSelectedEntite(treeNode);
		this.setSelectedStatut(StatutEntiteEnum.getStatutEntiteEnum(treeNode.getIdStatut()));
	}

	@GlobalCommand
	@NotifyChange({ "selectedEntite", "selectedType", "selectedStatut" })
	public void updateSelectedRevision() {
		// This global command is executed here in order to clear the display of
		// a previously selected node of a different revision
		this.setSelectedEntite(null);
		this.setSelectedStatut(null);
	}

	// @Command
	// public void createNewTypeCommand() {
	// Executions.createComponents("newReferenceData.zul", null, null);
	// }

	@GlobalCommand
	@NotifyChange({ "dataList", "selectedType", "selectedStatut" })
	public void typeEntiteListChangedGlobalCommand() {
		initViewModel();
	}

	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void toggleEditModeGlobalCommand(@BindingParam("editMode") boolean editMode) {
		this.editMode = editMode;
	}

	public List<StatutEntiteEnum> getStatutList() {
		return statutList;
	}

	public void setStatutList(List<StatutEntiteEnum> statutList) {
		this.statutList = statutList;
	}

	public StatutEntiteEnum getSelectedStatut() {
		return selectedStatut;
	}

	public void setSelectedStatut(StatutEntiteEnum selectedStatut) {
		this.selectedStatut = selectedStatut;
	}

	// @Command
	// public void toggleActifSelectedEntityCommand() {
	//
	// if (this.selectedEntite == null)
	// return;
	//
	// viewModelHelper.postNotifyChange(null, null, this.selectedEntite,
	// "actif");
	// }
}
