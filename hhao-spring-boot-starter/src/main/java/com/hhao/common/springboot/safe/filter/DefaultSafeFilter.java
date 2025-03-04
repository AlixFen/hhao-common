/*
 * Copyright 2018-2022 WangSheng.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hhao.common.springboot.safe.filter;

/**
 * 注入到ApiAroundAdvice
 *
 * @author Wang
 * @since 2022/1/14 16:04
 */

import com.hhao.common.exception.error.request.DecodeException;
import com.hhao.common.springboot.safe.SafeHtml;
import com.hhao.common.springboot.safe.SafeHtmlExecutor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Xss过滤处理
 * 过滤开启需要符合以下两个条件：
 * 1、开启api接口的aop;
 * 2、显式的标注@SafeHtml注解
 *
 * @author Wang
 * @since 2022 /1/14 16:15
 */
public class DefaultSafeFilter implements SafeFilter{
    protected final Logger logger = LoggerFactory.getLogger(DefaultSafeFilter.class);
    private SafeHtmlExecutor safeHtmlExecutor;

    /**
     * Instantiates a new Safe filter.
     *
     * @param safeHtmlExecutor the safe html executor
     */
    public DefaultSafeFilter(SafeHtmlExecutor safeHtmlExecutor) {
        this.safeHtmlExecutor = safeHtmlExecutor;
    }

    private SafeHtml findSafeHtmlAnnotation(Annotation[] annotations) {
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof SafeHtml) {
                return (SafeHtml) annotations[i];
            }
        }
        return null;
    }

    private boolean isContinue(SafeHtml safeHtml) {
        if (safeHtml == null) {
            return false;
        }
        if (SafeHtml.XssFilterModel.NONE.equals(safeHtml.xssFilterModel()) && !StringUtils.hasLength(safeHtml.decode())) {
            return false;
        }
        return true;
    }

    /**
     * Filter.
     *
     * @param mi the mi
     */
    @Override
    public void filter(MethodInvocation mi) {
        //获取接口类的@SafeHtml
        SafeHtml apiClassSafeHtml = mi.getMethod().getDeclaringClass().getAnnotation(SafeHtml.class);
        //获取接口方法的@SafeHtml，可能为null
        SafeHtml apiMethodSafeHtml = mi.getMethod().getAnnotation(SafeHtml.class);
        //获取接口方法参数的注解
        Annotation[][] parameterAnnotations = mi.getMethod().getParameterAnnotations();
        //获取接口参数
        Object[] args = mi.getArguments();

        //方法参数的@SafeHtml注解
        SafeHtml apiParamSafeHtml = null;
        //本次api使用的注解
        SafeHtml useSafeHtml = null;

        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            //获取方法参数的@SafeHtml注解
            apiParamSafeHtml = findSafeHtmlAnnotation(parameterAnnotations[i]);
            //优先级如下：参数注解>方法注解>类注解
            useSafeHtml = (apiParamSafeHtml != null ? apiParamSafeHtml : (apiMethodSafeHtml != null ? apiMethodSafeHtml : apiClassSafeHtml));

            //如果useSafeHtml==null，说明接口参数与方法都没有定义@SafeHtml
            //判断是否继续处理
            if (!isContinue(useSafeHtml)) {
                continue;
            }
            //以下进入参数Xss的处理
            args[i] = filterObject(obj, useSafeHtml);
        }
        ;
    }

    /**
     * 递归处理obj的@SafeHtml注解
     *
     * @param obj      the obj
     * @param safeHtml the safe html
     * @return object
     */
    protected Object filterObject(Object obj, SafeHtml safeHtml) {
        //递归到Object
        if (obj == null || safeHtml == null || obj.getClass().equals(Object.class)) {
            return obj;
        }

        //如果是字符串，直接进行处理
        if (obj.getClass().equals(String.class)) {
            return safeHtmlExecutor.filter((String) obj, safeHtml);
        }
        //List集合的处理
        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            for (int index = 0; index < list.size(); index++) {
                list.set(index, filterObject(list.get(index), safeHtml));
            }
            return obj;
        }
        //Set集合的处理
        if (obj instanceof Set) {
            Set<Object> set = (Set<Object>) obj;
            List<Object> list = new ArrayList<>();
            set.stream().forEach(item -> {
                list.add(filterObject(item, safeHtml));
            });
            set.clear();
            set.addAll(list);
            return obj;
        }
        //Map集合的处理
        if (obj instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) obj;
            map.forEach((key, item) -> {
                map.put(key, filterObject(item, safeHtml));
            });
            return obj;
        }
        //获取类的@SafeHtml注解
        SafeHtml classSafeHtml = obj.getClass().getDeclaredAnnotation(SafeHtml.class);
        //如果类级注解不存在，则继承父的注解
        classSafeHtml = classSafeHtml == null ? safeHtml : classSafeHtml;

        //类字段的@SafeHtml
        SafeHtml classFieldSafeHtml = null;
        //本次采用的注解
        SafeHtml useSafeHtml = null;

        //对类字段进行处理
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            //获取参数类字段的@SafeHtml
            classFieldSafeHtml = field.getDeclaredAnnotation(SafeHtml.class);
            //优先级：字段注解>类注解
            useSafeHtml = (classFieldSafeHtml != null ? classFieldSafeHtml : (classSafeHtml != null ? classSafeHtml : safeHtml));

            if (!isContinue(useSafeHtml)) {
                return obj;
            }
            //以下部份useSafeHtml!=null，说明一定有注解的存在，可能是字段注解，也可能是类注解
            //2种情况的处理
            //1、字段没有注解，只有类有注解，则只处理该类下String类型的字段
            //2、字段有注解，非字符串，则级联进入该类处理
            if (field.getType().equals(String.class) || classFieldSafeHtml != null) {
                try {
                    if (!field.canAccess(obj)) {
                        field.setAccessible(true);
                    }
                    field.set(obj, filterObject(field.get(obj), useSafeHtml));
                } catch (Exception e) {
                    //如果是解码错误，则抛出异常
                    if (e instanceof DecodeException){
                        throw (DecodeException)e;
                    }
                    logger.debug("decode error:" + e.getMessage());
                    continue;
                }
            }
        }
        return obj;
    }
}