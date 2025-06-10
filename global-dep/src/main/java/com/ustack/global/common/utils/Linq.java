package com.ustack.global.common.utils;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Ducky Yang
 * Date: 2024/2/4
 * Description:
 */
public class Linq<T> {

    private Stream<T> stream;

    /**
     * 设定数据源
     *
     * @param source
     */
    public Linq(List<T> source) {
        if (source == null) {
            throw new IllegalArgumentException("source 不能为null");
        }
        stream = source.stream();
    }

    public <U extends Comparable<? super U>> Linq<T> orderBy(Function<? super T, ? extends U> func) {
        stream = stream.sorted(Comparator.comparing(func));
        return this;
    }

    public <U extends Comparable<? super U>> Linq<T> orderByDesc(Function<? super T, ? extends U> func) {
        stream = stream.sorted(Comparator.comparing(func).reversed());
        return this;
    }

    public Linq<T> where(Predicate<T> predicate) {
        stream = stream.filter(predicate);
        return this;
    }

    public T first() {
        return stream.findFirst().orElse(null);
    }

    public T first(Predicate<T> predicate) {
        return stream.filter(predicate).findFirst().orElse(null);
    }

    public void forEach(Consumer<T> consumer) {
        this.stream.forEach(consumer);
    }

    public List<T> list() {
        return stream.collect(Collectors.toList());
    }

    public List<T> list(Predicate<T> predicate) {
        return stream.filter(predicate).collect(Collectors.toList());
    }

    public <U extends Comparable<? super U>> List<T> list(Predicate<T> predicate, Function<? super T, ? extends U> orderBy, Boolean asc) {
        return asc ? this.orderBy(orderBy).list(predicate) : this.orderByDesc(orderBy).list(predicate);
    }

    public Linq<T> skip(long size) {
        stream = stream.skip(size);
        return this;
    }

    public Linq<T> take(long size) {
        stream = stream.limit(size);
        return this;
    }

    public Boolean any(Predicate<T> predicate) {
        return stream.anyMatch(predicate);
    }

    public Boolean contains(Predicate<T> predicate) {
        return stream.anyMatch(predicate);
    }

    public Boolean all(Predicate<T> predicate) {
        return stream.allMatch(predicate);
    }

    public Boolean none(Predicate<T> predicate) {
        return stream.noneMatch(predicate);
    }

    public <R> List<R> select(Function<T, R> func) {
        return stream.map(func).collect(Collectors.toList());
    }

    public <R> Linq<R> selectTo(Function<T, R> func) {
        List<R> list = stream.map(func).collect(Collectors.toList());
        return new Linq<>(list);
    }

    public Linq<T> distinct() {
        stream = stream.distinct();
        return this;
    }

    public <R> List<R> distinct(Function<T, R> func) {
        return stream.map(func).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
    }

    public List<T> distinct(Predicate<T> predicate) {
        List<T> n = new ArrayList<>();
        stream.forEach(item -> {
            if (predicate.test(item)) {
                n.add(item);
            }
        });
        return n;
    }

    public <R> List<R> mapTo(Class<R> clazz) {
        List<R> list = new ArrayList<>();
        stream.forEach(item -> {
            try {
                R instance = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(item, instance);
                list.add(instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 判断集合中是否包含指定表达式的项
     *
     * @param list
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Boolean contains(List<T> list, Predicate<T> predicate) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    public static <T> Boolean contains(Collection<T> list, Predicate<T> predicate) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    /**
     * 判断集合中是否所有项都满足表达式
     *
     * @param list
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Boolean all(List<T> list, Predicate<T> predicate) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return list.stream().allMatch(predicate);
    }

    /**
     * 从集合中寻找第一个符合表达式的项
     *
     * @param list
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> T first(List<T> list, Predicate<T> predicate) {
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.stream().filter(predicate).findFirst().orElse(null);
    }
    public static <T> T first(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.stream().filter(x->true).findFirst().orElse(null);
    }

    /**
     * 从集合中查找符合表达式的所有项
     *
     * @param list
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> List<T> find(List<T> list, Predicate<T> predicate) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }

        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 从集合中查找符合委托表达式的内容，并返回结果
     *
     * @param list
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> select(List<T> list, Function<T, R> func) {
        List<R> rs = new ArrayList<>();
        list.forEach(item -> {
            R res = func.apply(item);
            rs.add(res);
        });
        return rs;
    }

    /**
     * 从集合中查找符合委托表达式的内容，并返回结果
     *
     * @param list
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Set<R> selectSet(List<T> list, Function<T, R> func) {
        Set<R> rs = new TreeSet<>();
        for (T t : list) {
            R res = func.apply(t);
            rs.add(res);
        }
        return rs;
    }

    /**
     * 快速创建一个可变集合，原始写法太长了，简单封装一下
     *
     * @param args
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> List<T> as(T... args) {
        return new ArrayList<>(Arrays.asList(args));
    }

    /**
     * 判断一个集合是否为空，增加了null的判断
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 判断集合不为空 或 null
     * @param list
     * @return
     * @param <T>
     */
    public static <T> Boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }

    /**
     * 判断集合中是否存在重复元素
     * @param: list
     * @author linxin
     * @return Boolean
     * @date 2024/8/17 15:04
     */
    public static <T> Boolean haDuplicate(List<T> list){
        if (isEmpty(list)){
            return false;
        }
        return list.stream().distinct().count() != list.size();
    }

    public static <T> List<T> in(List<T> list, Collection<T> searchList){
        if (CollUtil.isEmpty(list)){
            return Collections.emptyList();
        }
        if (CollUtil.isEmpty(searchList)){
            return list;
        }
        return list.stream().filter(f -> searchList.stream().anyMatch(m -> Objects.equals(f, m))).collect(Collectors.toList());
    }

    public static <T> List<T> toList(Collection<T> list){
        return list.stream().collect(Collectors.toList());
    }


    /**
     * 分组
     * @param: list
     * @param: keyExtractor
     * @author linxin
     * @return Map<String,List<T>>
     * @date 2024/11/5 17:18
     */
    public static <K,T> Map<K,List<T>> group(Collection<T> list, Function<T, K> keyExtractor){
        if (CollUtil.isEmpty(list) || Objects.isNull(keyExtractor)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(keyExtractor));
    }

    public static <K,T> Map<K,T> toMap(Collection<T> list, Function<T, K> keyExtractor){
        if (CollUtil.isEmpty(list) || Objects.isNull(keyExtractor)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity(), (o, n) -> n));
    }
}
