package ntnu.idatt2106.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2106.backend.model.GroceryHistory;
import ntnu.idatt2106.backend.model.dto.GroceryStatisticDTO;
import ntnu.idatt2106.backend.model.grocery.RefrigeratorGrocery;
import ntnu.idatt2106.backend.repository.GroceryHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroceryHistoryService {

    private final GroceryHistoryRepository groceryHistoryRepository;

    public List<GroceryHistory> findGroceriesForMonth(int x, long refrigeratorId) {
        LocalDate dateXMonthsBack = LocalDate.now().minusMonths(x);
        LocalDate startDate = dateXMonthsBack.withDayOfMonth(1); // Get first day of month x months back
        LocalDate endDate = dateXMonthsBack.withDayOfMonth(dateXMonthsBack.lengthOfMonth()); // Get last day of month x months back
        return groceryHistoryRepository.findByDateConsumedBetweenAndRefrigeratorId(startDate, endDate, refrigeratorId);
    }
    public List<GroceryStatisticDTO> getStatsforLastYear(long refrigeratorId){
        deleteOldStatistics();
        List<GroceryStatisticDTO> stats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, yyyy");
        for(int i = 0; i < 12; i++){
            List<GroceryHistory> iMonth = findGroceriesForMonth(i, refrigeratorId);
            LocalDate dateXMonthsBack = LocalDate.now().minusMonths(i);
            String formattedDate = dateXMonthsBack.format(formatter);

            if(iMonth.size() == 0){
                stats.add(GroceryStatisticDTO.builder()
                        .foodWaste(0)
                        .foodEaten(0)
                        .monthName(formattedDate)
                        .build());
            }
            else{
                stats.add(GroceryStatisticDTO.builder()
                        .foodWaste(sumTrash(iMonth))
                        .foodEaten(sumEaten(iMonth))
                        .monthName(formattedDate)
                        .build());
            }
        }
        return stats;
    }

    public Integer sumTrash(List<GroceryHistory> list) {
        List<GroceryHistory> trash = list.stream()
                .filter(groceryHistory -> groceryHistory.isWasTrashed())
                .collect(Collectors.toList());

        Integer totalWeight = trash.stream()
                .mapToInt(GroceryHistory::getWeightInGrams)
                .sum();

        return totalWeight;
    }

    public Integer sumEaten(List<GroceryHistory> list) {
        List<GroceryHistory> eaten = list.stream()
                .filter(groceryHistory -> !groceryHistory.isWasTrashed())
                .collect(Collectors.toList());

        Integer totalWeight = eaten.stream()
                .mapToInt(GroceryHistory::getWeightInGrams)
                .sum();

        return totalWeight;
    }

    public void deleteOldStatistics() {
        LocalDate dateThreshold = LocalDate.now().minusDays(400); // Change to 365 if you want exactly 12 months
        groceryHistoryRepository.deleteByDateConsumedBefore(dateThreshold);
    }


    public void newGroceryHistory(RefrigeratorGrocery refrigeratorGrocery, boolean isTrash){
        GroceryHistory groceryHistory = GroceryHistory.builder()
                .dateConsumed(LocalDate.now())
                .refrigerator(refrigeratorGrocery.getRefrigerator())
                .wasTrashed(isTrash)
                .weightInGrams(refrigeratorGrocery.getQuantity()*refrigeratorGrocery.getUnit().getWeight())
                .build();
        groceryHistoryRepository.save(groceryHistory);
    }



}
