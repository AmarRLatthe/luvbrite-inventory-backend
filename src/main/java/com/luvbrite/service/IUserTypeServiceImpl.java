package com.luvbrite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.luvbrite.model.UserTypeDTO;
import com.luvbrite.repository.IUserTypeRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class IUserTypeServiceImpl implements IUserTypeService{
	
	@Autowired
	private IUserTypeRepository iUserTypeRepository;

	@Cacheable("userType")
	@Override
	public List<UserTypeDTO> getAllUserTypes() {
		return iUserTypeRepository.getAllUserTypes();
	}
}
