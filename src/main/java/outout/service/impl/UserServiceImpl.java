package outout.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import outout.service.UserService;
import outout.view.AccountCredentials;
import outout.model.User;
import outout.repository.UserRepository;
import outout.view.AccountCreationResult;
import outout.view.AuthenticationToken;
import outout.validator.AccountCredentialsValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Value("${token.secret}")
  private String ts;

  @Transactional
  public AccountCreationResult createUser(AccountCredentials accountCredentials) {
    AccountCreationResult acr = new AccountCreationResult();
    User user = new User();
    user.setUsername(accountCredentials.getUsername());
    user.setPassword(encoder.encode(accountCredentials.getPassword()));
    User checkUser = this.userRepository.findByUsername(user.getUsername());

    if (AccountCredentialsValidator.checkErrors(accountCredentials, checkUser) == null) {
      user = this.userRepository.save(user);
      acr.setSuccessful(true);
    } else {
      acr.setErrors(AccountCredentialsValidator.checkErrors(accountCredentials, checkUser));
    }

    return acr;
  }

  public AuthenticationToken authenticate(AccountCredentials accountCredentials) {
    User user = this.userRepository.findByUsername(accountCredentials.getUsername());
    if (user != null && encoder.matches(accountCredentials.getPassword(), user.getPassword())) {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS512, ts)
                .setSubject(accountCredentials.getUsername())
                .compact();
        authenticationToken.setToken(jwt);
        return authenticationToken;
    } else {
      return null;
    }
  }

}
