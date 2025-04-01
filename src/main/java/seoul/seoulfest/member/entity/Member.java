package seoul.seoulfest.member.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.event.entity.EventComment;
import seoul.seoulfest.event.entity.EventFavorite;
import seoul.seoulfest.event.entity.EventLike;
import seoul.seoulfest.member.enums.Role;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String verifyId;

	@Setter
	private String username;

	@Setter
	private String email;

	@Enumerated(value = EnumType.STRING)
	@Setter
	private Role role;

	@Setter
	private String gender;

	@Column(name = "birthday")
	@Setter
	private LocalDate birthDay;

	// 관계 매핑
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventComment> eventComments = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventLike> eventLikes = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventFavorite> eventFavorites = new ArrayList<>();

	@Builder
	public Member(String verifyId, String username, String email, Role role, String gender, LocalDate birthDay) {
		this.verifyId = verifyId;
		this.username = username;
		this.email = email;
		this.role = role;
		this.gender = gender;
		this.birthDay = birthDay;
	}

	public int getAge() {
		return Period.between(this.birthDay, LocalDate.now()).getYears();
	}

	// 연관관계 편의 메서드
	public void addEventComment(EventComment eventComment) {
		this.eventComments.add(eventComment);
	}

	public void removeEventComment(EventComment eventComment) {
		this.eventComments.remove(eventComment);
	}

	public void addEventLike(EventLike eventLike) {
		this.eventLikes.add(eventLike);
	}

	public void removeEventLike(EventLike eventLike) {
		this.eventLikes.remove(eventLike);
	}

	public void addEventFavorite(EventFavorite eventFavorite) {
		this.eventFavorites.add(eventFavorite);
	}

	public void removeEventFavorite(EventFavorite eventFavorite) {
		this.eventFavorites.remove(eventFavorite);
	}
}
