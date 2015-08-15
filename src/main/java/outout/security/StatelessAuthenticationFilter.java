package outout.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthenticationFilter extends GenericFilterBean {

    public String tokenSecret;
    private SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    public StatelessAuthenticationFilter(final String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("X-AUTH-TOKEN");
        Authentication authentication;
        System.out.println("Checking JWT...");
        try {
            Jws<Claims> jsonWebToken = Jwts.parser()
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(token);
            String username = jsonWebToken.getBody().getSubject();
            String password = jsonWebToken.getSignature();
            System.out.println(String.format("Username = %s, Password/Signature = %s", username, password));
            authentication = new UserAuthentication(username);
        }
        catch(Exception exc) {
            authentication = null;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
