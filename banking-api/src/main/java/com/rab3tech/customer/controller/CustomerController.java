package com.rab3tech.customer.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rab3tech.customer.service.CustomerRequestService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.PayeeService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.customer.service.impl.SecurityQuestionService;
import com.rab3tech.vo.ApplicationResponseVO;
import com.rab3tech.vo.CustomerSecurityQueAnsVO;
import com.rab3tech.vo.EnquiryRequestVO;
import com.rab3tech.vo.LoginRequestVO;
import com.rab3tech.vo.LoginVO;
import com.rab3tech.vo.SecurityQuestionsVO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v3")
public class CustomerController {
	@Autowired
 	private LoginService loginService;
	
	@Autowired
	private CustomerEnquiryService customerEnquiryService;
	
	@Autowired
	private SecurityQuestionService securityQuestionService;
	
	@Autowired
	private PayeeService payeeService;
	
	@Autowired
	private CustomerRequestService customerRequestService;

	
	@PostMapping("/user/login")
 	public ApplicationResponseVO authUser(@RequestBody LoginRequestVO loginRequestVO) {
 		Optional<LoginVO>  optional=loginService.findUserByUsername(loginRequestVO.getUsername());
 		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
 		if(optional.isPresent()) {
 			Optional<LoginVO>  optional2 = loginService.authUser(optional.get());
 			if(optional2.isPresent()) {
	 			applicationResponseVO.setCode(200);
	 			applicationResponseVO.setStatus("success");
	 			applicationResponseVO.setMessage("Userid is correct");
 			}
 			else {
 				applicationResponseVO.setCode(400);
 	 			applicationResponseVO.setStatus("fail");
 	 			applicationResponseVO.setMessage("Please enter correct password..");
 			}
 		}else {
 			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage("Username or password is not correct");
 		}
 		return applicationResponseVO;
 	}
	
	@PostMapping("/account/enquiry")
	public ApplicationResponseVO submitEnquiryData(@RequestBody EnquiryRequestVO enquiryRequestVO) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		boolean status = customerEnquiryService.emailNotExist(enquiryRequestVO.getEmail());
		if(status) {
			applicationResponseVO.setCode(200);
 			applicationResponseVO.setStatus("success");
 			applicationResponseVO.setMessage("");
		}
		else {
			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage("Sorry , this email is already in use " + enquiryRequestVO.getEmail());
		}
		return applicationResponseVO;
	}
	
	@PostMapping("/verify/email")
	public ApplicationResponseVO verifyEmail(@RequestBody LoginRequestVO loginRequestVO) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		Optional<LoginVO>  optional=loginService.findUserByUsername(loginRequestVO.getUsername());
		if(optional.isPresent()) {
			applicationResponseVO.setCode(200);
 			applicationResponseVO.setStatus("success");
 			applicationResponseVO.setMessage("Userid is correct");
		}else {
			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage("Please Enter your valid email address..");
		}
		
		return applicationResponseVO;
	}
	
	@PostMapping("/check/answers")
	public ApplicationResponseVO verifyAnswers(@RequestBody CustomerSecurityQueAnsVO customerSecurityQueAnsVO) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		CustomerSecurityQueAnsVO queAnsVO = securityQuestionService.findAnsByLoginid(customerSecurityQueAnsVO.getLoginid());
		
		if(queAnsVO.getSecurityQuestionAnswer1().equalsIgnoreCase(customerSecurityQueAnsVO.getSecurityQuestionAnswer1())){
			if(queAnsVO.getSecurityQuestionAnswer2().equalsIgnoreCase(customerSecurityQueAnsVO.getSecurityQuestionAnswer2())){
				applicationResponseVO.setCode(200);
	 			applicationResponseVO.setStatus("success");
	 			applicationResponseVO.setMessage("Answers are Correct");		
			}
			else {
				applicationResponseVO.setCode(402);
	 			applicationResponseVO.setStatus("fail");
	 			applicationResponseVO.setMessage("Please Answer Sequrity Question 2 correctly");
			}
		}
		else {
			if(queAnsVO.getSecurityQuestionAnswer2().equalsIgnoreCase(customerSecurityQueAnsVO.getSecurityQuestionAnswer2())){
				applicationResponseVO.setCode(401);
	 			applicationResponseVO.setStatus("fail");
	 			applicationResponseVO.setMessage("Please Answer Sequrity Question 1 correctly");
			}
			else {
				applicationResponseVO.setCode(400);
	 			applicationResponseVO.setStatus("fail");
	 			applicationResponseVO.setMessage("Please Answer Both Sequrity Questions correctly");
			}
		}
		
		
		return applicationResponseVO;
	}
	
	@DeleteMapping("/remove/payee/{id}")
	public ApplicationResponseVO removePayee(@PathVariable("id") int id) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		try{
			payeeService.removePayee(id);
			applicationResponseVO.setCode(200);
 			applicationResponseVO.setStatus("success");
 			applicationResponseVO.setMessage("One Payee Deleted..");
			
		}
		catch(Exception e) {
			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage(e.getMessage());
		}
		return applicationResponseVO;
	}
	
	@PostMapping("/sequirity/question/{id}")
	public SecurityQuestionsVO getQuestionById(@PathVariable("id") int id) {
		SecurityQuestionsVO questionsVO = securityQuestionService.findByid(id);
		return questionsVO;
	}
	
	@PostMapping("/submit/request/{email}")
	public ApplicationResponseVO submitRequest(@PathVariable("email") String email) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		try {
			String refNo = customerRequestService.submitRequest(email);
			applicationResponseVO.setCode(200);
 			applicationResponseVO.setStatus("success");
 			applicationResponseVO.setMessage(refNo);
		}
		catch(Exception e) {
			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage(e.getMessage());
		}
		return applicationResponseVO;
	}

}
