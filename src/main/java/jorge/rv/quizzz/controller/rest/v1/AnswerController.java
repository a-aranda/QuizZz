package jorge.rv.quizzz.controller.rest.v1;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jorge.rv.quizzz.exceptions.ResourceUnavailableException;
import jorge.rv.quizzz.exceptions.UnauthorizedActionException;
import jorge.rv.quizzz.model.Answer;
import jorge.rv.quizzz.model.Question;
import jorge.rv.quizzz.service.AnswerService;
import jorge.rv.quizzz.service.QuestionService;

@RestController
@RequestMapping(AnswerController.ROOT_MAPPING)
public class AnswerController {
	
	public static final String ROOT_MAPPING = "/answers";
	private static final Logger logger = LoggerFactory.getLogger(Answer.class);
	
	@Autowired
	AnswerService answerService;
	
	@Autowired
	QuestionService questionService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> save(@Valid Answer answer, 
						BindingResult result, 
						@RequestParam long question_id) 
								throws ResourceUnavailableException, UnauthorizedActionException {
		
		if (result.hasErrors()) {
			logger.error("Invalid answer provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Question question = questionService.find(question_id);
		answer.setQuestion(question);
		Answer newAnswer = answerService.save(answer);
		return ResponseEntity.status(HttpStatus.CREATED).body(newAnswer);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<?> find(@PathVariable Long answer_id) 
			throws ResourceUnavailableException {
		
		Answer answer = answerService.find(answer_id);
		return ResponseEntity.status(HttpStatus.OK).body(answer);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> update(@PathVariable Long answer_id, 
							@Valid Answer answer, 
							BindingResult result) 
									throws UnauthorizedActionException, ResourceUnavailableException {
		
		if (result.hasErrors()) {
			logger.error("Invalid answer provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		
		Answer updatedAnswer =  answerService.update(answer_id, answer);
		return ResponseEntity.status(HttpStatus.OK).body(updatedAnswer);
	}
	
	@RequestMapping(value = "/{answer_id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> delete(@PathVariable Long answer_id) 
			throws UnauthorizedActionException, ResourceUnavailableException {
		
		answerService.delete(answer_id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}