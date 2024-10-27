package com.vn.sbit.idenfity_service;

import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes = IdenfityServiceApplicationTests.class)
class IdenfityServiceApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(IdenfityServiceApplicationTests.class);

	@Test
	void hash() throws NoSuchAlgorithmException {
		String passWord="123456";
		MessageDigest md = MessageDigest.getInstance("MD5");// md5 algorithm
		md.update(passWord.getBytes());

		byte[] digest=md.digest();
		String md5Hash= DatatypeConverter.printHexBinary(digest);
		System.out.println("MD5 :"+md5Hash);//unchanged after each time


		log.info(md5Hash);


		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(5);
        //+salt = $phienbanBcypt$costfactir(2^5)+salt+hash(kết quả băm cuối cùng từ pass+salt)
		System.out.println("Bcrypt :");
		log.info(passwordEncoder.encode(passWord));//change after each time
		log.info(passwordEncoder.encode(passWord));//change after each time










	}

}
