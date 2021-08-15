package com.yafey.restful;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@Entity
public class Girl {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String cupSize;
	
	private Integer age;
}
