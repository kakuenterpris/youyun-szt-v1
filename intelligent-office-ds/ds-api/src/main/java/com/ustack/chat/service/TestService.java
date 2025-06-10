package com.ustack.chat.service;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.test.TableTestDTO;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
public interface TestService {

    RestResponse testInsert(TableTestDTO param);


    RestResponse testUpdate (TableTestDTO param);


    RestResponse testQuery(TableTestDTO param);


}
