package com.zerobase.cms.user.service.seller;

import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    SellerRepository sellerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SellerService sellerService;

    @Test
    void findByIdAndEmailSuccess() {
        //given
        Seller seller = Seller.builder()
                .id(1L)
                .email("abc@gmail.com").build();

        given(sellerRepository.findById(1L))
                .willReturn(Optional.of(seller));
        //when
        var result = sellerService.findByIdAndEmail(1L,"abc@gmail.com");
        //then
        assertEquals(seller, result.get());
    }

    @Test
    void findValidCustomerTest() {
        //given
        Seller seller = Seller.builder()
                .email("abc@gmail.com")
                .password("1234")
                .verify(true)
                .build();

        given(sellerRepository.findByEmail("abc@gmail.com"))
                .willReturn(Optional.of(seller));
        given(passwordEncoder.matches("1111","1234")).willReturn(true);
        //when
        var result = sellerService.findValidSeller("abc@gmail.com","1111");
        //then
        assertEquals(seller,result.get());
    }

}