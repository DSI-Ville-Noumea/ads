package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TreeViewModel {

	private Logger logger = LoggerFactory.getLogger(TreeViewModel.class);

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ICreateTreeService createTreeService;
	
	private RevisionDto selectedRevision;
	
	public RevisionDto getSelectedRevision() {
		return selectedRevision;
	}

	public void setSelectedRevision(RevisionDto selectedRevision) {
		this.selectedRevision = selectedRevision;
	}


	private TreeModel<TreeNode<NoeudDto>> noeudTree;

	public TreeModel<TreeNode<NoeudDto>> getNoeudTree() {
		return noeudTree;
	}

	public void setNoeudTree(TreeModel<TreeNode<NoeudDto>> noeudTree) {
		this.noeudTree = noeudTree;
	}

	
	private NoeudDto selectedNoeud;

	public NoeudDto getSelectedNoeud() {
		return selectedNoeud;
	}

	public void setSelectedNoeud(NoeudDto selectedNoeud) {
		this.selectedNoeud = selectedNoeud;
	}

	
	private TreeNode<NoeudDto> selectedTreeItem;

	public TreeNode<NoeudDto> getSelectedTreeItem() {
		return selectedTreeItem;
	}

	@NotifyChange({ "selectedNoeud" })
	public void setSelectedTreeItem(TreeNode<NoeudDto> selectedTreeItem) {
		this.selectedTreeItem = selectedTreeItem;
		setSelectedNoeud(selectedTreeItem.getData());
	}

	public TreeViewModel() {

		treeConsultationService = (ITreeConsultationService) SpringUtil
				.getBean("treeConsultationService");
		
		createTreeService = (ICreateTreeService) SpringUtil
				.getBean("createTreeService");
		
		// Set a default non null node to prevent ZK from bugging with next new value
		setNoeudTree(new DefaultTreeModel<NoeudDto>(new DefaultTreeNode<NoeudDto>(new NoeudDto())));
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

	@Command
	public void onDropCommand(
			@BindingParam("item") DefaultTreeNode<NoeudDto> item,
			@BindingParam("newParent") DefaultTreeNode<NoeudDto> newParent) {
		item.removeFromParent();
		newParent.add(item);
	}

	@GlobalCommand
	@NotifyChange({ "noeudTree" })
	public void updateSelectedRevision(
			@BindingParam("revision") RevisionDto revision) {

		setSelectedRevision(revision);
		NoeudDto root = treeConsultationService
				.getTreeOfSpecificRevision(revision.getIdRevision());
		setNoeudTree(new DefaultTreeModel<NoeudDto>(buildTreeNodes(root), true));
	}
	
	@Command
	public void createNewRevisionCommand() {
		createTreeService.createTreeFromRevisionAndNoeuds(selectedRevision, buildTreeNodes(noeudTree.getRoot()));
		BindUtils.postGlobalCommand(null, null, "revisionListChanged", null);
	}
}
