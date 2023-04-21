package ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ntnu.idatt2106.backend.exceptions.SaveException;
import ntnu.idatt2106.backend.model.Grocery;
import ntnu.idatt2106.backend.model.GroceryShoppingList;
import ntnu.idatt2106.backend.model.requests.SaveGroceryRequest;
import ntnu.idatt2106.backend.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shopping-list")
@RequiredArgsConstructor
@Tag(name = "ShoppingList Controller", description = "Controller to handle the shopping list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    Logger logger = LoggerFactory.getLogger(ShoppingListController.class);

    @PostMapping("/create/{refrigeratorId}")
    public ResponseEntity<Long> createShoppingList(@PathVariable(name = "refrigeratorId") long refrigeratorId) throws SaveException {
        logger.info("Received request to create shopping list for refrigerator with id {}", refrigeratorId);
        long shoppingListId = shoppingListService.createShoppingList(refrigeratorId);
        if (shoppingListId == -1) {
            logger.info("Failed to create shopping list for refrigerator id {}", refrigeratorId);
            throw new SaveException("Failed to create shopping list for refrigerator id " + refrigeratorId);
        }
        logger.info("Returning shopping list id {}", shoppingListId);
        return new ResponseEntity<>(shoppingListId, HttpStatus.OK);
    }

    @GetMapping("/groceries/{shoppingListId}")
    public ResponseEntity<List<Grocery>> getGroceriesFromShoppingList(@PathVariable(name="shoppingListId") long shoppingListId) throws NullPointerException {
        logger.info("Received request to get groceries in shopping list with id {}", shoppingListId);
        List<Grocery> groceries = shoppingListService.getGroceries(shoppingListId);
        if (groceries.isEmpty()) {
            logger.info("Received no groceries. Return status NO_CONTENT");
            throw new NullPointerException("Received no groceries");
        }
        logger.info("Returns groceries and status OK");
        return new ResponseEntity<>(groceries, HttpStatus.OK);
    }

    @PostMapping("/add-grocery/{shoppingListId}/{isRequested}")
    public ResponseEntity<GroceryShoppingList> saveGroceryToShoppingList(@RequestBody SaveGroceryRequest groceryRequest) throws SaveException{
        logger.info("Received request to save grocery {} to shopping list with id {}", groceryRequest.getName(), groceryRequest.getShoppingListId());
        Optional<GroceryShoppingList> groceryList = shoppingListService.saveGrocery(groceryRequest);
        if (groceryList.isEmpty()) {
            logger.info("No registered changes to grocery is saved");
            throw new SaveException("Failed to add a new grocery to shopping list");
        }
        logger.info("Returns groceries and status OK");
        return new ResponseEntity<>(groceryList.get(), HttpStatus.OK);
    }
}
