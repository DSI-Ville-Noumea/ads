package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RevisionTreeViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ViewModelHelper viewModelHelper;
	
	private TreeNode<NoeudDto> selectedTreeItem;

	public TreeNode<NoeudDto> getSelectedTreeItem() {
		return selectedTreeItem;
	}

	public void setSelectedTreeItem(TreeNode<NoeudDto> selectedTreeItem) {
		this.selectedTreeItem = selectedTreeItem;
	}

	private TreeModel<TreeNode<NoeudDto>> noeudTree;

	public TreeModel<TreeNode<NoeudDto>> getNoeudTree() {
		return noeudTree;
	}

	public void setNoeudTree(TreeModel<TreeNode<NoeudDto>> noeudTree) {
		this.noeudTree = noeudTree;
	}

	public RevisionTreeViewModel() {

	}

	protected DefaultTreeNode<NoeudDto> buildTreeNodes(NoeudDto noeud) {

		List<DefaultTreeNode<NoeudDto>> enfants = new ArrayList<DefaultTreeNode<NoeudDto>>();

		for (NoeudDto enfant : noeud.getEnfants()) {
			enfants.add(buildTreeNodes(enfant));
		}

		return new DefaultTreeNode<NoeudDto>(noeud, enfants);
	}

	protected NoeudDto buildTreeNodes(TreeNode<NoeudDto> noeud) {

		NoeudDto dto = noeud.getData();
		dto.getEnfants().clear();

		for (TreeNode<NoeudDto> enfant : noeud.getChildren()) {
			dto.getEnfants().add(buildTreeNodes(enfant));
		}

		return dto;
	}

	@GlobalCommand
	@NotifyChange({ "noeudTree" })
	public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {

		if (revision == null) {
			// Set a default non null node to prevent ZK from bugging with next
			// new value
			setNoeudTree(new DefaultTreeModel<NoeudDto>(new DefaultTreeNode<NoeudDto>(new NoeudDto())));
			return;
		}

		NoeudDto root = treeConsultationService.getTreeOfSpecificRevision(revision.getIdRevision());
		setNoeudTree(new DefaultTreeModel<NoeudDto>(buildTreeNodes(root), true));
	}

	@Command
	public void onDropCommand(@BindingParam("item") DefaultTreeNode<NoeudDto> item,
			@BindingParam("newParent") DefaultTreeNode<NoeudDto> newParent) {
		item.removeFromParent();
		newParent.add(item);
	}

	@Command
	@NotifyChange({ "selectedTreeItem" })
	public void createNewNodeCommand() {
		NoeudDto n = new NoeudDto();
		n.setSigle("NOUVEAU");
		TreeNode<NoeudDto> newNode = new DefaultTreeNode<NoeudDto>(n, new ArrayList<DefaultTreeNode<NoeudDto>>());
		selectedTreeItem.add(newNode);
		setSelectedTreeItem(newNode);
	}

	@Command
	public void deleteNodeCommand() {

		if (selectedTreeItem.getParent().getData().getSigle().equals("Root")) {
			return;
		}

		selectedTreeItem.getParent().remove(selectedTreeItem);
	}
	
	/**
	 * This method is called by global command "whatIsTheCurrentRevisionTree"
	 * and answers by calling global command "thisIsTheCurrentRevisionTree" with the rootNode as parameter
	 */
	@GlobalCommand
	public void whatIsTheCurrentRevisionTree() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentRevisionTree", buildTreeNodes(noeudTree.getRoot()));
		viewModelHelper.postGlobalCommand(null, null, "thisIsTheCurrentRevisionTree", params);
	}
}
