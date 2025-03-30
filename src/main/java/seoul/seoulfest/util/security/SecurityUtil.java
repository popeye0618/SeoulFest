package seoul.seoulfest.util.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final MemberRepository memberRepository;

	public Member getCurrentMember() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			String verifyId = userDetails.getName();

			return memberRepository.findByVerifyId(verifyId)
				.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));
		}

		throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
	}

}
