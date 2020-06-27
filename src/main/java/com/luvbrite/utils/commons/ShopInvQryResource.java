package com.luvbrite.utils.commons;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Setter
@Slf4j
public class ShopInvQryResource {
	
	@Value("${shop.inv.qry.path}")
	private String path;
	
	private String shopInvQry;
	
	public ShopInvQryResource(@Value("${shop.inv.qry.path}") String path) {
		log.info("path ::>> {}",path);
		this.path = path;
	}
	
	@PostConstruct
	private void getQuery() {
		log.info("Path Is :::::>>>>  {}",path);
		InputStream iS=	ShopInvQryResource.class.getResourceAsStream(path);
		
		log.info("MY ::::>>>> {}",iS);
		shopInvQry = readInputStreamThroughBufferedReader(iS);
		log.info("\n\n\n\nHello\n\n\n\n {}",shopInvQry);
	 
	}
	
	private String readInputStreamThroughBufferedReader(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();

		try {

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			log.error("Exception occured while reading ShopInvtoryQuery", e);
			return e.getMessage();
		}

		return sb.toString();
	}


}
