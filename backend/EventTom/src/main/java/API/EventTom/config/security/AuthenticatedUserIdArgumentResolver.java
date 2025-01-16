package API.EventTom.config.security;

import API.EventTom.exceptions.tokenException.UserNotAuthenticatedException;
import API.EventTom.models.user.UserDetailsImpl;
import jakarta.annotation.Nonnull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        parameter.getParameterAnnotation(AuthenticatedUserId.class);
        return parameter.getParameterType().equals(Long.class);
    }

    @Override
    public @Nonnull Object resolveArgument(@Nonnull MethodParameter parameter, @Nonnull ModelAndViewContainer mavContainer,
                                  @Nonnull NativeWebRequest webRequest, @Nonnull WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        throw new UserNotAuthenticatedException("User not authenticated");

    }
}
