package com.luvbrite.controller;

import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.ProductsExt;
import com.luvbrite.service.IProductService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("api/product")
public class ProductsController {

	@Autowired
	IProductService iProductSerivce;

	@GetMapping("/allproducts")
	public ResponseEntity<CommonResponse> getAllProducts(Authentication authentication) {
		CommonResponse commonResponse = new CommonResponse();

		List<ProductsExt> productList = null;

		try {
			productList =  iProductSerivce.listAllProducts();
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

	@GetMapping("/getAllProductNames")
	public ResponseEntity<CommonResponse> getAllProductNames(){
		CommonResponse commonResponse = new CommonResponse();

//		List<ProductsExt> productList = null;
		List<String[]> productNames = iProductSerivce.getAllProdNames();
		try {
			if (productNames != null) {
				commonResponse.setCode(200);
				commonResponse.setData(productNames);
				commonResponse.setMessage("products fetched successfully");
				commonResponse.setStatus("SUCCESS");

				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			} else {
				commonResponse.setCode(422);
				commonResponse.setStatus("Unprocessable");
				commonResponse.setMessage("Not able to fetch product names.");
				return new ResponseEntity<>(commonResponse, HttpStatus.OK);
			}
		} catch (Exception e) {
			commonResponse.setCode(500);
//			commonResponse.setData(productList);
			commonResponse.setMessage("Something went wrong.please try again later");
			commonResponse.setStatus("FAILED");
			log.error("Message is {} and exception is {}",e.getMessage(),e);

			return new ResponseEntity<>(commonResponse, HttpStatus.OK);
		}
	}
}
