package com.luvbrite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CategoryDTO {

	private int id;
	private String categoryName;
	private long dateAdded;
	private String formattedDateAdded;
}
