package com.zerobase.cms.user.service.seller;

import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Seller> findByIdAndEmail(Long id, String email){
        return sellerRepository.findById(id).stream().filter(
                seller -> seller.getEmail().equals(email)
        ).findFirst();
    }
    public Optional<Seller> findValidSeller(String email,String password){
        return sellerRepository.findByEmail(email).stream().filter(
                customer -> passwordEncoder.matches(password, customer.getPassword()) && customer.isVerify()
        ).findFirst();
    }
}
