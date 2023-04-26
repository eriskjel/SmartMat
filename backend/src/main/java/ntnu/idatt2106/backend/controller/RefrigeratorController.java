package ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ntnu.idatt2106.backend.exceptions.LastSuperuserException;
import ntnu.idatt2106.backend.exceptions.RefrigeratorNotFoundException;
import ntnu.idatt2106.backend.exceptions.SaveException;
import ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import ntnu.idatt2106.backend.model.Refrigerator;
import ntnu.idatt2106.backend.model.dto.RefrigeratorDTO;
import ntnu.idatt2106.backend.model.dto.response.SuccessResponse;
import ntnu.idatt2106.backend.model.requests.MemberRequest;
import ntnu.idatt2106.backend.model.requests.RefrigeratorRequest;
import ntnu.idatt2106.backend.model.dto.MemberDTO;
import ntnu.idatt2106.backend.model.requests.RemoveMemberRequest;
import ntnu.idatt2106.backend.service.RefrigeratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for API calls regarding refrigerator and its members
 *
 * TODO change to fetch email from cookie
 * TODO usae global exception handlers
 */
@RestController
@RequestMapping("/api/refrigerator")
@RequiredArgsConstructor
@Tag(name = "Refrigerator Controller", description = "Controller to handle the refrigerator")
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    Logger logger = LoggerFactory.getLogger(RefrigeratorController.class);

    @PostMapping("/members/edit-role")
    public ResponseEntity<MemberDTO> editRole(@Valid @RequestBody MemberRequest memberRequest) {
        logger.info("Received request to edit member role in refrigerator");
        MemberDTO result;
        try {
            result = refrigeratorService.setRole(memberRequest);
            if (result == null) throw new Exception();
        } catch (Exception e) {
            logger.error("Could not edit role");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Returning response");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/members/new")
    public ResponseEntity<MemberDTO> newMember(@Valid @RequestBody MemberRequest memberRequest) throws SaveException {
        logger.info("Received request to add new member to refrigerator");
        MemberDTO result;
        try {
            result = refrigeratorService.addMember(memberRequest);
            if (result == null) throw new Exception();
        } catch (Exception e) {
            throw new SaveException("Failed to add member");
        }
        logger.info("Returning response");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/members/remove")
    public ResponseEntity<SuccessResponse> removeMember(@Valid @RequestBody RemoveMemberRequest memberRequest) {
        logger.info("Received request to remove member from refrigerator");
        try{
            refrigeratorService.removeUserFromRefrigerator(memberRequest);
            logger.info("Member removed successfully");
            return new ResponseEntity<>(new SuccessResponse("Member removed successfully", HttpStatus.OK.value()), HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (LastSuperuserException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<Refrigerator> newRefrigerator(@Valid @RequestBody RefrigeratorRequest refrigerator) throws SaveException {
        logger.info("Received request to create refrigerator for refrigerator");
        Refrigerator result;
        try {
            result = refrigeratorService.save(refrigerator);
            if (result == null) throw new Exception();
        } catch (Exception e) {
            throw new SaveException("Failed to create refrigerator");
        }
        logger.info("Returning refrigerator with id {}", result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{refrigeratorId}/{username}")
    public ResponseEntity<SuccessResponse> deleteRefrigerator(@Valid @PathVariable int refrigeratorId, @PathVariable String username) {
        try {
            refrigeratorService.forceDeleteRefrigerator(username,refrigeratorId);
            logger.info("Member removed successfully");
            return new ResponseEntity<>(new SuccessResponse("Member removed successfully", HttpStatus.OK.value()), HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Refrigerator>> getAllByUser(@Valid @PathVariable String userName){
        logger.info("Received request for all refrigerators by user");
        try {
            List<Refrigerator> result = refrigeratorService.getAllByUser(userName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<RefrigeratorDTO> getById(@Valid @PathVariable long refrigeratorId) {
        logger.info("Received request for refrigerator with id: {}", refrigeratorId);
        try {
            RefrigeratorDTO result = refrigeratorService.getRefrigeratorDTOById(refrigeratorId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (EntityNotFoundException | RefrigeratorNotFoundException e) {
            logger.warn("Refrigerator could not be found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
