package seoul.seoulfest.member.service;

import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.member.dto.request.InputFeatureReq;
import seoul.seoulfest.member.dto.request.UpdateFeatureReq;
import seoul.seoulfest.member.dto.response.InputFeatureRes;
import seoul.seoulfest.member.dto.response.MemberInfoRes;

public interface MemberService {

	/**
	 * 유저 피처 입력
	 *
	 * @param userDetails 로그인된 유저
	 * @param request     유저 정보
	 * @return AT, RT
	 */
	InputFeatureRes inputFeature(CustomUserDetails userDetails, InputFeatureReq request);

	/**
	 * 유저 정보 업데이트
	 * @param request 새 유저 정보
	 */
	void updateFeature(UpdateFeatureReq request);

	MemberInfoRes getMemberInfo();

	void validEmail(String email);
}
