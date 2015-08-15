package outout.controller;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.model.Suggestion;
import outout.view.RestaurantSuggestion;
import outout.view.RestaurantSuggestions;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/suggestion")
public class SuggestionController {

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ResponseEntity<Void> suggest(@RequestBody RestaurantSuggestion restaurantSuggestion, Principal principal) {
        Query query = entityManager.createQuery("select s from Suggestion s where s.suggestion = :suggestion " +
                "and trunc(s.suggestedDate) = trunc(:suggestedDate)");
        query.setParameter("suggestion", restaurantSuggestion.getRestaurant());
        query.setParameter("suggestedDate", DateTime.now().toDate());
        List<Suggestion> suggestions = query.getResultList();
        query = entityManager.createQuery("select s from Suggestion s where trunc(s.suggestedDate) = trunc(:suggestedDate) " +
                "and s.suggestedBy = :username");
        query.setParameter("suggestedDate", DateTime.now().toDate());
        query.setParameter("username", principal.getName());
        List<Suggestion> userSuggestionsForToday = query.getResultList();
        if(suggestions.size() == 0 && userSuggestionsForToday.size() < 2) {
            Suggestion suggestion = new Suggestion();
            suggestion.setSuggestedBy(principal.getName());
            suggestion.setSuggestion(restaurantSuggestion.getRestaurant());
            suggestion.setSuggestedDate(DateTime.now().toDate());
            entityManager.persist(suggestion);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public ResponseEntity<RestaurantSuggestions> suggestionsForToday() {
        RestaurantSuggestions restaurantSuggestions = new RestaurantSuggestions();
        Query query = entityManager.createQuery("select s from Suggestion s where trunc(s.suggestedDate) = trunc(:suggestedDate)");
        query.setParameter("suggestedDate", DateTime.now().toDate());
        List<Suggestion> suggestions = query.getResultList();
        restaurantSuggestions.setRestaurantSuggestions(new ArrayList<>(suggestions.size()));
        for(Suggestion suggestion: suggestions) {
            RestaurantSuggestion restaurantSuggestion = new RestaurantSuggestion();
            restaurantSuggestion.setRestaurant(suggestion.getSuggestion());
            restaurantSuggestions.getRestaurantSuggestions().add(restaurantSuggestion);
        }
        return new ResponseEntity<>(restaurantSuggestions, HttpStatus.OK);
    }


}
