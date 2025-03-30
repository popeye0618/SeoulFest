package seoul.seoulfest.auth.oauth2.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
	KAKAO("kakao");

	private final String registrationId;
}
