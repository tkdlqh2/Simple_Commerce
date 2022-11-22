package com.zerobase.cms.user.service.seller;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public Seller signUp(SignUpForm form){
        Seller seller = Seller.from(form);
        seller.setPassword(passwordEncoder.encode(form.getPassword()));
        sellerRepository.save(seller);
        return seller;
    }

    public boolean isEmailExist(String email){
        return sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).isPresent();
    }

    @Transactional
    public void verifyEmail(String email,String code){
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.UNREGISTERED_USER));

        if(seller.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        if(!seller.getVerificationCode().equals(code)){
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }

        if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }
        seller.setVerify(true);
    }

    @Transactional
    public void changeSellerValidateEmail(Long customerId,String verificationCode){
        Seller seller = sellerRepository.findById(customerId).orElseThrow(
                ()-> new CustomException(ErrorCode.UNREGISTERED_USER)
        );

        seller.setVerificationCode(verificationCode);
        seller.setVerifyExpiredAt(LocalDateTime.now());
    }
}
