package com.checkmate.backend.entity.schedule;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NOTIFICATION")
public class Notification {

	@Column(name = "ISNOTICE")
	boolean isNotice;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "NOTIFICATION_SEQ")
	private Long notificationSeq;
	@Column(name = "BODY")
	private String body;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "NOTIFICATION_DATE")
	private LocalDateTime notificationDate;
	@Column(name = "NOTIFICATION_USERID")
	private String userId;

	public Notification(String title, String body, String userId, LocalDateTime notificationDate, boolean isNotice) {
		this.title = title;
		this.body = body;
		this.userId = userId;
		this.notificationDate = notificationDate;
		this.isNotice = isNotice;
	}

	@Override
	public String toString() {
		return "Notification{" +
			"notificationSeq=" + notificationSeq +
			", body='" + body + '\'' +
			", title='" + title + '\'' +
			", notificationDate=" + notificationDate +
			", isNotice=" + isNotice +
			'}';
	}
}
