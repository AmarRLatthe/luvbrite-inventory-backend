package com.luvbrite.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.UserDetails;
import com.luvbrite.repository.IOperatorRepository;
import com.luvbrite.repository.IUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OperatorServiceImpl implements IOperatorService {
	
	@Autowired
	private IOperatorRepository iOperatorRepository; 
	
	@Autowired
	private IUserRepository iUserRepository;
	
	@Override
	public int saveOperator(UserDetails operator) {
		try {
			return iOperatorRepository.saveOperator(operator);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Override
	public List<UserDetails> getOperatorsDataByShopId(Integer shopId) {
		try {
			return iOperatorRepository.getOperatorsDataByShopId(shopId);
		} catch (Exception e) {
			log.error("Message is {} and exception is {}",e.getMessage(),e);
			return Collections.emptyList();
		}
	}

	@Override
	public Map<String, Object> validateOperator(UserDetails operator) {
		Map<String , Object> map = new HashMap<String, Object>();
		try {
			map.put("isValid", true);	
			if(operator.getUsername()==null) {
				map.put("username", "username should not be empty");
				map.put("isValid", false);
			}else {
				int count = iUserRepository.countUserByUserName(operator.getUsername());
				if(count>0) {
					map.put("username", "username is already available.please try with different username");
					map.put("isValid", false);
				}
			}
			if(operator.getPassword()==null) {
				map.put("pwdErr", "password should not be empty");
				map.put("isValid", false);
			}
			if(operator.getEmail()!=null){
				int count = iUserRepository.countUserByEmail(operator.getEmail());
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

	@Override
	public int updateOperatorById(int id, UserDetails operator) {
		try {
			return iOperatorRepository.updateOperatorById(id,operator);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}

	}

	@Override
	public int deleteOperatorById(Integer id) {
		try {
			return iOperatorRepository.deleteOperatorById(id);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

	@Override
	public int updatePwdByOperatorId(Integer id, String password) {
		try {
			return iOperatorRepository.updatePwdByOperatorId(id,password);
		} catch (Exception e) {
			log.info("message is {} and exception is {}",e.getMessage(),e);
			return -1;
		}
	}

}
