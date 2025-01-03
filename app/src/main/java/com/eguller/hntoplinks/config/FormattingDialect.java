package com.eguller.hntoplinks.config;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import com.eguller.hntoplinks.util.FormattingUtils;

@Component
public class FormattingDialect extends AbstractDialect implements IExpressionObjectDialect {

  public FormattingDialect() {
    super("formatting");
  }

  @Override
  public IExpressionObjectFactory getExpressionObjectFactory() {
    return new IExpressionObjectFactory() {

      @Override
      public Set<String> getAllExpressionObjectNames() {
        return Set.of("fmt");
      }

      @Override
      public Object buildObject(IExpressionContext context, String expressionObjectName) {
        return FormattingUtils.getInstance();
      }

      @Override
      public boolean isCacheable(String expressionObjectName) {
        return true;
      }
    };
  }
}
