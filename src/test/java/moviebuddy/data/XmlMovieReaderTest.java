package moviebuddy.data;

import moviebuddy.MovieBuddyFactory;
import moviebuddy.MovieBuddyProfile;
import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ActiveProfiles(MovieBuddyProfile.XML_MODE)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MovieBuddyFactory.class)
@TestPropertySource(properties = "movie.metadata=movie_metadata.xml")
class XmlMovieReaderTest {

    final MovieReader movieReader;

    public XmlMovieReaderTest(@Qualifier("xmlMovieReader") MovieReader movieReader) {
        this.movieReader = movieReader;
    }

    @Test
    void NotEmpty_LoadedMovies() {
        List<Movie> movies = movieReader.loadMovies();
        Assertions.assertEquals(1375, movies.size());
    }
}