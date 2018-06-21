package org.collectiveone.modules.assignations;

import java.util.List;
import java.util.UUID;

import org.collectiveone.common.BaseController;
import org.collectiveone.common.dto.GetResult;
import org.collectiveone.common.dto.PostResult;
import org.collectiveone.modules.assignations.dto.AssignationDto;
import org.collectiveone.modules.assignations.dto.EvaluationDto;
import org.collectiveone.modules.governance.DecisionVerdict;
import org.collectiveone.modules.governance.GovernanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/1")
public class AssignationController extends BaseController {
	
	@Autowired
	private AssignationService assignationService;
	
	@Autowired
	private GovernanceService governanceService;

	
	@RequestMapping(path = "/initiative/{initiativeId}/assignation", method = RequestMethod.POST)
	public PostResult newAssignation(@PathVariable("initiativeId") String initiativeId, @RequestBody AssignationDto assignation) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		DecisionVerdict canCreate = governanceService.canCreateAssignation(UUID.fromString(initiativeId), getLoggedUser().getC1Id());
		
		if (canCreate == DecisionVerdict.DENIED) {
			return new PostResult("error", "creation of assignation not authorized",  "");
		}
		
		return assignationService.createAssignation(UUID.fromString(initiativeId), assignation, getLoggedUser().getC1Id());
	} 
	
	@RequestMapping(path = "/initiative/{initiativeId}/assignations", method = RequestMethod.GET)
	public GetResult<List<AssignationDto>> getAssignations(
			@PathVariable("initiativeId") String initiativeIdStr,
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size,
			@RequestParam("sortDirection") String sortDirection,
			@RequestParam("sortProperty") String sortProperty) {
		
		UUID initiativeId = UUID.fromString(initiativeIdStr);
		
		// ##### can you help me to find initiativeService alternative
		if (!initiativeService.canAccess(initiativeId, getLoggedUserId())) {
			return new GetResult<List<AssignationDto>>("error", "access denied", null);
		}
		
		Sort sort = new Sort(Sort.Direction.valueOf(sortDirection), sortProperty);
		
		return assignationService.getAssignationsOfInitiative(initiativeId, getLoggedUserId(), new PageRequest(page, size, sort));
	}
	
	@RequestMapping(path = "/initiative/{initiativeId}/assignationsOfSubinitiatives", method = RequestMethod.GET)
	public GetResult<List<AssignationDto>> getAssignationsOfSubinitiatives(
			@PathVariable("initiativeId") String initiativeIdStr,
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size,
			@RequestParam("sortDirection") String sortDirection,
			@RequestParam("sortProperty") String sortProperty ) {
		
		UUID initiativeId = UUID.fromString(initiativeIdStr);
		
		// #### delete or change to new version of canAccess?
		if (!initiativeService.canAccess(initiativeId, getLoggedUserId())) {
			return new GetResult<List<AssignationDto>>("error", "access denied", null);
		}
		
		Sort sort = new Sort(Sort.Direction.valueOf(sortDirection), sortProperty);
		
		return assignationService.getAssignationsOfSubinitiatives(initiativeId, getLoggedUserId(), new PageRequest(page, size, sort));
	}
	
	@RequestMapping(path = "/assignation/{assignationId}", method = RequestMethod.GET)
	public GetResult<AssignationDto> getAssignationOfUser(
			@PathVariable("assignationId") String assignationIdStr, 
			@RequestParam(defaultValue = "false") Boolean  addAllEvaluations) {
		
		UUID assignationId = UUID.fromString(assignationIdStr); 
		UUID initiativeId = assignationService.findInitiativeId(assignationId);
		
		// ##### what is initiativeService altrenative
		if (!modelSectionService.canAccess(initiativeId, getLoggedUserId())) {
			return new GetResult<AssignationDto>("error", "access denied", null);
		}
		
		return assignationService.getAssignationDto(assignationId, getLoggedUserId(), addAllEvaluations);
	}
	
	@RequestMapping(path = "/assignation/{assignationId}/open", method = RequestMethod.PUT)
	public PostResult openAssignation(
			@PathVariable("assignationId") String assignationIdStr) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		UUID assignationId = UUID.fromString(assignationIdStr); 
		UUID initiativeId = assignationService.findInitiativeId(assignationId);
		
		if (governanceService.canCreateAssignation(initiativeId, getLoggedUserId()) == DecisionVerdict.DENIED) {
			return new PostResult("error", "creation of assignation not authorized",  "");
		}
		
		PostResult result = assignationService.openAssignation(assignationId);
		
		return result;
	}
	
	@RequestMapping(path = "/assignation/{assignationId}/evaluate", method = RequestMethod.POST)
	public PostResult evaluateAssignation(
			@PathVariable("assignationId") String assignationId,
			@RequestBody EvaluationDto evaluationDto) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		PostResult result = assignationService.evaluateAndUpdateAssignation(getLoggedUser().getC1Id(), UUID.fromString(assignationId), evaluationDto);
		
		return result;
	}
	
	@RequestMapping(path = "/assignation/{assignationId}/revert", method = RequestMethod.PUT)
	public PostResult revertAssignation(
			@PathVariable("assignationId") String assignationId) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		// ##### i am confused here, getInitiativeIdOf was removed but hrere can you help writing
		DecisionVerdict canRevert = governanceService.canRevertAssignation(assignationService.getInitiativeIdOf(UUID.fromString(assignationId)), getLoggedUser().getC1Id());
		
		if (canRevert == DecisionVerdict.DENIED) {
			return new PostResult("error", "revert of assignation not authorized",  "");
		}
		
		PostResult result = assignationService.revertAssignation(UUID.fromString(assignationId), getLoggedUser().getC1Id());
		
		return result;
	}
	
	@RequestMapping(path = "/assignation/{assignationId}/approveRevert", method = RequestMethod.PUT)
	public PostResult approveRevertAssignation(
			@PathVariable("assignationId") String assignationId, 
			@RequestParam Boolean approveFlag) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		PostResult result = assignationService.approveRevertAssignation(getLoggedUser().getC1Id(), UUID.fromString(assignationId), approveFlag);
		
		/* update assignation state in case all receivers have approved */
		assignationService.checkRevertStatus(UUID.fromString(assignationId));
		
		return result;
	}
	
	@RequestMapping(path = "/assignation/{assignationId}/delete", method = RequestMethod.PUT)
	public PostResult deleteAssignation(
			@PathVariable("assignationId") String assignationId) {
		
		if (getLoggedUser() == null) {
			return new PostResult("error", "endpoint enabled users only", null);
		}
		
		//##### here to 
		DecisionVerdict canDelete = governanceService.canDeleteAssignation(assignationService.getInitiativeIdOf(UUID.fromString(assignationId)), getLoggedUser().getC1Id());
		
		if (canDelete == DecisionVerdict.DENIED) {
			return new PostResult("error", "delete of assignation not authorized",  "");
		}
		
		PostResult result = assignationService.deleteAssignation(UUID.fromString(assignationId), getLoggedUser().getC1Id());
		
		return result;
	}
	
}
