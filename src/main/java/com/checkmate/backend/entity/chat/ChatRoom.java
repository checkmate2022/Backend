package com.checkmate.backend.entity.chat;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.checkmate.backend.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom implements Serializable {

	@Id
	@Column(name = "chatroom_id")
	private String id;

	private static final long serialVersionUID = 6494678977089006639L;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User sender;


	public ChatRoom(String name,User receiver,User sender) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.receiver=receiver;
		this.sender=sender;
	}


}
