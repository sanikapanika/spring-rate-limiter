package com.sanikapanika.ratelimiter.support;

import com.sanikapanika.ratelimiter.Throttling;
import com.sanikapanika.ratelimiter.ThrottlingException;
import com.sanikapanika.ratelimiter.ThrottlingKey;
import com.sanikapanika.ratelimiter.service.ThrottlingEvaluator;
import com.sanikapanika.ratelimiter.service.ThrottlingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThrottlingInterceptor extends HandlerInterceptorAdapter {

    private final Log logger = LogFactory.getLog(getClass());

    private final ThrottlingEvaluator throttlingEvaluator;
    private final ThrottlingService throttlingService;

    public ThrottlingInterceptor(ThrottlingEvaluator throttlingEvaluator, ThrottlingService throttlingService) {
        this.throttlingEvaluator = throttlingEvaluator;
        this.throttlingService = throttlingService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Throttling annotation = handlerMethod.getMethod().getAnnotation(Throttling.class);

            if (annotation != null) {

                String evaluatedValue = throttlingEvaluator.evaluate(annotation, handlerMethod.getBean(), handlerMethod.getBeanType(),
                        handlerMethod.getMethod(), handlerMethod.getMethodParameters());

                ThrottlingKey key = ThrottlingKey.builder()
                        .method(handlerMethod.getMethod())
                        .annotation(annotation)
                        .evaluatedValue(evaluatedValue)
                        .build();

                boolean isHandlingAllowed = throttlingService.throttle(key, evaluatedValue);

                if (!isHandlingAllowed) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("cannot proceed with a handling http request [" + request.getRequestURI() + "] due to @Throttling configuration, type="
                                + annotation.type() + ", value=" + evaluatedValue);
                    }
                    throw new ThrottlingException();
                }

            }
        }

        return true;
    }

}
