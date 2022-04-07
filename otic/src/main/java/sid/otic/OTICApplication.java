package sid.otic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import sid.otic.OTICApplication;

/**
 * @author ock
 */
@SpringBootApplication
public class OTICApplication {

    public static void main(String[] args) {
        SpringApplication.run(OTICApplication.class, args);
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
    
        return new OpenAPI()
          .info(new Info()
	          .title("One-Time Inverse Consultation (OTIC) API")
	          .version("1.0.0-oas3")
	          .description("This API allows to obtain translations from a source language into a target language by pivoting on a third language in the Apertium RDF graph. It is based on the method proposed by Tanaka and Umemura in 1994 [1], and adapted by Lin et. al 2011 [2]. References: [1] K. Tanaka and K. Umemura. \"Construction of a bilingual dictionary intermediated by a third language\". In COLING, pages 297–303, 1994. [2] L. T. Lim, B. Ranaivo-Malançon, and E. K. Tang. \"Low cost construction of a multilingual lexicon from bilingual lists\". Polibits, 43:45–51, 2011.")
	          //.license(new License().name("Apache 2.0").url("http://springdoc.org"))
	      );
    }

}
