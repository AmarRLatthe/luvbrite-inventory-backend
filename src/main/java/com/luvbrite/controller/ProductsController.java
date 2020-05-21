package com.luvbrite.controller;

import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.ProductsExt;
import com.luvbrite.service.ProductService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class ProductsController {

	@Autowired
	ProductService productSerivce;

	@GetMapping("/allproducts")
	public ResponseEntity<CommonResponse> getAllProducts(Authentication authentication) {
		CommonResponse commonResponse = new CommonResponse();

		List<ProductsExt> productList = null;

		try {
			productList =  productSerivce.listAllProducts();
			if (productList != null) {
				commonResponse.setCode(200);
				commonResponse.setData(productList);
				commonResponse.setMessage("products fetched successfully");
				commonResponse.setStatus("SUCCESS");

				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			} else {
				commonResponse.setCode(200);
				commonResponse.setData(productList);
				commonResponse.setMessage("products list is null");
				commonResponse.setStatus("FAILED");

				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			}
		} catch (Exception e) {
			commonResponse.setCode(500);
			commonResponse.setData(productList);
			commonResponse.setMessage("products list is null");
			commonResponse.setStatus("FAILED");
			log.error("Exception occured while fetching product list",e);

			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		}
	}

}
