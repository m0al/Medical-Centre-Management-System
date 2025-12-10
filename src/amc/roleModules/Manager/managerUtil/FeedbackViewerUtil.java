// This file provides utility functions for the manager's feedback viewing screen.
package amc.roleModules.Manager.managerUtil;

// Project-specific imports for data models and data access.
import amc.dataModels.Feedback;
import amc.dataModels.User;
import amc.dataAccess.FeedbackRepository;
import amc.dataAccess.UserRepository;

// Standard Java library imports for date/time, collections, and streams.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PURPOSE: Handles the business logic for fetching, filtering, sorting, and analyzing feedback data.
 * This class acts as a helper for the feedback viewer UI to keep the display logic separate from data manipulation.
 */
public class FeedbackViewerUtil {

    // STEP 1: Declare repositories for database access and a map for user ID to name caching.
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private Map<String, String> userIdToNameMap;

    /**
     * PURPOSE: Initializes the utility by creating repository instances and loading the user map.
     * INPUTS: None.
     * OUTPUT: A new instance of FeedbackViewerUtil.
     */
    public FeedbackViewerUtil() {
        // STEP 1: Instantiate the repository for accessing feedback data.
        this.feedbackRepository = new FeedbackRepository();

        // STEP 2: Instantiate the repository for accessing user data.
        this.userRepository = new UserRepository();

        // STEP 3: Pre-load user data to avoid repeated database calls.
        loadUserIdToNameMap();
    }

    /**
     * PURPOSE: To load all users from the database and cache their IDs and names in a map.
     * INPUTS: None.
     * OUTPUT: None. Populates the userIdToNameMap.
     */
    public void loadUserIdToNameMap() {
        // STEP 1: Create a new HashMap to store the mapping of user IDs to user names.
        userIdToNameMap = new HashMap<>();

        // STEP 2: Retrieve the list of all users from the user repository.
        List<User> allUsers = userRepository.findAll();

        // STEP 3: Iterate through each user in the list.
        for (User currentUser : allUsers) {
            // STEP 4: Add an entry to the map with the user's ID as the key and their name as the value.
            userIdToNameMap.put(currentUser.getUserId(), currentUser.getName());
        }
    }

    /**
     * PURPOSE: To get the cached map of user IDs to names.
     * INPUTS: None.
     * OUTPUT: The map containing user IDs and their corresponding names.
     */
    public Map<String, String> getUserIdToNameMap() {
        // STEP 1: Return the map that links user IDs to their full names.
        return this.userIdToNameMap;
    }

    /**
     * PURPOSE: Filters and sorts the feedback list based on a search query, a selected user, and a sort option.
     * INPUTS: searchQuery (text to search for), selectedUserName (the user to filter by), sortOption (how to order results).
     * OUTPUT: A list of Feedback objects that match the criteria.
     */
    public List<Feedback> getFilteredAndSortedFeedback(String searchQuery, String selectedUserName, String sortOption) {
        // STEP 1: Retrieve all feedback entries from the repository.
        List<Feedback> allFeedbackEntries = feedbackRepository.listAll();

        // STEP 2: Filter the feedback list based on the search query and selected user.
        List<Feedback> filteredFeedbackList = allFeedbackEntries.stream()
            // STEP 2A: Keep feedback if the query is empty or if it matches the comment or the user's name.
            .filter(feedback -> searchQuery.isEmpty() ||
                           feedback.getComment().toLowerCase().contains(searchQuery.toLowerCase()) ||
                           userIdToNameMap.getOrDefault(feedback.getFromUserId(), "").toLowerCase().contains(searchQuery.toLowerCase()))
            // STEP 2B: Keep feedback if "All Users" is selected or if the feedback is from the selected user.
            .filter(feedback -> "All Users".equals(selectedUserName) ||
                           selectedUserName.equals(userIdToNameMap.get(feedback.getFromUserId())))
            // STEP 2C: Collect the results into a new list.
            .collect(Collectors.toList());

        // STEP 3: Sort the filtered list based on the chosen sort option.
        if ("Rating: Ascending".equals(sortOption)) {
            // STEP 3A: If the sort option is ascending by rating, sort it from lowest to highest.
            filteredFeedbackList.sort(Comparator.comparingInt(Feedback::getRating));
        } else if ("Rating: Descending".equals(sortOption)) {
            // STEP 3B: If the sort option is descending by rating, sort it from highest to lowest.
            filteredFeedbackList.sort(Comparator.comparingInt(Feedback::getRating).reversed());
        }

        // STEP 4: Return the fully filtered and sorted list.
        return filteredFeedbackList;
    }

    /**
     * PURPOSE: To calculate average feedback ratings for different time periods.
     * INPUTS: A list of feedback entries to analyze.
     * OUTPUT: A map containing average ratings for "today", "week", "month", and "allTime".
     */
    public Map<String, Double> calculateStatistics(List<Feedback> feedbackEntries) {
        // STEP 1: Create a map to hold the calculated statistics.
        Map<String, Double> statisticsMap = new HashMap<>();

        // STEP 2: Get the current date and calculate the start of the current week and month.
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);

        // STEP 3: Calculate and store the average rating for different periods.
        statisticsMap.put("today", calculateAverageRating(feedbackEntries, currentDate, currentDate));
        statisticsMap.put("week", calculateAverageRating(feedbackEntries, startOfWeek, currentDate));
        statisticsMap.put("month", calculateAverageRating(feedbackEntries, startOfMonth, currentDate));
        statisticsMap.put("allTime", calculateAverageRating(feedbackEntries, null, null));

        // STEP 4: Return the map with all calculated statistics.
        return statisticsMap;
    }

    /**
     * PURPOSE: A private helper method to calculate the average rating of feedback within a specific date range.
     * INPUTS: feedbackEntries (the list of feedback), startDate (the start of the range), endDate (the end of the range).
     * OUTPUT: The average rating as a double, or 0.0 if no ratings are available.
     */
    private double calculateAverageRating(List<Feedback> feedbackEntries, LocalDate startDate, LocalDate endDate) {
        // STEP 1: Define the date-time format for parsing timestamps.
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // STEP 2: Process the list of feedback to calculate the average rating.
        return feedbackEntries.stream()
            // STEP 2A: Filter feedback to include only entries within the specified date range.
            .filter(feedback -> {
                // STEP 2A-1: Ignore feedback with no timestamp.
                if (feedback.getTimestampIso() == null) {
                    return false;
                }
                // STEP 2A-2: If no date range is provided, include all feedback.
                if (startDate == null && endDate == null) {
                    return true; // This handles the "allTime" case.
                }
                // STEP 2A-3: Parse the feedback's timestamp and check if it falls within the range.
                try {
                    LocalDate feedbackDate = LocalDateTime.parse(feedback.getTimestampIso(), dateTimeFormatter).toLocalDate();
                    return !feedbackDate.isBefore(startDate) && !feedbackDate.isAfter(endDate);
                } catch (Exception e) {
                    // STEP 2A-4: If parsing fails, exclude the feedback entry.
                    return false;
                }
            })
            // STEP 2B: Extract the integer rating from each valid feedback entry.
            .mapToInt(Feedback::getRating)
            // STEP 2C: Calculate the average of all the ratings.
            .average()
            // STEP 2D: If there are no ratings, default to 0.0.
            .orElse(0.0);
    }
}