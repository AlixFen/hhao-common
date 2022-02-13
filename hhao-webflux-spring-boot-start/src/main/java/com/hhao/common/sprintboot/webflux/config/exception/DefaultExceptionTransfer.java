
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

package com.hhao.common.sprintboot.webflux.config.exception;

import com.hhao.common.springboot.exception.ExceptionTransfer;
import com.hhao.common.springboot.exception.entity.request.ValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * 异常类的转换
 *
 * @author Wang
 * @since 2022/1/14 11:04
 */
public class DefaultExceptionTransfer implements ExceptionTransfer {
    protected final Logger logger = LoggerFactory.getLogger(DefaultExceptionTransfer.class);
    @Override
    public boolean support(Throwable exception) {
//        if (exception instanceof MethodArgumentTypeMismatchException
//                || exception instanceof HttpRequestMethodNotSupportedException
//                || exception instanceof HttpMediaTypeNotSupportedException
//                || exception instanceof HttpMediaTypeNotAcceptableException
//                || exception instanceof MissingPathVariableException
//                || exception instanceof MissingServletRequestParameterException
//                || exception instanceof ServletRequestBindingException
//                || exception instanceof ConversionNotSupportedException
//                || exception instanceof TypeMismatchException
//                || exception instanceof HttpMessageNotReadableException
//                || exception instanceof HttpMessageNotWritableException
//                || exception instanceof MethodArgumentNotValidException
//                || exception instanceof MissingServletRequestPartException
//                || exception instanceof BindingResult
//                || exception instanceof AsyncRequestTimeoutException
//                || exception instanceof IOException
//                || exception instanceof HttpMessageConversionException){
//            return true;
//        }

        if (exception instanceof WebExchangeBindException) {
            return true;
        }
        logger.debug("don't support exception:" + exception.getClass());
        return false;
    }

    @Override
    public Throwable transfer(Throwable exception) {
        if (exception instanceof WebExchangeBindException) {
            //对验证类异常作转换处理
            exception = new ValidateException(((WebExchangeBindException) exception).getBindingResult());
        }
//        if (exception instanceof MethodArgumentTypeMismatchException){
//            //对请求参数缺失错误做转换处理
//            MethodArgumentTypeMismatchException ex=(MethodArgumentTypeMismatchException) exception;
//            if (ex.getRootCause()!=null && ex.getRootCause() instanceof DateTimeParseException){
//                exception=new DateTimeConvertException();
//            }else{
//                exception=new RequestException(exception);
//            }
//        }else if (exception instanceof HttpRequestMethodNotSupportedException) {
//            exception=new RequestException(ErrorInfos.ERROR_405,exception);
//        } else if (exception instanceof HttpMediaTypeNotSupportedException) {
//            exception=new RequestException(ErrorInfos.ERROR_415,exception);
//        }else if (exception instanceof HttpMediaTypeNotAcceptableException) {
//            exception=new RequestException(ErrorInfos.ERROR_406,exception);
//        }else if (exception instanceof MissingPathVariableException) {
//            exception=new ServerException(ErrorInfos.ERROR_500,exception);
//        } else if (exception instanceof MissingServletRequestParameterException) {
//            exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//        }else if (exception instanceof ServletRequestBindingException) {
//            exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//        }else if (exception instanceof ConversionNotSupportedException) {
//            exception=new ServerException(ErrorInfos.ERROR_500,exception);
//        }else if (exception instanceof TypeMismatchException) {
//            exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//        }else if (exception instanceof HttpMessageNotReadableException){
//            //对Spring Convert日期转化错误的处理
//            HttpMessageNotReadableException ex=(HttpMessageNotReadableException)exception;
//            if ( ex.getRootCause()!=null && ex.getRootCause() instanceof DateTimeParseException){
//                exception=new DateTimeConvertException();
//            }else{
//                exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//            }
//        }else if (exception instanceof HttpMessageNotWritableException) {
//            exception=new ServerException(ErrorInfos.ERROR_500,exception);
//        }else if (exception instanceof MethodArgumentNotValidException) {
//            //对验证类异常作转换处理
//            exception=new ValidateException( ((MethodArgumentNotValidException) exception).getBindingResult());
//        }else if (exception instanceof MissingServletRequestPartException) {
//            exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//        }if (exception instanceof BindingResult) {
//            //对验证类异常作转换处理
//            exception=new ValidateException((BindingResult)exception);
//        }else if (exception instanceof AsyncRequestTimeoutException) {
//            exception=new ServerException(ErrorInfos.ERROR_503,exception);
//        }else if (exception instanceof IOException) {
//            exception=new SystemException(ErrorInfos.ERROR_500_IO,exception);
//        }else if (exception instanceof HttpMessageConversionException){
//            exception=new RequestException(ErrorInfos.ERROR_40X,exception);
//        }
        return exception;
    }
}
