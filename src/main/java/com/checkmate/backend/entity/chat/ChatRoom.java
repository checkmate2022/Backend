package com.checkmate.backend.entity.chat;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

	private static final long serialVersionUID = 6494678977089006639L;
	@Id
	@Column(name = "chatroom_id")
	private String id;
	private String name;

	private String username1;

	private String userImage1;

	private String userImage2;

	private String username2;

	public ChatRoom(String name, String username1, String userImage1, String username2, String userImage2) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.username1 = username1;
		this.userImage1 = userImage1;
		this.username2 = username2;
		this.userImage2 = userImage2;
	}

}
