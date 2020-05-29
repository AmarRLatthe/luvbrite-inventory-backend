package com.luvbrite.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.luvbrite.model.PurchaseDTO;
import com.luvbrite.model.PurchasedOrderDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PurchaseRepositoryImpl implements IPurchaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public PurchaseDTO addPurchase(PurchaseDTO purchase) throws Exception{
		try {
			log.info("Came in purchase Repository ");
		

			String formatted_date="";
			
			
			StringBuilder _addPurchaseQry = new StringBuilder();
			                         
	     _addPurchaseQry.append("INSERT INTO purchase_inventory(product_id, growth_condition, quantity, weight_in_grams, ")
			            .append("unit_price, vendor_id, operator_comments, date_added, purchase_code,shop_id,created_by) ")
			            .append("VALUES (?,?,?,?,?,?,?,to_date(?,'yyyy-MM-dd'),?,?,?)")
			            .append("RETURNING  TO_CHAR(date_added, 'MM/dd/yyyy') AS formatted_date;");
			
			
			 formatted_date = (String) jdbcTemplate.queryForObject(_addPurchaseQry.toString(),
																	new Object[] { purchase.getProductId(), 
																			       purchase.getGrowthCondition(), 
																			       purchase.getQuantity(),
																			       purchase.getWeightInGrams(), 
																			       purchase.getUnitPrice(), 
																			       purchase.getVendorId(),
																			       purchase.getOperatorComments(), 
																			       purchase.getDateAdded(), 
																			       purchase.getPurchaseCode(),
																			       purchase.getShopId(),
																			       purchase.getCreatedBy()
																			       },
																	               String.class);
												
			purchase.setDateAdded(formatted_date);
			
			return purchase;

		} catch (Exception e) {
			log.error("Message is {} and exception is {}", e.getMessage(), e);
			throw e;
			//return null;
		}
		
	}

	@Override
	public List<PurchaseDTO> getAllPurchases() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updatePurchaseById(Integer id, PurchaseDTO purchase) {
		try {
			StringBuilder qry = new StringBuilder();
			qry.append(" UPDATE purchase_inventory ")
			.append(" SET  ")
			.append(" product_id=?, ")
			.append(" quantity=?, ")
			.append(" weight_in_grams=?, ")
			.append(" unit_price=?, ")
			.append(" vendor_id=?, ")
			.append(" operator_comments=?, ")
			.append(" date_added=to_date(?,'yyyy-MM-dd') ")
			.append(" WHERE ")
			.append(" id=?; ");
				
			return jdbcTemplate.update(qry.toString(), 
						purchase.getProductId(),
						purchase.getQuantity(),
						purchase.getWeightInGrams(),
						purchase.getUnitPrice(),
						purchase.getVendorId(),
						purchase.getOperatorComments(),
						purchase.getDateAdded(),
						id
					);
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public PurchasedOrderDTO getPurchasedOrderDetailsById(Integer id) {
		try {
			StringBuilder sb = new StringBuilder();
						sb.append(" SELECT ")
							.append(" COALESCE(NULLIF(pi.growth_condition,null),'') AS growth_condition ,")
							.append(" pi.quantity,  ")
							.append(" (pi.quantity*pi.weight_in_grams) AS purchased,  ")
							.append(" to_char(pi.date_added, 'MM/dd/YYYY') AS date_purchased, ")
							.append(" p.product_name,  ")
							.append(" v.vendor_name,  ")
							.append(" c.category_name,  ")
							.append(" s.strain_name, ")
							.append(" COALESCE(NULLIF(packets.total_packets,null),'0') AS total_packets, ")
							.append(" COALESCE(NULLIF(packets.total_packets_grams,null),'0') AS total_packets_grams, ")
							.append(" COALESCE(NULLIF(sold_packets.sold_packets,null),'0') AS sold_packets, ")
							.append(" COALESCE(NULLIF(sold_packets.sold_grams,null),'0') AS sold_grams ")
							.append(" FROM  purchase_inventory pi  ")
							.append(" JOIN ")
							.append(" products p on pi.product_id = p.id  ")
							.append(" JOIN  ")
							.append(" vendors v on pi.vendor_id = v.id  ")
							.append(" JOIN ")
							.append(" categories c on p.category_id = c.id  ")
							.append(" JOIN ")
							.append(" strains s on p.strain_id = s.id  ")
							.append(" LEFT JOIN  ")
							.append(" ( ")
							.append(" SELECT purchase_id, COUNT(id) AS total_packets, SUM(weight_in_grams) AS total_packets_grams ")
							.append(" FROM packet_inventory  ")
							.append(" WHERE purchase_id = ? Group By purchase_id  ")
							.append(" ) ")
							.append(" AS packets ON pi.id = packets.purchase_id ")
							.append(" LEFT JOIN ")
							.append(" ( ")
							.append(" SELECT purchase_id, COUNT(id) AS sold_packets, SUM(weight_in_grams) AS sold_grams ")
							.append(" FROM packet_inventory ")
							.append(" WHERE sales_id > 0 AND purchase_id = ? GROUP By purchase_id ")
							.append(" ) ")
							.append(" AS sold_packets ON pi.id = sold_packets.purchase_id ")
							.append(" WHERE ")
							.append(" pi.id = ? ");
							
					return jdbcTemplate.queryForObject(sb.toString(),new Object[] {id ,id, id}, new RowMapper<PurchasedOrderDTO>() {

						@Override
						public PurchasedOrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
							PurchasedOrderDTO dto = new PurchasedOrderDTO();
								dto.setCondition(rs.getString("growth_condition"));
								dto.setPurchaseCount(rs.getInt("quantity"));
								dto.setPurchased(rs.getDouble("purchased"));
								dto.setCategoryName(rs.getString("category_name"));
								dto.setInpackets(rs.getDouble("total_packets_grams"));
								dto.setPacketCount(rs.getInt("total_packets"));	
								dto.setProductName(rs.getString("product_name"));
								dto.setPurchaseDate(rs.getString("date_purchased"));
								dto.setSold(rs.getDouble("sold_grams"));
								dto.setSoldCount(rs.getInt("sold_packets"));
								dto.setVendorName(rs.getString("vendor_name"));	
								dto.setStrainName(rs.getString("strain_name"));
								dto.setRemainingCount(rs.getInt("total_packets") - rs.getInt("sold_packets"));
								dto.setRemainingPackets((double)(rs.getInt("total_packets") - rs.getInt("sold_packets")));
								dto.setRemainingTotal((double)(rs.getInt("total_packets") - rs.getInt("sold_packets")));
									

								
								return dto;
						}
					});
		} catch (Exception e) {
			log.error("Message is {} and Exception is {}",e.getMessage(), e);
			return null;
		}
	}

}
