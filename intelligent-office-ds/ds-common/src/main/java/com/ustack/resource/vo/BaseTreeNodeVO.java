package com.ustack.resource.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseTreeNodeVO {

    @TableField(exist = false)
    private Integer id;

    @TableField(exist = false)
    private Integer parentId;

    @TableField(exist = false)
    private List<BaseTreeNodeVO> children;

    public BaseTreeNodeVO() {
    }

    public void addChildren(BaseTreeNodeVO baseTreeNodeVO){
        if (this.children == null){
            this.setChildren(new ArrayList<>());
        }
        this.getChildren().add(baseTreeNodeVO);
    }

}
