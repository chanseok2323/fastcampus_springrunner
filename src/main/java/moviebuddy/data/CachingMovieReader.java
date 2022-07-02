package moviebuddy.data;

import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Objects;

public class CachingMovieReader implements MovieReader {

    private static final String CACHE_NAME = CachingMovieReader.class.getName();
    private static final String CACHE_KEY = "movies";

    private final CacheManager cacheManager;
    private final MovieReader target;

    public CachingMovieReader(CacheManager cacheManager, MovieReader target) {
        this.cacheManager = Objects.requireNonNull(cacheManager);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public List<Movie> loadMovies() {
        //캐시된 데이터가 있으면 즉시 반환 처리
        Cache cache = cacheManager.getCache(CACHE_NAME);
        List<Movie> movies = cache.get(CACHE_KEY, List.class);
        if(Objects.nonNull(movies)) {
            return movies;
        }

        //캐시된 데이터가 없으면, 대상 객체에서 명령을 위임하고, 반환 받은 값을 캐시에 저장 후 반환 처리
        movies = target.loadMovies();
        cache.put(CACHE_KEY, movies);

        return movies;
    }

    public List<Movie> getCachedData() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        return cache.get(CACHE_KEY, List.class);
    }
}
