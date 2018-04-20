package org.collectiveone.modules.activity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.collectiveone.modules.activity.dto.NotificationDto;
import org.collectiveone.modules.assignations.Assignation;
import org.collectiveone.modules.initiatives.Initiative;
import org.collectiveone.modules.model.ModelCardWrapper;
import org.collectiveone.modules.model.ModelSection;
import org.collectiveone.modules.model.ModelView;
import org.collectiveone.modules.tokens.InitiativeTransfer;
import org.collectiveone.modules.tokens.TokenMint;
import org.collectiveone.modules.tokens.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class PushMessageBuilder {
	
	@Autowired
	protected Environment env;
	
	public NotificationDto getNotificationDto(Notification notification, Boolean htmlFlag) {
		
		Activity act = notification.getActivity();
		
		Initiative initiative = act.getInitiative();
		Initiative subInitiative = act.getSubInitiative();
		String transferredAssets = (act.getInitiativeTransfers() != null) ? getTransferString(act.getInitiativeTransfers()) : "";
		TokenType tokenType = notification.getActivity().getTokenType();
		TokenMint mint = notification.getActivity().getMint();
		Assignation assignation = notification.getActivity().getAssignation();
		InitiativeTransfer transfer = notification.getActivity().getInitiativeTransfer();
		
		ModelView modelView = notification.getActivity().getModelView();
		ModelSection modelSection = notification.getActivity().getModelSection();
		ModelCardWrapper modelCardWrapper = notification.getActivity().getModelCardWrapper();
		
		ModelSection onSection = notification.getActivity().getOnSection();
		ModelView onView = notification.getActivity().getOnView();
		
		ModelSection fromSection = notification.getActivity().getFromSection();
		ModelView fromView = notification.getActivity().getFromView();

		
		String message = "";
		String url = "";
		
		switch (notification.getActivity().getType()) {
			
		case INITIATIVE_CREATED:
			message = getPTag(htmlFlag) + "created the " + getInitiativeAnchor(initiative, htmlFlag) + " initiative and added you as a member." + getPTagEnd(htmlFlag);
			
			url = getInitiativeAnchor(initiative, htmlFlag);
			
			/* you can add this after the switch instead o creating multiple repeated calls */
			break;

		default:
			message = "You have received a notification";
			url = "http://www.collectiveone.org";
			break;
		
		}
		
		
		NotificationDto notificationDto = new NotificationDto();
		
		notificationDto.setSubscriber(notification.getSubscriber().getUser().toDtoLight());
		notificationDto.setTriggerUser(act.getTriggerUser().toDtoLight());
		notificationDto.setState(notification.getState().toString());
		notificationDto.setEmailState(notification.getEmailState().toString());
		notificationDto.setPushState(notification.getPushState().toString());
		
		notificationDto.setMesssage(message);
		notificationDto.setUrl(url);
		notificationDto.setIsHtml(htmlFlag);
		
		return notificationDto;
	}

	private String getPTag(Boolean flag) {
	  return flag ? "<p>" : "";
	}
	
	private String getPTagEnd(Boolean flag) {
		  return flag ? "</p>" : "";
		}
	
	private String getTriggeredUser(Notification notification) {
		return notification.getActivity().getTriggerUser().getProfile().getNickname();
	}
	
	private String getInitiativeAnchor(Initiative initiative, Boolean htmlFlag) {
		String string = "";
		
		if (htmlFlag) {
			string += "<a href=" + env.getProperty("collectiveone.webapp.baseurl") +"/#/app/inits/" + 
					initiative.getId().toString() + "/overview>";	
		}
		
		string += initiative.getMeta().getName();
		
		if (htmlFlag) {
			string += "</a>";
		}
		
		return string;
	}
	
	private String getAssignationAnchor(Assignation assignation) {
		return "transfer";
	}
	
	private String getTransferString(List<InitiativeTransfer> transfers) {
		if (transfers.size() > 0) {
			return NumberFormat.getNumberInstance(Locale.US).format(transfers.get(0).getValue()) + " " + transfers.get(0).getTokenType().getName();
		} else {
			return "";
		}
	}
	
	private String getModelViewAnchor(ModelView view) {
		return view.getTitle();
	}
	
	private String getModelSectionAnchor(ModelSection section) {
		return section.getTitle();
	}
	
	private String getModelCardWrapperAnchor(ModelCardWrapper cardWrapper, ModelSection onSection) {
		return cardWrapper.getCard().getTitle();
	}
	
	private String getModelCardWrapperAnchor(ModelCardWrapper cardWrapper) {
		return cardWrapper.getCard().getTitle();
	}
//	
//	private String getUnsuscribeFromInitiativeHref(Initiative initiative) {
//		return env.getProperty("collectiveone.webapp.baseurl") +"/#/app/inits/unsubscribe?fromInitiativeId=" + 
//				initiative.getId().toString() + "&fromInitiativeName=" + initiative.getMeta().getName();
//	}
//	
//	private String getUnsuscribeFromAllHref() {
//		return env.getProperty("collectiveone.webapp.baseurl") +"/#/app/inits/unsubscribe?fromAll=true";
//	}
//	
}
