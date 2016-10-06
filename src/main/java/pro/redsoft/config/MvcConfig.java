package pro.redsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/node_modules/**")
                .addResourceLocations("classpath:/templates/node_modules/");

        registry
                .addResourceHandler("/app")
                .addResourceLocations("classpath:/templates/");
    }

//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        super.configureViewResolvers(registry);
//        registry.viewResolver(new InternalResourceViewResolver());
//    }
//
//    @Bean
//    public ViewResolver internalResourceViewResolver() {
//        InternalResourceViewResolver bean = new InternalResourceViewResolver();
//        bean.setViewClass(JstlView.class);
//        //bean.setPrefix("/templates/");
//        bean.setSuffix(".jsp");
//        return bean;
//    }
}
