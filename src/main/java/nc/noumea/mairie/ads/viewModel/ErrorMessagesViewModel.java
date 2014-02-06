package nc.noumea.mairie.ads.viewModel;

import java.util.List;

import nc.noumea.mairie.ads.dto.ErrorMessageDto;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class ErrorMessagesViewModel {

	public List<ErrorMessageDto> messages;

	public List<ErrorMessageDto> getMessages() {
		return messages;
	}

	public void setMessages(List<ErrorMessageDto> messages) {
		this.messages = messages;
	}

	public boolean isErrorMessages(){
		return messages != null && messages.size() != 0;
	}

	@GlobalCommand
	@NotifyChange({ "messages", "errorMessages" })
	public void setErrorMessagesGlobalCommand(@BindingParam("messages") List<ErrorMessageDto> messages) {
		this.messages = messages;
	}
}
