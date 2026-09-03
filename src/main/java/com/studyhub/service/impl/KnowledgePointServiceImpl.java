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
         KnowledgePoint kp = getById(id);
        if(kp == null){
            throw new BusinessException(404,"知识点不存在");
        }
        return kp;
    }

    @Override
    public boolean add(KnowledgePoint knowledgePoint){
        if (categoryMapper.selectById(knowledgePoint.getCategoryId()) == null) {
            throw new BusinessException(400, "分类不存在");
        }
        return this.save(knowledgePoint);
    }

    @Override
    public KnowledgePoint update(KnowledgePoint knowledgePoint){
        if (this.getById(knowledgePoint.getId()) == null) {
            throw new BusinessException(404,"知识点不存在");
        }
        this.updateById(knowledgePoint);
        return this.getById(knowledgePoint.getId());
    }

    @Override
    public boolean delete(Long id){
       if(this.getById(id) == null){
           throw new BusinessException(404,"知识点不存在");
       }
       return this.removeById(id);
    }

    @Override
    public Page<KnowledgePoint> getPage(int page,int size,Long categoryId, String keyword,Integer importance,Integer status){

        size = Math.min(size,100);
        page = Math.min(page,1);

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

}
