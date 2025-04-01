package seoul.seoulfest.event.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	@Enumerated(value = EnumType.STRING)
	@Setter
	private Status status;

	@Column(name = "codename")
	private String codename;

	@Column(name = "guname")
	private String guName;

	private String title;

	@Column(name = "event_datetime")
	private LocalDateTime eventDateTime;

	private String place;

	@Column(name = "org_name")
	private String orgName;

	@Column(name = "use_target")
	private String useTarget;

	@Column(name = "use_fee")
	private String useFee;

	private String player;

	private String introduce;

	@Column(name = "etc_desc")
	private String etcDesc;

	@Column(name = "org_link")
	private String orgLink;

	@Column(name = "main_img")
	private String mainImg;

	@Column(name = "register_date")
	private LocalDate registerDate;

	private String ticket;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "themecode")
	private String themeCode;

	// 위도
	private String lot;

	// 경도
	private String lat;

	@Column(name = "is_free")
	private String isFree;

	private String portal;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventComment> eventComments = new ArrayList<>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventLike> eventLikes = new ArrayList<>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventFavorite> eventFavorites = new ArrayList<>();

	@Builder
	public Event(Status status, String codename, String guName, String title, LocalDateTime eventDateTime, String place,
		String orgName, String useTarget, String useFee, String player, String introduce, String etcDesc,
		String orgLink,
		String mainImg, LocalDate registerDate, String ticket, LocalDate startDate, LocalDate endDate,
		String themeCode, String lot, String lat, String isFree, String portal) {
		this.status = status;
		this.codename = codename;
		this.guName = guName;
		this.title = title;
		this.eventDateTime = eventDateTime;
		this.place = place;
		this.orgName = orgName;
		this.useTarget = useTarget;
		this.useFee = useFee;
		this.player = player;
		this.introduce = introduce;
		this.etcDesc = etcDesc;
		this.orgLink = orgLink;
		this.mainImg = mainImg;
		this.registerDate = registerDate;
		this.ticket = ticket;
		this.startDate = startDate;
		this.endDate = endDate;
		this.themeCode = themeCode;
		this.lot = lot;
		this.lat = lat;
		this.isFree = isFree;
		this.portal = portal;
	}

	public int getLikes() {
		return eventLikes.size();
	}
	public int getFavorites() {
		return eventFavorites.size();
	}
	public int getComments() {
		return eventComments.size();
	}

	// 연관관계 편의 메서드: 댓글을 이벤트에 추가하고, 댓글의 event 필드도 설정
	public void addEventComment(EventComment comment) {
		this.eventComments.add(comment);
		comment.assignEvent(this);  // 아래 assignEvent() 메서드 사용
	}
}
