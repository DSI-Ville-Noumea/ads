package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.EntiteDto;
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
public class TreeViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ViewModelHelper viewModelHelper;

	@Wire("#tree")
	private Tree tree;

	private EntiteDto root;
	private TreeNode<EntiteDto> selectedTreeItem;
	private TreeModel<TreeNode<EntiteDto>> noeudTree;
	private boolean editMode;
	private String filter;

	public TreeViewModel() {
	}

	@Init
	@NotifyChange({ "noeudTree", "selectedTreeItem", "filter" })
	public void init() {

		filter = "";
		setSelectedTreeItem(null);
		root = treeConsultationService.getWholeTree();
		setNoeudTree(new DefaultTreeModel<>(buildTreeNodes(root), true));
	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	protected DefaultTreeNode<EntiteDto> buildTreeNodes(EntiteDto noeud) {

		List<DefaultTreeNode<EntiteDto>> enfants = new ArrayList<>();

		for (EntiteDto enfant : noeud.getEnfants()) {
			enfants.add(buildTreeNodes(enfant));
		}

		return new DefaultTreeNode<>(noeud, enfants);
	}

	protected EntiteDto buildTreeNodes(TreeNode<EntiteDto> noeud) {

		EntiteDto dto = noeud.getData();
		dto.getEnfants().clear();

		for (TreeNode<EntiteDto> enfant : noeud.getChildren()) {
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

	private boolean showHideNodes(TreeNode<EntiteDto> node) {

		if (node == null)
			return false;

		ArrayList<TreeNode<EntiteDto>> childrenToRemove = new ArrayList<>();
		boolean hasAChildMatching = false;

		for (TreeNode<EntiteDto> enfant : node.getChildren()) {
			boolean isFilterPresent = showHideNodes(enfant);
			if (!isFilterPresent)
				childrenToRemove.add(enfant);
			hasAChildMatching = hasAChildMatching || isFilterPresent;
		}

		if (StringUtils.containsIgnoreCase(node.getData().getSigle(), filter)) {
			return true;
		}

		for (TreeNode<EntiteDto> enfant : childrenToRemove) {
			node.remove(enfant);
		}

		return hasAChildMatching;
	}

	@GlobalCommand
	@NotifyChange({ "noeudTree", "selectedTreeItem", "filter" })
	public void updateSelected() {

		filter = "";
		setSelectedTreeItem(null);
		root = treeConsultationService.getWholeTree();
		setNoeudTree(new DefaultTreeModel<>(buildTreeNodes(root), true));
	}

	@Command
	public void onDropCommand(@BindingParam("item") DefaultTreeNode<EntiteDto> item,
			@BindingParam("newParent") DefaultTreeNode<EntiteDto> newParent) {
		item.removeFromParent();
		newParent.add(item);
	}

	@Command
	@NotifyChange({ "selectedTreeItem" })
	public void createNewNodeCommand() {
		EntiteDto n = new EntiteDto();
		n.setSigle("NOUVEAU");
		selectedTreeItem.getData().getEnfants().add(n);
		TreeNode<EntiteDto> newNode = new DefaultTreeNode<>(n, new ArrayList<DefaultTreeNode<EntiteDto>>());
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
	 * This method is called by global command "whatIsTheCurrentTree"
	 * and answers by calling global command "thisIsTheCurrentTree" with the rootNode as parameter
	 */
	@GlobalCommand
	public void whatIsTheCurrentTree() {
		filter = "";
		// #16263 bug suppression
//		showHideNodes();
		Map<String, Object> params = new HashMap<>();
		params.put("currentTree", buildTreeNodes(noeudTree.getRoot()));
		viewModelHelper.postGlobalCommand(null, null, "thisIsTheCurrent²Tree", params);
	}

	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void toggleEditModeGlobalCommand(@BindingParam("editMode") boolean editMode) {
		this.editMode = editMode;
	}

	public TreeModel<TreeNode<EntiteDto>> getNoeudTree() {
		return noeudTree;
	}

	public void setNoeudTree(TreeModel<TreeNode<EntiteDto>> noeudTree) {
		this.noeudTree = noeudTree;
	}

	public TreeNode<EntiteDto> getSelectedTreeItem() {
		return selectedTreeItem;
	}

	public void setSelectedTreeItem(TreeNode<EntiteDto> selectedTreeItem) {
		this.selectedTreeItem = selectedTreeItem;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
