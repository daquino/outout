# outout
Demo application for refactor/clean code paired programming session using Spring Boot.
This is project is intended to be used as a paired programming refactoring exercise.

The master branch contains the poorly written code (mostly everything in the controller).

There are 3 controllers in the application that need to have the code broken out:
1.  CreateAccountController
2.  AuthenticationController
3.  SuggestionController

Each of these contains controller, business logic and data access code that
should be separated out into different classes.

The project is using Spring Boot with JPA/Hibernate.  The project is configured
to use the in-memory database H2.

After you are finished with your solution, you may look at the refactor branch
to see my solution on separating the code.
