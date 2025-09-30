package com.yushan.backend.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Custom Method Security Expression Handler
 * 
 * This class provides custom expression evaluation for method-level security
 * by using our custom SecurityExpressionRoot
 */
public class CustomMethodSecurityExpressionHandler implements MethodSecurityExpressionHandler, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public EvaluationContext createEvaluationContext(Authentication authentication, MethodInvocation mi) {
        SecurityExpressionRoot root = new SecurityExpressionRoot(authentication);
        root.setThis(mi.getThis());
        
        // Create evaluation context with our custom root
        org.springframework.expression.spel.support.StandardEvaluationContext context =
            new org.springframework.expression.spel.support.StandardEvaluationContext(root);

        // Enable @bean references in SpEL (e.g., @novelGuard)
        if (beanFactory != null) {
            context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        
        // Set method arguments for SpEL parameter resolution
        Object[] args = mi.getArguments();
        String[] paramNames = getParameterNames(mi);
        
        for (int i = 0; i < args.length && i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        
        return context;
    }
    
    private String[] getParameterNames(MethodInvocation mi) {
        // Get actual parameter names from method signature
        java.lang.reflect.Method method = mi.getMethod();
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        String[] paramNames = new String[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            paramNames[i] = parameters[i].getName();
        }
        
        return paramNames;
    }

    public MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation mi) {
        SecurityExpressionRoot root = new SecurityExpressionRoot(authentication);
        root.setThis(mi.getThis());
        return root;
    }

    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        Object rootObject = ctx.getRootObject().getValue();
        if (rootObject instanceof SecurityExpressionRoot) {
            ((SecurityExpressionRoot) rootObject).setReturnObject(returnObject);
        }
    }

    public void setFilterObject(Object filterObject, EvaluationContext ctx) {
        Object rootObject = ctx.getRootObject().getValue();
        if (rootObject instanceof SecurityExpressionRoot) {
            ((SecurityExpressionRoot) rootObject).setFilterObject(filterObject);
        }
    }

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
        return filterTarget;
    }

    @Override
    public org.springframework.expression.ExpressionParser getExpressionParser() {
        return new org.springframework.expression.spel.standard.SpelExpressionParser();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
