package com.terapico.caf;

import cn.hutool.core.util.ReflectUtil;
import com.terapico.uccaf.BaseUserContext;
import com.terapico.uccaf.UCInvocationContext;
import com.terapico.uccaf.UCInvocationContextFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;

public class ApplicationUCInvocationContextFactory extends UCInvocationContextFactory {
  public ApplicationUCInvocationContextFactory(ApplicationContext pApplicationContext) {
    super(pApplicationContext);
  }

  @Override
  public InvocationContext create(Object input) throws InvocationException {
    try {
      return super.create(input);
    } catch (NoSuchBeanDefinitionException  | InvocationException e ){
      InvocationContext invocationContext = fallBackWithCustomizer(input);
      if (invocationContext != null) {
        return invocationContext;
      } else {
        throw e;
      }
    }
  }

  private InvocationContext fallBackWithCustomizer(Object input) {
    if (!(input instanceof HttpServletRequest)) {
      throw new IllegalArgumentException(
          "Could not create the call context since the type is: "
              + input.getClass().getName()
              + ", Expected class: HttpServletRequest");
    }

    HttpServletRequest request = (HttpServletRequest) input;
    BeanFactory beanFactory = getBeanFactory();
    if (!(beanFactory instanceof SpringBeanFactory)) {
      return null;
    }

    AppInvocationCustomizer appInvocationCustomizer = null;
    try{
      appInvocationCustomizer = ((SpringBeanFactory) beanFactory).context.getBean(AppInvocationCustomizer.class);
    }catch (Exception e){

    }
    if (appInvocationCustomizer == null) {
      return null;
    }
    BaseUserContext ctx = super.loadUserConext(request);
    UCInvocationContext newInvocationContext = new UCInvocationContext();
    newInvocationContext.setUserContext(ctx);
    newInvocationContext.setTargetObject(appInvocationCustomizer);
    newInvocationContext.setTargetMethod(ReflectUtil.getMethodByName(appInvocationCustomizer.getClass(), "exec"));
    newInvocationContext.setParameters(new Object[] {ctx, request});
    return newInvocationContext;
  }
}
