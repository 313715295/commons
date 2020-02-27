package com.jdxiaokang.commons.redis.redislock.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: redis-lock
 * @description:
 * @date 2020/2/16
 */
public class AspectExpressUtil {

    private final static ExpressionParser parser =  new SpelExpressionParser(new SpelParserConfiguration(true, true));
    private final static ParserContext parserContext = new TemplateParserContext();

    public static String getValue(String express, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable("a"+i,args[i]);
            }
        }
        Expression expression = parser.parseExpression(express, parserContext);
        return expression.getValue(context,String.class);
    }

}
