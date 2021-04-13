package com.ikeyit.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Map;

/**
 * 统一异常处理
 */
@RestController
@RestControllerAdvice
public class RestErrorController extends AbstractErrorController {

    private static final Logger log = LoggerFactory.getLogger(RestErrorController.class);

    private static final String PATH = "/error";

    MessageSource messageSource;

    ErrorAttributes errorAttributes;

    String messagePrefix = "error.";

    public RestErrorController(ErrorAttributes errorAttributes, MessageSource messageSource) {
        super(errorAttributes);
        this.errorAttributes = errorAttributes;
        this.messageSource = messageSource;
    }

    /**
     * 当filter内部或controller方法被调用之前发生的异常，在这里处理！ 例如NOT_FOUND或者INTERNAL_SERVER_ERROR
     *
     * @param request
     * @return
     */
    @RequestMapping(PATH)
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Throwable exception = errorAttributes.getError(webRequest);
        if (exception != null) {
            log.error("服务器出现异常，潜在BUG", exception);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        HttpStatus status = getStatus(request);
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE));
        String path = (String) attributes.get("path");
        String message = (String) attributes.get("message");
        if (HttpStatus.NOT_FOUND == status) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, CommonErrorCode.NOT_FOUND, new Object[]{path});
        } else if (HttpStatus.FORBIDDEN == status) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, CommonErrorCode.FORBIDDEN,  new Object[]{message});
        } else if (HttpStatus.UNAUTHORIZED == status) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, CommonErrorCode.UNAUTHORIZED,  new Object[]{message});
        }

        log.error("未处理错误:{}", message);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception){
        ErrorCode errorCode = exception.getErrorCode();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorCode, exception.getParams());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException exception){

        ErrorResponse errorResponse =  new ErrorResponse(exception.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getStatus()));
    }

    /**
     * 转换Validation Exception
     * @param exception
     * @param locale
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CommonErrorCode.INVALID_ARGUMENT, new Object[]{exception.getMessage()});
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(Exception exception) {
        log.error("出错了：", exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CommonErrorCode.INVALID_ARGUMENT);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleException(Throwable exception){
        log.error("出错了：", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_SERVER_ERROR);
    }


    @Override
    public String getErrorPath() {
        return PATH;
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, ErrorCode errorCode) {
        return buildErrorResponse(httpStatus, errorCode, null, null);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, ErrorCode errorCode, Object[] params) {
        return buildErrorResponse(httpStatus, errorCode, params, null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, ErrorCode errorCode, Object[] params, Locale locale) {
        String code = errorCode.getCode();
        if (locale == null)
            locale = LocaleContextHolder.getLocale();
        //国际化异常message
        String message = messageSource.getMessage(messagePrefix + code, params, errorCode.getMessage(), locale);
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}