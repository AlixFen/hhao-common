/*
 * Copyright 2018-2021 WangSheng.
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

package com.hhao.common.springboot.convert;

import com.hhao.common.metadata.Mdm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Local date time and string converter.
 *
 * @author Wang
 * @since 1.0.0
 */
public class LocalDateTimeAndStringConverter implements ConditionalGenericConverter {
    protected final Logger logger = LoggerFactory.getLogger(LocalDateTimeAndStringConverter.class);
    private Boolean dataTimeErrorThrow=false;

    public LocalDateTimeAndStringConverter(Boolean dataTimeErrorThrow){
        this.dataTimeErrorThrow=dataTimeErrorThrow;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType().equals(String.class) && targetType.getType().equals(LocalDateTime.class)) {
            return true;
        } else if (sourceType.getType().equals(LocalDateTime.class) && targetType.getType().equals(String.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> pairs = new HashSet<ConvertiblePair>();
        //注册可支持源类型为String，目标类型为LocalDateTime的转换
        pairs.add(new ConvertiblePair(String.class, LocalDateTime.class));
        //注册可支持源类型为LocalDateTime，目标类型为String的转换
        pairs.add(new ConvertiblePair(LocalDateTime.class, String.class));
        return pairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source==null){
            return null;
        }
        //String<->LocalDateTime的转换
        if (sourceType.getType().equals(String.class) && targetType.getType().equals(LocalDateTime.class)) {
            String str = (String) source;
            if (StringUtils.hasLength(str)) {
                try {
                    return LocalDateTime.parse(str, Mdm.DATE_TIME_FORMATTER.value(DateTimeFormatter.class));
                }catch (Exception e){
                    if (dataTimeErrorThrow){
                        throw new RuntimeException(e);
                    }
                    logger.debug(e.getMessage());;
                }
            }
        } else {
            LocalDateTime dateTime = (LocalDateTime) source;
            return dateTime.format(Mdm.DATE_TIME_FORMATTER.value(DateTimeFormatter.class));
        }

        return null;
    }
}
