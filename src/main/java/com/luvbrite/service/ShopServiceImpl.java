package com.luvbrite.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.CreateShopDTO;
import com.luvbrite.model.ShopDTO;
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
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map.put("isValid", true);
			if (StringUtils.isBlank(shopDTO.getUserName())) {
				map.put("username", "username should not be empty");
				map.put("isValid", false);
			} else {
//				UserDetails  user = iUserRepository.findByUsername(shopDTO.getUserName());
				int count = iUserRepository.countUserByUserName(shopDTO.getUserName());
				log.info("username Count is {}", count);
				if (count > 0) {
					map.put("username", "username is already available.please try with different username");
					map.put("isValid", false);
				}
			}
			if (StringUtils.isBlank(shopDTO.getPassword())) {
				map.put("pwdErr", "username should not be empty");
				map.put("isValid", false);
			}
			if (StringUtils.isBlank(shopDTO.getShopName())) {
				map.put("shopName", "shop name should not be empty");
				map.put("isValid", false);
			} else {
				int count = iShopRepository.countShopsByShopName(shopDTO.getShopName());
				log.info("Shop Count is {}", count);
				if (count > 0) {
					map.put("shopName", "shop name is already exist. please try with different shop name");
					map.put("isValid", false);
				}
			}
			if (StringUtils.isBlank(shopDTO.getDomain())) {
				map.put("domain", "domain name should not be empty");
				map.put("isValid", false);
			} else {
				int count = iShopRepository.countShopsByDomain(shopDTO.getDomain());
				log.info("Shop Count is {}", count);
				if (count > 0) {
					map.put("domain", "domain name is already exist. please try with different domain name");
					map.put("isValid", false);
				}
			}

			if (!StringUtils.isBlank(shopDTO.getEmail())) {
				int count = iUserRepository.countUserByEmail(shopDTO.getEmail());
				log.info("email Count is {}", count);
				if (count > 0) {
					map.put("email", "email is already exist. please try with different email");
					map.put("isValid", false);
				}
			}
			return map;
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			map.put("isValid", false);
			map.put("message", "Something went Wrong. please try again later.");
			return map;
		}

	}

	@Override
	public List<ShopDTO> getAllShops() {
		try {
			return iShopRepository.getAllShops();
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	@Override
	public Map<String, Object> isValidateForUpdate(Integer id, ShopDTO shop) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isValid", true);
		try {
			if (StringUtils.isBlank(shop.getShopOwnerUsername())) {
				map.put("username", "username should not be empty");
				map.put("isValid", false);
			} else {
//			UserDetails  user = iUserRepository.findByUsername(shopDTO.getUserName());
				int count = iUserRepository.countUserByUserNameNId(shop.getShopOwnerUsername(), id);
				log.info("username Count is {}", count);
				if (count > 0) {
					map.put("username", "username is already available.please try with different username");
					map.put("isValid", false);
				}
			}

			if (StringUtils.isBlank(shop.getShopName())) {
				map.put("shopName", "shop name should not be empty");
				map.put("isValid", false);
			} else {
				int count = iShopRepository.countShopsByShopNameNId(id, shop.getShopName());
				log.info("Shop Count is {}", count);
				if (count > 0) {
					map.put("shopName", "shop name is already exist. please try with different shop name");
					map.put("isValid", false);
				}
			}
			if (StringUtils.isBlank(shop.getDomain())) {
				map.put("domain", "domain name should not be empty");
				map.put("isValid", false);
			} else {
				int count = iShopRepository.countShopsByDomainNId(id, shop.getDomain());
				log.info("Shop Count is {}", count);
				if (count > 0) {
					map.put("domain", "domain name is already exist. please try with different domain name");
					map.put("isValid", false);
				}
			}

			if (!StringUtils.isBlank(shop.getEmail())) {
				int count = iUserRepository.countUserByEmailNId(id, shop.getEmail());
				log.info("email Count is {}", count);
				if (count > 0) {
					map.put("email", "email is already exist. please try with different email");
					map.put("isValid", false);
				}
			}
			return map;
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}", e.getMessage(), e);
			map.put("isValid", false);
			map.put("message", "Something went Wrong. please try again later.");
			return map;
		}
	}

	@Override
	public int updateShopById(Integer id, ShopDTO shop) {
		try {
			return iShopRepository.updateShopById(id,shop);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int deleteShopById(Integer id) {
		try {
			return iShopRepository.deleteShopById(id);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int updatePwdByshopId(Integer id, String password) {
		try {
			return iShopRepository.updatePwdByshopId(id,password);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

}
