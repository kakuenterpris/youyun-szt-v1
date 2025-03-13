package com.thtf.chat.mappings;

import com.thtf.chat.entity.TableTestEntity;
import com.thtf.login.dto.UserInfoDTO;
import com.thtf.login.vo.UserInfoVO;
import com.thtf.test.TableTestBO;
import com.thtf.test.TableTestDTO;
import com.thtf.test.TableTestVO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface ExampleMapping {
    TableTestVO dto2Vo(TableTestDTO param);

    TableTestBO dto2Bo(TableTestDTO param);

    TableTestEntity bo2Entity(TableTestBO param);

    TableTestBO entity2Bo(TableTestEntity param);


}
