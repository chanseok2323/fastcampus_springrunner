package moviebuddy;

import com.github.benmanes.caffeine.cache.Caffeine;
import moviebuddy.cache.CachingAdvice;
import moviebuddy.cache.CachingAspect;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.cache.annotation.CacheResult;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("/application.properties")
@ComponentScan
@Import({MovieBuddyFactory.DomainModuleConfig.class, MovieBuddyFactory.DataSourceModuleConfig.class})
@EnableAspectJAutoProxy
public class MovieBuddyFactory {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("moviebuddy");
        return marshaller;
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS));

        return cacheManager;
    }

    /*
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public Advisor cachingAdvisor(CacheManager cacheManager) {
        //NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        //pointcut.setMappedName("load*");
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, CacheResult.class);

        Advice advice = new CachingAdvice(cacheManager);
        // Advisor = Pointcut(대상 선정) + Advice(부가기능)
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
     */

    @Bean
    public CachingAspect cachingAspect(CacheManager cacheManager) {
        return new CachingAspect(cacheManager);
    }

    @Configuration
    static class DomainModuleConfig {

    }

    @Configuration
    static class DataSourceModuleConfig {

        /**
         * proxyFactoryBean.setProxyTargetClass(true);
         * 클래스 프록시는 CGLIB이라고 하는 바이트 코드 생성 라이브러리를 이용하여 대상 객체 타입을 상속해서 서브클래스로 만들어 이를 프록시로 사용
         * final 클래스와 final 메소드에는 적용안됨 (final 클래스는 상속 안됨, final 메소드는 오버라이등 불가)
         * 대상 클래스의 생성자가 두번 호출 됨 (같은 대상 클래스의 타입의 빈이 두 개가 만들어지기 떄문에)
         */
        /*
        @Bean
        @Primary
        public ProxyFactoryBean cachingMovieReaderFactory(ApplicationContext applicationContext) {
            MovieReader target = applicationContext.getBean(MovieReader.class);
            CacheManager cacheManager = applicationContext.getBean(CacheManager.class);

            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setTarget(target);
            // 클래스 프록시 활성화(true)/비활성화(false, 기본값)
            // proxyFactoryBean.setProxyTargetClass(true);
            proxyFactoryBean.addAdvice(new CachingAdvice(cacheManager));
            return proxyFactoryBean;
        }
        */
    }
}
