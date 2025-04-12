package seoul.seoulfest.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.auth.dto.LoginDto;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.dto.request.InputFeatureReq;
import seoul.seoulfest.member.dto.request.UpdateFeatureReq;
import seoul.seoulfest.member.dto.response.InputFeatureRes;
import seoul.seoulfest.member.dto.response.MemberInfoRes;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.enums.Role;
import seoul.seoulfest.member.repository.MemberRepository;
import seoul.seoulfest.util.jwt.JwtTokenProvider;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

	private final MemberRepository memberRepository;
	private final SecurityUtil securityUtil;
	private final JwtTokenProvider jwtTokenProvider;


	/**
	 * 유저 피처 입력
	 *
	 * @param userDetails 로그인된 유저
	 * @param request     유저 정보
	 * @return AT, RT
	 */
	@Override
	@Transactional
	public InputFeatureRes inputFeature(CustomUserDetails userDetails, InputFeatureReq request) {

		validEmail(request.getEmail());

		Member currentMember = securityUtil.getCurrentMember();

		validRoleSemi(currentMember);

		inputUserInfo(currentMember, request);
		currentMember.setRole(Role.ROLE_USER);

		jwtTokenProvider.deleteRefreshToken(currentMember.getVerifyId());

		LoginDto updatedLoginDto = LoginDto.builder()
			.verifyId(currentMember.getVerifyId())
			.role(currentMember.getRole().name())
			.email(currentMember.getEmail())
			.build();

		CustomUserDetails updatedUserDetails = CustomUserDetails.create(updatedLoginDto);

		String accessToken = jwtTokenProvider.generateAccessToken(updatedUserDetails);
		String refreshToken = jwtTokenProvider.generateRefreshToken(updatedUserDetails);

		return InputFeatureRes.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	private void validRoleSemi(Member member) {
		if (!member.getRole().equals(Role.ROLE_SEMI_USER)) {
			throw new BusinessException(AuthErrorCode.INVALID_ROLE);
		}
	}

	@Override
	public void validEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new BusinessException(AuthErrorCode.EMAIL_DUPLICATED);
		}
	}

	private void inputUserInfo(Member member, InputFeatureReq request) {
		member.setUsername(request.getUsername());
		member.setGender(request.getGender());
		member.setBirthDay(request.getBirthday());
		member.setEmail(request.getEmail());
	}

	/**
	 * 유저 정보 업데이트
	 * @param request 새 유저 정보
	 */
	@Override
	@Transactional
	public void updateFeature(UpdateFeatureReq request) {
		Member currentMember = securityUtil.getCurrentMember();

		updateUserInfo(currentMember, request);
	}

	private void updateUserInfo(Member member, UpdateFeatureReq request) {
		member.setUsername(request.getUsername());
		member.setGender(request.getGender());
		member.setBirthDay(request.getBirthday());
		member.setEmail(request.getEmail());
	}

	@Override
	public MemberInfoRes getMemberInfo() {

		Member member = securityUtil.getCurrentMember();

		return MemberInfoRes.builder()
			.verifyId(member.getVerifyId())
			.username(member.getUsername())
			.gender(member.getGender())
			.email(member.getEmail())
			.build();
	}
}
