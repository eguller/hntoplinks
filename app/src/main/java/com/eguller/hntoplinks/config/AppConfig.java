package com.eguller.hntoplinks.config;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig implements WebMvcConfigurer, SchedulingConfigurer {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${hntoplinks.firebaseio-url}")
  private String firebaseIoBaseUrl;

  @Autowired private FormattingDialect formattingDialect;

  @Autowired private DateDialect dateDialect;

  @Bean
  public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
    return new DeviceHandlerMethodArgumentResolver();
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(deviceHandlerMethodArgumentResolver());
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(30, ChronoUnit.SECONDS))
        .additionalMessageConverters(new StringHttpMessageConverter(Charset.forName("UTF-8")))
        .build();
  }

  @Bean
  public RestClient fireBaseRestClient(RestTemplate restTemplate) {
    return RestClient.builder().baseUrl(firebaseIoBaseUrl).build();
  }

  @Bean
  public LayoutDialect layoutDialect() {
    return new LayoutDialect();
  }

  @Bean
  public TemplateEngine emailTemplateEngine() {
    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    // Resolver for HTML emails (except the editable one)
    templateEngine.addDialect(formattingDialect);
    templateEngine.addDialect(dateDialect);
    templateEngine.addTemplateResolver(htmlTemplateResolver());
    return templateEngine;
  }

  private ITemplateResolver htmlTemplateResolver() {
    final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setOrder(Integer.valueOf(2));
    templateResolver.setResolvablePatterns(
        new HashSet<>(Arrays.asList("/email/html/*", "/fragments/*")));
    templateResolver.setPrefix("/templates");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCharacterEncoding("UTF-8");
    templateResolver.setCacheable(false);
    return templateResolver;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
  }

  @Bean(destroyMethod = "shutdown")
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(5);
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToEnumConverter());
  }
}
