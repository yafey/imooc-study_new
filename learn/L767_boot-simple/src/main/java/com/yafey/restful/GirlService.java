package com.yafey.restful;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GirlService {

    @Autowired
    private GirlRepository girlRepository;

    @Transactional(rollbackFor = Exception.class)
    public void insertTwo(List<Girl> girlsList) throws Exception {

        for (Girl girl : girlsList) {
            if (girl.getAge()>40) {
                throw new Exception("too old");
            }
            girlRepository.save(girl);
        }
    }

}
