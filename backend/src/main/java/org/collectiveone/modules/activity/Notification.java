package org.collectiveone.modules.activity;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.collectiveone.modules.activity.enums.NotificationEmailState;
import org.collectiveone.modules.activity.enums.NotificationPushState;
import org.collectiveone.modules.activity.enums.NotificationState;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table( name = "notifications" )
public class Notification {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator",
		parameters = { @Parameter( name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@ManyToOne
	private Activity activity;
	
	@ManyToOne
	private Subscriber subscriber;
	
	@Column(name = "creation_date")
	private Timestamp creationDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	private NotificationState state;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "email_state")
	private NotificationEmailState emailState;

	@Enumerated(EnumType.STRING)
	@Column(name = "push_state")
	private NotificationPushState pushState;
	
	/* notification conversion toDto is complex and done in PushMessageBuild */
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public NotificationState getState() {
		return state;
	}

	public void setState(NotificationState state) {
		this.state = state;
	}

	public NotificationEmailState getEmailState() {
		return emailState;
	}

	public void setEmailState(NotificationEmailState emailState) {
		this.emailState = emailState;
	}

	public NotificationPushState getPushState() {
		return pushState;
	}

	public void setPushState(NotificationPushState pushState) {
		this.pushState = pushState;
	}
	
}
