package beans;

import entity.Categories;
import entity.Events;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import record.KeepRecord;

@Named("homeBean")
@RequestScoped
public class HomeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "https://localhost:8181/EventEase/api/";

    @Inject
    private KeepRecord keepRecord;

    private List<Categories> categories;
    private List<Events> eventBanners;
    private List<Events> upcomingEvents;
    private String searchQuery;
    private MenuModel userMenuModel;

    @PostConstruct
    public void init() {
        System.out.println("========== HOME BEAN INIT ==========");

        loadCategories();
        System.out.println("Categories loaded: " + (categories != null ? categories.size() : 0));
        if (categories != null && !categories.isEmpty()) {
            for (Categories cat : categories) {
                System.out.println("Category: " + cat.getcName() + " | Image: " + cat.getcImg());
            }
        }

        loadEventBanners();
        System.out.println("Event banners loaded: " + (eventBanners != null ? eventBanners.size() : 0));
        if (eventBanners != null && !eventBanners.isEmpty()) {
            for (Events event : eventBanners) {
                System.out.println("Event: " + event.geteName() + " | Banner: " + event.getbannerImg());
            }
        }

        loadUpcomingEvents();
        System.out.println("Upcoming events loaded: " + (upcomingEvents != null ? upcomingEvents.size() : 0));

        createUserMenu();
    }

    private void loadCategories() {
        Client client = ClientBuilder.newClient();
        try {
            String token = keepRecord != null ? keepRecord.getToken() : null;
            System.out.println("Loading categories with token: " + (token != null ? "EXISTS" : "NULL"));

            WebTarget target = client.target(BASE_URL)
                    .path("admin/category/getAllCategories");

            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            System.out.println("Categories API Response Status: " + response.getStatus());

            if (response.getStatus() == 200) {
                categories = response.readEntity(new GenericType<List<Categories>>() {
                });
                if (categories == null) {
                    categories = new ArrayList<>();
                }
            } else {
                System.err.println("Failed to load categories. Status: " + response.getStatus());
                categories = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("ERROR loading categories:");
            e.printStackTrace();
            categories = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    private void loadEventBanners() {
        Client client = ClientBuilder.newClient();
        try {
            String token = keepRecord != null ? keepRecord.getToken() : null;
            System.out.println("Loading event banners with token: " + (token != null ? "EXISTS" : "NULL"));

            WebTarget target = client.target(BASE_URL)
                    .path("admin/event/upcoming");

            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            System.out.println("Event Banners API Response Status: " + response.getStatus());

            if (response.getStatus() == 200) {
                eventBanners = response.readEntity(new GenericType<List<Events>>() {
                });
                if (eventBanners == null) {
                    eventBanners = new ArrayList<>();
                }
            } else {
                System.err.println("Failed to load event banners. Status: " + response.getStatus());
                eventBanners = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("ERROR loading event banners:");
            e.printStackTrace();
            eventBanners = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    private void loadUpcomingEvents() {
        Client client = ClientBuilder.newClient();
        try {
            String token = keepRecord != null ? keepRecord.getToken() : null;
            System.out.println("Loading upcoming events with token: " + (token != null ? "EXISTS" : "NULL"));

            WebTarget target = client.target(BASE_URL)
                    .path("admin/event/upcoming");

            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            System.out.println("Upcoming Events API Response Status: " + response.getStatus());

            if (response.getStatus() == 200) {
                upcomingEvents = response.readEntity(new GenericType<List<Events>>() {
                });
                if (upcomingEvents == null) {
                    upcomingEvents = new ArrayList<>();
                }

                if (upcomingEvents.size() > 6) {
                    upcomingEvents = upcomingEvents.subList(0, 5);
                }
            } else {
                System.err.println("Failed to load upcoming events. Status: " + response.getStatus());
                upcomingEvents = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("ERROR loading upcoming events:");
            e.printStackTrace();
            upcomingEvents = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    private void createUserMenu() {
        userMenuModel = new DefaultMenuModel();

        DefaultMenuItem profile = DefaultMenuItem.builder()
                .value("Profile")
                .icon("pi pi-user")
                .command("#{homeBean.goToProfile}")
                .build();

        DefaultMenuItem bookings = DefaultMenuItem.builder()
                .value("My Bookings")
                .icon("pi pi-ticket")
                .command("#{homeBean.goToBookings}")
                .build();

        DefaultMenuItem interests = DefaultMenuItem.builder()
                .value("Events Interested")
                .icon("pi pi-thumbs-up")
                .command("#{homeBean.goToInterests}")
                .build();
        
        DefaultMenuItem wishlists = DefaultMenuItem.builder()
                .value("Events Wishlisted")
                .icon("pi pi-heart-fill")
                .command("#{homeBean.goToWishlists}")
                .build();

        DefaultMenuItem logout = DefaultMenuItem.builder()
                .value("Logout")
                .icon("pi pi-sign-out")
                .command("#{homeBean.logout}")
                .build();

        userMenuModel.getElements().add(profile);
        userMenuModel.getElements().add(bookings); 
        userMenuModel.getElements().add(interests);
        userMenuModel.getElements().add(wishlists);
        userMenuModel.getElements().add(logout);
    }

    public String viewCategoryEvents(Integer categoryId) {
        return "category-events.jsf?faces-redirect=true&cId=" + categoryId;
    }

    public String viewEventDetails(Integer eventId) {
        return "event-details.jsf?faces-redirect=true&eventId=" + eventId;
    }

    public void goToProfile() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("profile.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToBookings() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("my-bookings.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToInterests() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("my-interests.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void goToWishlists() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("my-wishlists.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    public void logout() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("/Logout.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchEvents() {
        try {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("search-results.jsf?q=" + searchQuery);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    public List<Events> getEventBanners() {
        return eventBanners;
    }

    public void setEventBanners(List<Events> eventBanners) {
        this.eventBanners = eventBanners;
    }

    public List<Events> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(List<Events> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public MenuModel getUserMenuModel() {
        return userMenuModel;
    }

    public void setUserMenuModel(MenuModel userMenuModel) {
        this.userMenuModel = userMenuModel;
    }
}
