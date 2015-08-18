package outout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.service.AuthenticationServiceImpl;
import outout.service.UserService;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

@Controller
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthenticationToken> authenticate(@RequestBody AccountCredentials credentials) {
        if(authenticationService.isAuthenticated(credentials, userService.findUserFromCredentials(credentials))) {
            return authenticationService.createSuccessfulAuthenticationResponse(credentials);
        }
        else {
            return authenticationService.createFailedAuthenticationResponse();
        }
    }
}
