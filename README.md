# Outout
Outout is the BEST way to figure out where to go for lunch with your crew.
Standing in front the Commerce Street Parking garage with no consensus or clue where to go?  Daniel
wants to go to Jimmy John's for the 3rd time and you think he's crazy?  Nathan
giving you the standard options?  The Southern not answering your calls for the Meat and 3 special?  Don't let any of these situations happen to you!  Use Outout to post suggestions to your lunch group and vote on a place to hit up BEFORE heading out.  Mark yourself as having lunch plans already so your co-workers aren't looking for you until
12:45 (you know who you are).

***
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

After you are finished with your solution, you may look at the refactor branch
to see my solution on separating the code.
