package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RevisionTreeViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ViewModelHelper viewModelHelper;

	@Wire("#tree")
	private Tree tree;

	private NoeudDto root;

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

	private boolean editMode;

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	private String filter;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	public RevisionTreeViewModel() {

	}

	protected DefaultTreeNode<NoeudDto> buildTreeNodes(NoeudDto noeud) {

		List<DefaultTreeNode<NoeudDto>> enfants = new ArrayList<>();

		for (NoeudDto enfant : noeud.getEnfants()) {
			enfants.add(buildTreeNodes(enfant));
		}

		return new DefaultTreeNode<>(noeud, enfants);
	}

	protected NoeudDto buildTreeNodes(TreeNode<NoeudDto> noeud) {

		NoeudDto dto = noeud.getData();
		dto.getEnfants().clear();

		for (TreeNode<NoeudDto> enfant : noeud.getChildren()) {
			dto.getEnfants().add(buildTreeNodes(enfant));
		}

		return dto;
	}

	@Command
	@NotifyChange("noeudTree")
	public void showHideNodes() {

		if (tree == null || getNoeudTree() == null)
			return;

		// Rebuild entire tree from NoeudDto instance (root node)
		setNoeudTree(new DefaultTreeModel<>(buildTreeNodes(root), true));

		// Then filter out the nodes not matching the filter
		showHideNodes(getNoeudTree().getRoot());
	}

	private boolean showHideNodes(TreeNode<NoeudDto> node) {

		if (node == null)
			return false;

		ArrayList<TreeNode<NoeudDto>> childrenToRemove = new ArrayList<>();
		boolean hasAChildMatching = false;

		for (TreeNode<NoeudDto> enfant : node.getChildren()) {
			boolean isFilterPresent = showHideNodes(enfant);
			if (!isFilterPresent)
				childrenToRemove.add(enfant);
			hasAChildMatching = hasAChildMatching || isFilterPresent;
		}

		if (StringUtils.containsIgnoreCase(node.getData().getSigle(), filter)) {
			return true;
		}

		for (TreeNode<NoeudDto> enfant : childrenToRemove) {
			node.remove(enfant);
		}

		return hasAChildMatching;
	}

	@GlobalCommand
	@NotifyChange({ "noeudTree", "selectedTreeItem", "filter" })
	public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {

		if (revision == null) {
			// Set a default non null node to prevent ZK from bugging with next
			// new value
			setNoeudTree(new DefaultTreeModel<>(new DefaultTreeNode<>(new NoeudDto())));
			return;
		}

		filter = "";
		setSelectedTreeItem(null);
		root = treeConsultationService.getTreeOfSpecificRevision(revision.getIdRevision());
		setNoeudTree(new DefaultTreeModel<>(buildTreeNodes(root), true));
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
		selectedTreeItem.getData().getEnfants().add(n);
		TreeNode<NoeudDto> newNode = new DefaultTreeNode<>(n, new ArrayList<DefaultTreeNode<NoeudDto>>());
		selectedTreeItem.add(newNode);
		setSelectedTreeItem(newNode);
		Map<String, Object> params = new HashMap<>();
		params.put("treeNode", newNode.getData());
		viewModelHelper.postGlobalCommand(null, null, "revisionTreeNodeSelectedChangeCommand", params);
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
		filter = "";
		// #16263 bug suppression
//		showHideNodes();
		Map<String, Object> params = new HashMap<>();
		params.put("currentRevisionTree", buildTreeNodes(noeudTree.getRoot()));
		viewModelHelper.postGlobalCommand(null, null, "thisIsTheCurrentRevisionTree", params);
	}

	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void toggleEditModeGlobalCommand(@BindingParam("editMode") boolean editMode) {
		this.editMode = editMode;
	}
}
