package org.collectiveone.modules.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.collectiveone.modules.conversations.MessageThread;
import org.collectiveone.modules.governance.CardLike;
import org.collectiveone.modules.model.dto.ModelCardWrapperDto;
import org.collectiveone.modules.users.AppUser;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "model_cards_wrapper")
public class ModelCardWrapper {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator",
		parameters = { @Parameter( name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@ManyToOne
	private ModelSection rootSection;
	
	@OneToOne(cascade = CascadeType.ALL)
	private ModelCard card;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ModelCard> oldVersions = new ArrayList<ModelCard>();
	
	@OneToOne
	private MessageThread messageThread;	
	
	@OneToMany(mappedBy="cardWrapper")
	private List<CardLike> likes;
	
	private Timestamp creationDate;
	
	@ManyToOne
	private AppUser creator;
	
	private Timestamp lastEdited;
	
	@ManyToMany
	private List<AppUser> editors = new ArrayList<AppUser>();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		ModelCardWrapper other = (ModelCardWrapper) obj;
		return id.equals(other.getId());
	}
	
	public ModelCardWrapperDto toDto() {
		ModelCardWrapperDto cardWrapperDto = new ModelCardWrapperDto();
		
		cardWrapperDto.setId(id.toString());
		cardWrapperDto.setCard(card.toDto());
		if (creator != null) cardWrapperDto.setCreator(creator.toDtoLight());
		if (creationDate != null) cardWrapperDto.setCreationDate(creationDate.getTime());
		if (lastEdited != null) cardWrapperDto.setLastEdited(lastEdited.getTime());
		if (editors != null) {
			for (AppUser editor : editors) {
				cardWrapperDto.getEditors().add(editor.toDtoLight());
			}
		}
		
		return cardWrapperDto;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public ModelSection getRootSection() {
		return rootSection;
	}

	public void setInitiative(ModelSection rootSection) {
		this.rootSection = rootSection;
	}
	
	public ModelCard getCard() {
		return card;
	}

	public void setCard(ModelCard card) {
		this.card = card;
	}

	public List<ModelCard> getOldVersions() {
		return oldVersions;
	}

	public void setOldVersions(List<ModelCard> oldVersions) {
		this.oldVersions = oldVersions;
	}
	
	public MessageThread getMessageThread() {
		return messageThread;
	}

	public void setMessageThread(MessageThread messageThread) {
		this.messageThread = messageThread;
	}

	public List<CardLike> getLikes() {
		return likes;
	}

	public void setLikes(List<CardLike> likes) {
		this.likes = likes;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
	
	public AppUser getCreator() {
		return creator;
	}

	public void setCreator(AppUser creator) {
		this.creator = creator;
	}

	public Timestamp getLastEdited() {
		return lastEdited;
	}

	public void setLastEdited(Timestamp lastEdited) {
		this.lastEdited = lastEdited;
	}

	public List<AppUser> getEditors() {
		return editors;
	}

	public void setEditors(List<AppUser> editors) {
		this.editors = editors;
	}
	
}
