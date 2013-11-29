package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
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

	public TreeViewModel() {

		treeConsultationService = (ITreeConsultationService) SpringUtil
				.getBean("treeConsultationService");
		
		NoeudDto root = treeConsultationService.getTreeOfLatestRevisionTree();
		
		noeudTree = new DefaultTreeModel<NoeudDto>(buildTreeNodes(root), true);
	}
	
	protected DefaultTreeNode<NoeudDto> buildTreeNodes(NoeudDto noeud) {
		
		List<DefaultTreeNode<NoeudDto>> enfants = new ArrayList<DefaultTreeNode<NoeudDto>>();
		
		for(NoeudDto enfant : noeud.getEnfants()) {
			enfants.add(buildTreeNodes(enfant));
		}
		
		return new DefaultTreeNode<NoeudDto>(noeud, enfants);
	}
	
	@Command
	@NotifyChange({ "selectedNoeud" })
	public void selectTreeItem(@BindingParam("item") NoeudDto item ) {
		selectedNoeud = item;
	}
	
	@Command
	public void onDropCommand(
			@BindingParam("item") DefaultTreeNode<NoeudDto> item, 
			@BindingParam("newParent") DefaultTreeNode<NoeudDto> newParent) {
		item.removeFromParent();
		newParent.add(item);
	}
}
