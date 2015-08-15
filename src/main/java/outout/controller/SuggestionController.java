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
    private EntityManager em;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ResponseEntity<Void> suggest(@RequestBody RestaurantSuggestion r, Principal p) {
        Query q = em.createQuery("select s from Suggestion s where s.suggestion = :suggestion " +
                "and trunc(s.suggestedDate) = trunc(:suggestedDate)");
        q.setParameter("suggestion", r.getRestaurant());
        q.setParameter("suggestedDate", DateTime.now().toDate());
        List<Suggestion> sList = q.getResultList();
        q = em.createQuery("select s from Suggestion s where trunc(s.suggestedDate) = trunc(:suggestedDate) " +
                "and s.suggestedBy = :username");
        q.setParameter("suggestedDate", DateTime.now().toDate());
        q.setParameter("username", p.getName());
        List<Suggestion> us = q.getResultList();
        if(sList.size() == 0 && us.size() < 2) {
            Suggestion s = new Suggestion();
            s.setSuggestedBy(p.getName());
            s.setSuggestion(r.getRestaurant());
            s.setSuggestedDate(DateTime.now().toDate());
            em.persist(s);
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
        RestaurantSuggestions r = new RestaurantSuggestions();
        Query q = em.createQuery("select s from Suggestion s where trunc(s.suggestedDate) = trunc(:suggestedDate)");
        q.setParameter("suggestedDate", DateTime.now().toDate());
        List<Suggestion> sList = q.getResultList();
        r.setRestaurantSuggestions(new ArrayList<>(sList.size()));
        for(Suggestion s: sList) {
            RestaurantSuggestion rs = new RestaurantSuggestion();
            rs.setRestaurant(s.getSuggestion());
            r.getRestaurantSuggestions().add(rs);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }


}
