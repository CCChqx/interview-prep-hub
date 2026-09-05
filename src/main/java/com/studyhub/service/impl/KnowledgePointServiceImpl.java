package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.CategoryMapper;
import com.studyhub.mapper.KnowledgePointMapper;
import com.studyhub.service.KnowledgePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper,KnowledgePoint>
        implements KnowledgePointService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public KnowledgePoint getDetail(Long id) {
        String key = "studyhub:kp:detail:" + id;

        // 先查缓存
        String cache = stringRedisTemplate.opsForValue().get(key);

        if (cache != null) {
            if ("__NULL__".equals(cache)) {
                throw new BusinessException(404,"知识点不存在");
            }
            try{
                return objectMapper.readValue(cache,KnowledgePoint.class);
            }catch (Exception e){
                log.warn("缓存反序列化失败 key={}",key,e);
            }
        }

        // 没命中→抢锁（防击穿，只让一个线程查库回填）
        String lockKey = "studyhub:kp:detail:lock:" + id;
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey,"1",Duration.ofSeconds(3));  // setnx + 3秒过期

        if (Boolean.TRUE.equals(locked)) {
            // 抢到锁：查库+回填+释放锁
            try{
                KnowledgePoint kp = getById(id);
                if(kp == null){
                    // 空值缓存,查不到也缓存空标记,TTL短（5分钟）,放穿透
                    stringRedisTemplate.opsForValue().set(key,"__NULL__",Duration.ofMinutes(5));
                    throw new BusinessException(404,"知识点不存在");
                }
                // 回填缓存：writeValueAsString 抛受检异常，单独try-catch
                try {
                    stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(kp),
                            Duration.ofMinutes(30 + ThreadLocalRandom.current().nextInt(10)));
                }catch (Exception e){
                    log.warn("回填缓存失败 key={}",key,e);
                }
                return kp;
            }finally {
                stringRedisTemplate.delete(lockKey); // 释放锁
            }
        }else {
            // 没抢到锁：别人正在回填，睡50ms重试（此时应已回填）
            try{
                Thread.sleep(50);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            return getDetail(id); //重试，缓存命中直接返回
        }
    }

    @Override
    public boolean add(KnowledgePoint knowledgePoint){
        if (categoryMapper.selectById(knowledgePoint.getCategoryId()) == null) {
            throw new BusinessException(400, "分类不存在");
        }
        clearPageCache();
        return this.save(knowledgePoint);
    }

    @Override
    public KnowledgePoint update(KnowledgePoint knowledgePoint){
        if (this.getById(knowledgePoint.getId()) == null) {
            throw new BusinessException(404,"知识点不存在");
        }
        this.updateById(knowledgePoint);
        clearPageCache();
        stringRedisTemplate.delete("studyhub:kp:detail:" + knowledgePoint.getId());
        return this.getById(knowledgePoint.getId());
    }

    @Override
    public boolean delete(Long id){
       if(this.getById(id) == null){
           throw new BusinessException(404,"知识点不存在");
       }
       clearPageCache();
       stringRedisTemplate.delete("studyhub:kp:detail:" + id);
       return this.removeById(id);
    }

    @Override
    public Page<KnowledgePoint> getPage(int page,int size,Long categoryId, String keyword,Integer importance,Integer status){

        size = Math.min(size,100);
        page = Math.max(page,1);

        // 缓存key 按查询参数拼，不同条件 = 不同缓存
        String key = "studyhub:kp:page" + categoryId + "_" + page + "_" + size + "_" + keyword + "_" + importance + "_" + status;

        // 先查缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (cache != null) {
            try{
                return objectMapper.readValue(cache, new TypeReference<Page<KnowledgePoint>>() {});
            }catch (Exception e){
                log.warn("缓存反序列化失败 key={}",key,e); //失败当缓存失效，走查库
            }
        }

        // 告诉 MP 我要第 page页，每页size条 没命中
        Page<KnowledgePoint> p = new Page<>(page,size);

        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<>();

        // ③ 动态条件：传了才筛，没传不加
        if (categoryId != null) {
            wrapper.eq(KnowledgePoint::getCategoryId, categoryId);   // WHERE category_id = ?
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(KnowledgePoint::getTitle, keyword);         // AND title LIKE '%?%'
        }
        if (importance != null) {
            wrapper.eq(KnowledgePoint::getImportance, importance);   // AND importance = ?
        }
        if (status != null) {
            wrapper.eq(KnowledgePoint::getStatus, status);           // AND status = ?
        }

        wrapper.select(KnowledgePoint::getId, KnowledgePoint::getCategoryId,
                KnowledgePoint::getTitle, KnowledgePoint::getTags,
                KnowledgePoint::getImportance, KnowledgePoint::getStatus,
                KnowledgePoint::getCreateTime, KnowledgePoint::getUpdateTime);

        // 按时间倒序排列
        wrapper.orderByDesc(KnowledgePoint::getCreateTime);

        // 执行分页查询 自动 COUNT LIMIT
        Page<KnowledgePoint> result = this.page(p, wrapper);

        // 回填缓存（TTL随机，防雪崩）
        try{
            stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(result),
                    Duration.ofMinutes(30 + ThreadLocalRandom.current().nextInt(10)));
        }catch (Exception e){
            log.warn("回填缓存失败 key={}",key,e);
        }
        return result;
    }

    // 清所有分页列表缓存 （增删改会影响列表）
    private void clearPageCache(){
        Set<String> keys = stringRedisTemplate.keys("studyhub:kp:page*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

}
