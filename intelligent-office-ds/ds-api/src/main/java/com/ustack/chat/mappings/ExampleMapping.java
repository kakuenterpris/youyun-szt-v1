package com.ustack.chat.mappings;

import com.ustack.chat.entity.TableTestEntity;
import com.ustack.login.dto.UserInfoDTO;
import com.ustack.login.vo.UserInfoVO;
import com.ustack.test.TableTestBO;
import com.ustack.test.TableTestDTO;
import com.ustack.test.TableTestVO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface ExampleMapping {
    TableTestVO dto2Vo(TableTestDTO param);

    TableTestBO dto2Bo(TableTestDTO param);

    TableTestEntity bo2Entity(TableTestBO param);

    TableTestBO entity2Bo(TableTestEntity param);


}
