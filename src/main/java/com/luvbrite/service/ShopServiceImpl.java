package com.luvbrite.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.repository.IShopRepository;
import com.luvbrite.repository.IUserRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class ShopServiceImpl implements IShopService {

	@Autowired
	private IShopRepository iShopRepository;
	
	@Autowired
	private IUserRepository iUserRepository;

	@Override
	public int saveShop(CreateShopDTO shopDTO) {
		return iShopRepository.saveShop(shopDTO);
	}

	@Override
	public Map<String, Object> validateData(CreateShopDTO shopDTO) {
		Map<String , Object> map = new HashMap<String, Object>();
		
		try {
			map.put("isValid", true);	
			if(shopDTO.getUserName() == null) {
				map.put("username", "username should not be empty");
				map.put("isValid", false);
			}else {
//				UserDetails  user = iUserRepository.findByUsername(shopDTO.getUserName());
				int count = iUserRepository.countUserByUserName(shopDTO.getUserName());
				log.info("username Count is {}",count);
				if(count>0) {
					map.put("username", "username is already available.please try with different username");
					map.put("isValid", false);
				}
			}
			if(shopDTO.getPassword()==null) {
				map.put("pwdErr", "username should not be empty");
				map.put("isValid", false);
			}
			if(shopDTO.getShopName()==null) {
				map.put("shopName", "username should not be empty");
				map.put("isValid", false);
			}else {
				int count = iShopRepository.countShopsByShopName(shopDTO.getShopName());
				log.info("Shop Count is {}",count);
				if(count>0) {
					map.put("shopName","shop name is already exist. please try with different shop name");
					map.put("isValid", false);
				}
			}
			if(shopDTO.getEmail()!=null){
				int count = iUserRepository.countUserByEmail(shopDTO.getEmail());
				log.info("email Count is {}",count);
				if(count>0) {
					map.put("email","email is already exist. please try with different email");
					map.put("isValid", false);
				}
			}
			return map;
		}catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			map.put("isValid", false);
			map.put("message","Something went Wrong. please try again later.");
			return map;
		}

	}

}
