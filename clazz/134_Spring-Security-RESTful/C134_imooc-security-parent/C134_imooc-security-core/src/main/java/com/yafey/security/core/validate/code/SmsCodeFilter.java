package com.yafey.security.core.validate.code;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.validate.code.image.ImageCode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
// OncePerRequestFilter 是 Security 的一个工具类，来保证我们的 filter 只会被调用一次。
public class SmsCodeFilter extends OncePerRequestFilter implements InitializingBean {

	@Setter @Getter
    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    
	@Setter @Getter
    private SecurityProperties securityProperties;
    
    private Set<String> urlSet = new HashSet<>();
    
	/**
	 * Spring 验证请求url与配置的url是否匹配的工具类
	 */
	private AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    public void afterPropertiesSet() throws ServletException {
    	super.afterPropertiesSet();
    	String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCode().getSms().getUrl(), ",");
		for (String url : configUrls) {
			urlSet.add(url);
		}
		urlSet.add("/authentication/mobile");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
    		HttpServletResponse httpServletResponse, FilterChain filterChain) 
    		throws ServletException, IOException {
    	
    	boolean action = false;
    	for (String url : urlSet) {
			if(pathMatcher.match(url, request.getRequestURI())) { // 如果请求中匹配到配置中任意一个 uri,就需要校验。
				action = true;
			}
		}
    	
        if (action){
            try {
                validate(new ServletWebRequest(request));
            }catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request,httpServletResponse,e);
                return ; // 如果校验失败了，不再经过后续的过滤器。
            }
        }

        filterChain.doFilter(request,httpServletResponse);

    }

    /**
     * 校验逻辑
     * @param
     */
    public void validate(ServletWebRequest request) throws ServletRequestBindingException {

        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request,ValidateCodeProcessor.SESSION_KEY_PREFIX + "SMS");

        String codeInRequest =  ServletRequestUtils.getStringParameter(request.getRequest(),"smsCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (codeInSession == null) {
            throw new ValidateCodeException( "验证码不存在");
        }

        if (codeInSession.isExpried()) {
            sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + "SMS");
            throw new ValidateCodeException( "验证码已过期");
        }

        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }

        sessionStrategy.removeAttribute(request, ValidateCodeProcessor.SESSION_KEY_PREFIX + "SMS");

    }
}