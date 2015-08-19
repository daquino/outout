# Outout
Outout is the BEST way to figure out where to go for lunch with your crew.
Standing in front the Commerce Street Parking garage with no consensus or clue where to go?  Daniel
wants to go to Jimmy John's for the 3rd time and you think he's crazy?  Nathan
giving you the standard options?  The Southern not answering your calls for the Meat and 3 special?  Don't let any of these situations happen to you!  Use Outout to post suggestions to your lunch group and vote on a place to hit up BEFORE heading out.  Mark yourself as having lunch plans already so your co-workers aren't looking for you until
12:45 (you know who you are).

***

### Overview
This is project is intended to be used as a paired programming refactoring exercise.

The master branch contains the poorly written code (mostly everything in the controller).

There are 3 controllers in the application that need to have the code broken out:
-  CreateAccountController
-  AuthenticationController
-  SuggestionController

Each of these contains controller, business logic and data access code that
should be separated out into different classes.

The project is using Spring Boot with JPA/Hibernate.  The project is configured
to use the in-memory database H2.

***

### Business rules
- Account creation
  1. Username must be at least 5 characters.
  2. Password must at least be 10 characters.
  3. Username must be unique.
- Suggesting restaurants
  1. A restaurant may only be suggested once per day
  2. A user may only make 2 suggestions per day.
  3. Only an authenticated user may offer suggestions.

***

### Authentication and Authorization
The project is using [JSON Web Tokens](http://jwt.io/) for authentication and authorization.  Authentication is being handled manually by the AuthenticationController and Spring Security is handling authorization using the JWTs.  

While you can use Spring Security to abstract authentication from the application(hint), the current authentication model was purposely designed this way for this exercise.

***

### Testing
Integration tests are provided with the project to make sure any changes adhere to the above business rules.  The tests starts the application in an embedded tomcat instance before running through the test cases.  The test cases use Spring's RestTemplate to make API calls to each endpoint.

You can invoke all the integration tests with the following gradle command:

```
./gradlew integrationTest
```

You can also use your IDE to invoke the test cases with JUNIT.  Here are the following test classes to look for to run within your IDE:
- AuthenticationControllerIntegrationTest.java
- CreateAccountControllerIntegrationTest.java
- SuggestionControllerIntegrationTest.java
- IntegrationTestSuite.java
