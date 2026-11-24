package com.mangmuganae.eolmago.global.security.core;

import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.exception.CustomUserNotFoundException;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * 사용자 인증 객체를 생성하는 메서드입니다.
	 *
	 * @return CustomUserDetails 객체
	 * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Override
	public UserDetails loadUserByUsername(String socialId) throws CustomUserNotFoundException {
		User user = userRepository.findByProviderId(socialId).orElseThrow(
			() -> new CustomUserNotFoundException(ErrorCode.USER_NOT_FOUND));

		return new CustomUserDetails(user);
	}

	/**
	 * 사용자 ID로 사용자 인증 객체를 생성하는 메서드입니다.
	 *
	 * @param socialId 사용자 소셜로그인 식별자인 socialId 값이 들어갑니다.
	 * @return CustomUserDetails 객체
	 * @throws CustomUserNotFoundException 사용자를 찾을 수 없는 경우
	 */
	public UserDetails loadUserById(String socialId) throws CustomUserNotFoundException {
		User user = userRepository.findByProviderId(socialId).orElseThrow(
				() -> new CustomUserNotFoundException(ErrorCode.USER_NOT_FOUND));
		return new CustomUserDetails(user);
	}

}