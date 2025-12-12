package beans;

import ejb.interfaces.admin.AdminInterface;
import entity.Artists;
import entity.Categories;
import entity.Events;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class SearchResultsBean implements Serializable {

    @EJB
    private AdminInterface adminBean;

    private String searchQuery;
    private List<Events> searchResults;
    private String searchType; // "all", "name", "artist", "category", "date"
    private int resultCount;

    @PostConstruct
    public void init() {
        try {
            System.out.println("========== SearchResultsBean.init() ==========");

            // Get search query from URL parameter
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            searchQuery = params.get("q");

            System.out.println("Search Query: " + searchQuery);

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                performSearch();
            } else {
                searchResults = new ArrayList<>();
            }

        } catch (Exception e) {
            e.printStackTrace();
            searchResults = new ArrayList<>();
        }
    }

    private void performSearch() {
        System.out.println("========== Performing Search ==========");
        Set<Events> results = new HashSet<>();

        try {
            // Get all events
            List<Events> allEvents = adminBean.getAllEvents();
            List<Artists> allArtists = adminBean.getAllArtists();
            List<Categories> allCategories = adminBean.getAllCategories();

            String query = searchQuery.toLowerCase().trim();

            // 1. Search by Event Name
            List<Events> eventsByName = allEvents.stream()
                    .filter(e -> e.geteName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            results.addAll(eventsByName);
            System.out.println("Found by event name: " + eventsByName.size());

            // 2. Search by Category Name
            List<Categories> matchingCategories = allCategories.stream()
                    .filter(c -> c.getcName().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            for (Categories category : matchingCategories) {
                List<Events> eventsByCategory = allEvents.stream()
                        .filter(e -> e.getcId() != null
                        && e.getcId().getcId().equals(category.getcId()))
                        .collect(Collectors.toList());
                results.addAll(eventsByCategory);
            }
            System.out.println("Found by category: " + matchingCategories.size() + " categories");

            // 3. Search by Artist Name
            List<Artists> matchingArtists = allArtists.stream()
                    .filter(a -> a.getaName().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            for (Artists artist : matchingArtists) {
                if (artist.getEventsCollection() != null) {
                    results.addAll(artist.getEventsCollection());
                }
            }
            System.out.println("Found by artist: " + matchingArtists.size() + " artists");

            // 4. Search by Date (try to parse as date)
            try {
                // Try different date formats
                LocalDate searchDate = parseDate(query);
                if (searchDate != null) {
                    List<Events> eventsByDate = allEvents.stream()
                            .filter(e -> e.geteventDate() != null
                            && e.geteventDate().equals(searchDate))
                            .collect(Collectors.toList());
                    results.addAll(eventsByDate);
                    System.out.println("Found by date: " + eventsByDate.size());
                }
            } catch (Exception e) {
                // Not a valid date, skip date search
            }

            // 5. Search by Description
            List<Events> eventsByDescription = allEvents.stream()
                    .filter(e -> e.getdescription() != null
                    && e.getdescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            results.addAll(eventsByDescription);
            System.out.println("Found by description: " + eventsByDescription.size());

            // Convert Set to List and filter out inactive events
            searchResults = results.stream()
                    .filter(e -> "active".equalsIgnoreCase(e.getstatus()))
                    .sorted((e1, e2) -> e1.geteventDate().compareTo(e2.geteventDate()))
                    .collect(Collectors.toList());

            resultCount = searchResults.size();
            System.out.println("Total unique results: " + resultCount);

        } catch (Exception e) {
            e.printStackTrace();
            searchResults = new ArrayList<>();
            resultCount = 0;
        }
    }

    private LocalDate parseDate(String dateString) {
        // Try different date formats
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDate.parse(dateString, format);
            } catch (DateTimeParseException e) {
                // Continue to next format
            }
        }
        return null;
    }

    public void performNewSearch() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            performSearch();
        }
    }

    public String viewEventDetails(Integer eventId) {
        return "event-details.jsf?faces-redirect=true&id=" + eventId;
    }

    // Getters and Setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Events> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Events> searchResults) {
        this.searchResults = searchResults;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
