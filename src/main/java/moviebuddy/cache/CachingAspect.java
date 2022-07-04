package moviebuddy.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

@Aspect
public class CachingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final CacheManager cacheManager;

    public CachingAspect(CacheManager cacheManager) {
        this.cacheManager = Objects.requireNonNull(cacheManager);
    }

    @Around("target(moviebuddy.domain.MovieReader)")
    public Object doCachingReturnValue(ProceedingJoinPoint joinPoint) throws Throwable {
        // 캐시된 데이터가 있으면, 캐시 데이터를 즉시 반환 처리
        Cache cache = cacheManager.getCache(joinPoint.getThis().getClass().getName());
        Object cachedValue = cache.get(joinPoint.getSignature().getName(), Object.class);
        if(Objects.nonNull(cachedValue)) {
            log.info("returns cached data. [{}]", joinPoint);
            return cachedValue;
        }

        // 캐시된 데이터가 없으면, 대상 객체에 명령을 위임하고, 반환 받은 값을 캐시에 저장 후 반환
        cachedValue = joinPoint.proceed();
        cache.put(joinPoint.getSignature().getName(), cachedValue);
        log.info("caching return value. [{}]", joinPoint);

        return cachedValue;
   }
}
