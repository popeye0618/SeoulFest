package seoul.seoulfest.event.dto.event.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.event.entity.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventSyncDto {
	private Long id;
	private String status;
	private String category;
	private String guName;
	private String title;
	private LocalDateTime eventDateTime;
	private String place;
	private String orgName;
	private String useTarget;
	private String useFee;
	private String player;
	private String introduce;
	private String etcDesc;
	private String orgLink;
	private String mainImg;
	private LocalDate registerDate;
	private String ticket;
	private LocalDate startDate;
	private LocalDate endDate;
	private String themeCode;
	private String lot;
	private String lat;
	private String isFree;
	private String portal;
	private int likes;
	private int favorites;
	private int comments;

	@Builder
	public EventSyncDto(Long id, String status, String category, String guName, String title,
		LocalDateTime eventDateTime, String place, String orgName, String useTarget,
		String useFee, String player, String introduce, String etcDesc, String orgLink,
		String mainImg, LocalDate registerDate, String ticket, LocalDate startDate,
		LocalDate endDate, String themeCode, String lot, String lat, String isFree,
		String portal, int likes, int favorites, int comments) {
		this.id = id;
		this.status = status;
		this.category = category;
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
		this.likes = likes;
		this.favorites = favorites;
		this.comments = comments;
	}

	// Entity를 DTO로 변환하는 정적 메서드
	public static EventSyncDto fromEntity(Event event) {
		return EventSyncDto.builder()
			.id(event.getId())
			.status(event.getStatus() != null ? event.getStatus().name() : null)
			.category(event.getCodename())
			.guName(event.getGuName())
			.title(event.getTitle())
			.eventDateTime(event.getEventDateTime())
			.place(event.getPlace())
			.orgName(event.getOrgName())
			.useTarget(event.getUseTarget())
			.useFee(event.getUseFee())
			.player(event.getPlayer())
			.introduce(event.getIntroduce())
			.etcDesc(event.getEtcDesc())
			.orgLink(event.getOrgLink())
			.mainImg(event.getMainImg())
			.registerDate(event.getRegisterDate())
			.ticket(event.getTicket())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.themeCode(event.getThemeCode())
			.lot(event.getLot())
			.lat(event.getLat())
			.isFree(event.getIsFree())
			.portal(event.getPortal())
			.likes(event.getLikes())
			.favorites(event.getFavorites())
			.comments(event.getComments())
			.build();
	}
}