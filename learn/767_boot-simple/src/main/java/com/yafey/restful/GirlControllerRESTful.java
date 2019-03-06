package com.yafey.restful;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GirlControllerRESTful {

	@Autowired
	private GirlRepository girlRepository;

	/**
	 * 查询所有女生列表
	 * 
	 * @return
	 */
	@GetMapping(value = "/girls")
	public List<Girl> girlList() {
		return girlRepository.findAll();
	}

	/**
	 * 添加一个女生
	 * 
	 * @param girl
	 * @return
	 */
	@PostMapping("/girls")
	public Girl girlAdd(@RequestBody Girl girl) {
		return girlRepository.save(girl);
	}

	/**
	 * 查询一个女生
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/girls/{id}")
	public Girl girlFindOne(@PathVariable("id") Integer id) {
		return girlRepository.findById(id).get();
	}

	// 更新
	@PutMapping("/girls/{id}")
	public Girl girlUpdate(@PathVariable Integer id, @RequestParam String cupSize, @RequestParam Integer age) {
		Girl girl = new Girl().setId(id).setAge(age).setCupSize(cupSize);
		return girlRepository.save(girl);
	}

	// 删除
	@DeleteMapping("/girls/{id}")
	public void girlDelete(@PathVariable Integer id) {
		girlRepository.deleteById(id);
	}
	
    @GetMapping("/girls/age/{age}")
    public List<Girl> girlFindByAge(@PathVariable Integer age) {
        return girlRepository.findByAge(age);
    }


}
