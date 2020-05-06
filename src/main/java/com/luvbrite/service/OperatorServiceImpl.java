package com.luvbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luvbrite.model.UserDetails;
import com.luvbrite.repository.IOperatorRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OperatorServiceImpl implements IOperatorService {
	
	@Autowired
	private IOperatorRepository iOperatorRepository; 
	
	@Override
	public int saveOperator(UserDetails operator) {
		try {
			return iOperatorRepository.saveOperator(operator);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(),e);
			return -1;
		}

	}

}
