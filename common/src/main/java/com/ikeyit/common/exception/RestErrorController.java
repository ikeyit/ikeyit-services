package com.ikeyit.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
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
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request, Locale locale) {
        HttpStatus status = getStatus(request);
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE));
        if (HttpStatus.NOT_FOUND == status) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, CommonErrorCode.NOT_FOUND, locale, attributes.get("path"));
        } else if (HttpStatus.FORBIDDEN == status) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, CommonErrorCode.FORBIDDEN, locale, attributes.get("path"));
        } else if (HttpStatus.UNAUTHORIZED == status) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, CommonErrorCode.UNAUTHORIZED, locale, attributes.get("path"));
        }

        ServletWebRequest webRequest = new ServletWebRequest(request);
        Throwable exception = errorAttributes.getError(webRequest);

        log.error("出错了：" + attributes, exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_SERVER_ERROR, locale, (Object[]) null);
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception, Locale locale){
        ErrorCode errorCode = exception.getErrorCode();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorCode, locale, exception.getParams());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException exception, Locale locale){
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
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception, Locale locale){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CommonErrorCode.INVALID_ARGUMENT, locale, new String[]{exception.getMessage()});
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(Exception exception, Locale locale) {
        log.error("出错了：", exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, CommonErrorCode.INVALID_ARGUMENT, locale, (Object[]) null);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleException(Throwable exception, Locale locale){
        log.error("出错了：", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCode.INTERNAL_SERVER_ERROR, locale, (Object[]) null);
    }


    @Override
    public String getErrorPath() {
        return PATH;
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, ErrorCode errorCode, Locale locale, Object... params) {
        String code = errorCode.getCode();
        //国际化异常message
        String message = messageSource.getMessage(messagePrefix + code, params, errorCode.getMessage(), locale);
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}