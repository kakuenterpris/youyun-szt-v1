package com.ustack.op.service.impl;

import com.ustack.resource.vo.BaseTreeNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author PingY
 * @Classname TreeNodeServiceImpl
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@Slf4j
public class TreeNodeServiceImpl {
    /**
     * 获取关联父节点数据集合列表
     * 由对应的子节点到根节点 (root)
     *
     * @param listNodes 要处理列表集合节点数据 (不是组合成树状图后的数据)
     * @param ids       要搜索对应父节点节点的 ids
     * @param <T>
     * @return
     */
    public static <T extends BaseTreeNodeVO> List<T> getParentList(List<T> listNodes, List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(listNodes)) {
            return new ArrayList<>();
        }
        // 数据保存的对象
        List<T> treeNodes = new ArrayList<>();
        for (Integer id : ids) {
            List<T> parentList = getParentList(listNodes, id);
            treeNodes.addAll(parentList);
        }
        return treeNodes;
    }

    /**
     * 获取关联父节点数据集合列表
     * 由对应的子节点到根节点(root)
     *
     * @param listNodes 要处理列表集合节点数据(不是组合成树状图后的数据)
     * @param id        要搜索对应父节点节点的id
     * @param <T>
     * @return
     */
    public static <T extends BaseTreeNodeVO> List<T> getParentList(List<T> listNodes, Integer id) {
        if (null == id || CollectionUtils.isEmpty(listNodes)) {
            return new ArrayList<>();
        }
        // 数据保存的对象
        List<T> treeNodes = new ArrayList<>();
        int length = listNodes.size();

        // 防止死循环
        byte[] nodeIndex = new byte[length];
        T t;
        for (int i = 0; i < length; i++) {
            t = listNodes.get(i);
            // 循环找到节点id，赋值id为当前节点pid
            if (id.equals(t.getId()) && nodeIndex[i] == 0) {
                nodeIndex[i] = 1;
                treeNodes.add(t);
                id = t.getParentId();
                // 父id为空， 或者0 ，结束循环
                if (null == id || id == 0) {
                    break;
                }
                i = -1;
            }
        }
        return treeNodes;
    }

    /**
     * 获取关联子节点数据集合列表
     * 由对应的子节点向子节点搜索
     *
     * @param listNodes
     * @param ids
     * @param <T>
     * @return
     */
    public static <T extends BaseTreeNodeVO> List<T> getChildrenList(List<T> listNodes, List<Integer> ids) {
        Integer[] tmpIds = ids.toArray(new Integer[ids.size()]);
        return CollectionUtils.isEmpty(ids) ? new ArrayList<>() : getChildrenList(listNodes, tmpIds);
    }

    /**
     * @param listNodes 要处理列表集合节点数据(不是组合成树状图后的数据)
     * @param id        要搜索对应子节点的id
     * @param <T>
     * @return
     */
    public static <T extends BaseTreeNodeVO> List<T> getChildrenList(List<T> listNodes, Integer id) {
        return null == id ? new ArrayList<>() : getChildrenList(listNodes, Arrays.asList(new Integer[]{id}));
    }

    /**
     * @param listNodes 要处理列表集合节点数据(不是组合成树状图后的数据)
     * @param ids       要搜索对应子节点的id(数组)
     * @param <T>
     * @return
     */
    public static <T extends BaseTreeNodeVO> List<T> getChildrenList(List<T> listNodes, Integer[] ids) {
        if (ids == null || ids.length == 0 || CollectionUtils.isEmpty(listNodes)) {
            return new ArrayList<>();
        }
        // 数据保存的对象
        List<T> treeNodes = new ArrayList<>();
        int length = listNodes.size();
        // 防止死循环问题
        byte[] nodeIndex = new byte[length];
        // 循环获取要获取节点
        T t;
        for (Integer id : ids) {
            for (int i = 0; i < length; i++) {
                t = listNodes.get(i);
                if (id.equals(t.getId())) {
                    treeNodes.add(t);
                    nodeIndex[i] = 1;
                }
            }
        }
        Integer tempId;
        int index = 0;
        while (index < treeNodes.size()) {
            tempId = treeNodes.get(index).getId();
            if (null != tempId) {
                for (int i = 0; i < length; i++) {
                    t = listNodes.get(i);
                    if (tempId.equals(t.getParentId()) && nodeIndex[i] == 0) {
                        nodeIndex[i] = 1;
                        treeNodes.add(t);
                    }
                }
            }
            index++;
        }
        return treeNodes;
    }

    /**
     * 封装整个树状图数据
     *
     * @param listNodes 要处理列表集合节点数据
     */
    public static <T extends BaseTreeNodeVO> List<T> assembleTree(List<T> listNodes) {
        List<T> newTreeNodes = new ArrayList<>();
        // 循环赋值最上面的节点数据
        // 赋值最上面节点的值
        newTreeNodes.addAll(listNodes.stream()
                .filter(t -> null == t.getParentId() || 0 == t.getParentId())
                .collect(Collectors.toList()));
        // 循环处理子节点数据
        for (T t : newTreeNodes) {
            //递归
            assembleTree(t, listNodes);
        }
        return newTreeNodes;
    }

    /**
     * 封装成单个树子节点数据
     *
     * @param id        根目录节点id
     * @param listNodes 要处理的列表数据
     */
    public static <T extends BaseTreeNodeVO> T assembleTreeById(Integer id, List<T> listNodes) {
        if (null == id || CollectionUtils.isEmpty(listNodes)) {
            return null;
        }
        // 获取对应的节点
        T node = null;
        for (T temp : listNodes) {
            if (id.equals(temp.getId())) {
                node = temp;
                break;
            }
        }
        assembleTree(node, listNodes);
        return node;
    }

    /**
     * 根据节点封装树状图集合数据
     *
     * @param node      处理的节点(当前节点)
     * @param listNodes 要处理的列表数据
     */
    static <T extends BaseTreeNodeVO> void assembleTree(T node, List<T> listNodes) {
        if (node != null && !CollectionUtils.isEmpty(listNodes)) {
            // 循环节点数据，如果是子节点则添加起来
            listNodes.stream().filter(t -> Objects.equals(t.getParentId(), node.getId())).forEachOrdered(node::addChildren);
            // 循环处理子节点数据,递归
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                for (Object t : node.getChildren()) {
                    //递归
                    assembleTree((T) t, listNodes);
                }
            }
        }
    }

    /**
     * 主键输出
     *
     * @param treeNodes 节点
     * @return String 注解集合
     */
    public static <T extends BaseTreeNodeVO> String idToString(List<T> treeNodes) {
        return idToString(treeNodes, ",");
    }

    /**
     * 主键输出
     *
     * @param treeNodes 节点
     * @param c         拼接字符串
     * @return String 注解集合
     */
    public static <T extends BaseTreeNodeVO> String idToString(List<T> treeNodes, String c) {
        StringBuilder pks = new StringBuilder();
        if (treeNodes != null) {
            for (T t : treeNodes) {
                pks.append(t.getId()).append(c);
            }
        }
        return pks.length() > 0 ? pks.delete(pks.length() - c.length(), pks.length()).toString() : "";
    }
}
