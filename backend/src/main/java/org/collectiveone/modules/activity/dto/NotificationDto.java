package org.collectiveone.modules.activity.dto;

import org.collectiveone.modules.users.AppUserDto;

public class NotificationDto {

	private String id;
	private AppUserDto subscriber;
	private AppUserDto triggerUser;
	private Long creationDate;
	private String state;
	private String emailState;
	private String pushState;
	private String messsage;
	private String url;
	private Boolean isHtml;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AppUserDto getTriggerUser() {
		return triggerUser;
	}
	public void setTriggerUser(AppUserDto triggerUser) {
		this.triggerUser = triggerUser;
	}
	public Long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEmailState() {
		return emailState;
	}
	public void setEmailState(String emailState) {
		this.emailState = emailState;
	}
	public String getPushState() {
		return pushState;
	}
	public void setPushState(String pushState) {
		this.pushState = pushState;
	}
	public String getMesssage() {
		return messsage;
	}
	public void setMesssage(String messsage) {
		this.messsage = messsage;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getIsHtml() {
		return isHtml;
	}
	public void setIsHtml(Boolean isHtml) {
		this.isHtml = isHtml;
	}
	public AppUserDto getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(AppUserDto subscriber) {
		this.subscriber = subscriber;
	}
	
}
