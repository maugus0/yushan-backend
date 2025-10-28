package com.yushan.backend.security;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomMethodSecurityExpressionHandler Tests")
class CustomMethodSecurityExpressionHandlerTest {

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private Authentication authentication;

    @Mock
    private MethodInvocation methodInvocation;

    @Test
    @DisplayName("Test createEvaluationContext")
    void testCreateEvaluationContext() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        handler.setBeanFactory(beanFactory);
        
        // Create a mock method invocation
        Method testMethod = String.class.getMethod("toString");
        when(methodInvocation.getMethod()).thenReturn(testMethod);
        when(methodInvocation.getThis()).thenReturn("test");
        when(methodInvocation.getArguments()).thenReturn(new Object[]{});
        
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        
        assertNotNull(context);
        assertNotNull(context.getRootObject().getValue());
    }

    @Test
    @DisplayName("Test createSecurityExpressionRoot")
    void testCreateSecurityExpressionRoot() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        Method testMethod = String.class.getMethod("toString");
        lenient().when(methodInvocation.getMethod()).thenReturn(testMethod);
        lenient().when(methodInvocation.getThis()).thenReturn("test");
        
        var root = handler.createSecurityExpressionRoot(authentication, methodInvocation);
        
        assertNotNull(root);
        assertTrue(root instanceof SecurityExpressionRoot);
    }

    @Test
    @DisplayName("Test setReturnObject")
    void testSetReturnObject() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        Method testMethod = String.class.getMethod("toString");
        when(methodInvocation.getMethod()).thenReturn(testMethod);
        when(methodInvocation.getThis()).thenReturn("test");
        when(methodInvocation.getArguments()).thenReturn(new Object[]{});
        
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        Object returnObject = "returnValue";
        
        assertDoesNotThrow(() -> {
            handler.setReturnObject(returnObject, context);
        });
    }

    @Test
    @DisplayName("Test setFilterObject")
    void testSetFilterObject() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        Method testMethod = String.class.getMethod("toString");
        when(methodInvocation.getMethod()).thenReturn(testMethod);
        when(methodInvocation.getThis()).thenReturn("test");
        when(methodInvocation.getArguments()).thenReturn(new Object[]{});
        
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        Object filterObject = "filterValue";
        
        assertDoesNotThrow(() -> {
            handler.setFilterObject(filterObject, context);
        });
    }

    @Test
    @DisplayName("Test filter")
    void testFilter() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        Method testMethod = String.class.getMethod("toString");
        when(methodInvocation.getMethod()).thenReturn(testMethod);
        when(methodInvocation.getThis()).thenReturn("test");
        when(methodInvocation.getArguments()).thenReturn(new Object[]{});
        
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        Expression filterExpression = mock(Expression.class);
        Object filterTarget = "target";
        
        Object result = handler.filter(filterTarget, filterExpression, context);
        
        assertEquals(filterTarget, result);
    }

    @Test
    @DisplayName("Test getExpressionParser")
    void testGetExpressionParser() {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        var parser = handler.getExpressionParser();
        
        assertNotNull(parser);
    }

    @Test
    @DisplayName("Test setBeanFactory")
    void testSetBeanFactory() {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        
        assertDoesNotThrow(() -> {
            handler.setBeanFactory(beanFactory);
        });
    }

    @Test
    @DisplayName("Test createEvaluationContext with parameters")
    void testCreateEvaluationContextWithParameters() throws Exception {
        CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
        handler.setBeanFactory(beanFactory);
        
        Method testMethod = String.class.getMethod("substring", int.class);
        when(methodInvocation.getMethod()).thenReturn(testMethod);
        when(methodInvocation.getThis()).thenReturn("test");
        when(methodInvocation.getArguments()).thenReturn(new Object[]{1});
        
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        
        assertNotNull(context);
    }
}

