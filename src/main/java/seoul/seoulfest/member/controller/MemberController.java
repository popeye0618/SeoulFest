package seoul.seoulfest.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.member.dto.request.InputFeatureReq;
import seoul.seoulfest.member.dto.request.UpdateFeatureReq;
import seoul.seoulfest.member.dto.response.InputFeatureRes;
import seoul.seoulfest.member.dto.response.MemberInfoRes;
import seoul.seoulfest.member.service.MemberService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;

	/**
	 * 유저 피처 입력
	 *
	 * @param userDetails 로그인된 유저
	 * @param request     유저 정보
	 * @return AT, RT
	 */
	@PostMapping("/auth/semi/feature")
	public ResponseEntity<Response<InputFeatureRes>> inputFeature(
		@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody InputFeatureReq request) {
		InputFeatureRes response = memberService.inputFeature(userDetails, request);

		return Response.ok(response).toResponseEntity();
	}

	/**
	 * 유저 정보 업데이트
	 *
	 * @param request 새 유저 정보
	 */
	@PatchMapping("/auth/user/feature")
	public ResponseEntity<Response<Void>> updateFeature(@RequestBody UpdateFeatureReq request) {
		memberService.updateFeature(request);

		return Response.ok().toResponseEntity();
	}

	@GetMapping("/auth/user/info")
	public ResponseEntity<Response<MemberInfoRes>> getInfo() {
		MemberInfoRes memberInfoRes = memberService.getMemberInfo();

		return Response.ok(memberInfoRes).toResponseEntity();
	}

	@GetMapping("/auth/all-user/email/{email}")
	public ResponseEntity<Response<Void>> checkEmailDup(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String email) {
		memberService.validEmail(userDetails, email);

		return Response.ok().toResponseEntity();
	}

}
