package com.rab3tech.customer.ui.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rab3tech.customer.service.CustomerAddressService;
import com.rab3tech.customer.service.CustomerRequestService;
import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.PayeeService;
import com.rab3tech.customer.service.RequestService;
import com.rab3tech.customer.service.TransactionService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.customer.service.impl.SecurityQuestionService;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.vo.ChangePasswordVO;
import com.rab3tech.vo.CustomerAddressVO;
import com.rab3tech.vo.CustomerRequestVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.CustomerSecurityQueAnsVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.EmailVO;
import com.rab3tech.vo.LoginVO;
import com.rab3tech.vo.PayeeInfoVO;
import com.rab3tech.vo.RequestTypeVO;
import com.rab3tech.vo.SecurityQuestionsVO;
import com.rab3tech.vo.TransactionVO;

/**
 * 
 * @author nagendra
 * This class for customer GUI
 *
 */
@Controller
public class CustomerUIController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerUIController.class);
	
	@Autowired
	private RequestService requestService;
	
	@Autowired
	private CustomerRequestService customerRequestService;
	
	@Autowired
	private CustomerAddressService customerAddressService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private PayeeService payeeService;
	
	@Autowired
	private CustomerEnquiryService customerEnquiryService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SecurityQuestionService securityQuestionService;
	
	@Autowired
    private LoginService loginService;
	
	@PostMapping("customer/submit/address")
	public String submitAddress(@ModelAttribute CustomerAddressVO customerAddressVO,Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			customerAddressService.saveAddress(customerAddressVO,loginVO.getUsername());
			Optional<CustomerAddressVO> optional = customerAddressService.findByCustomerId(loginVO.getUsername());
			model.addAttribute("customerAddressVO", optional.get());
			return "customer/confirmAddress";
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@GetMapping("customer/select/request")
	public String selectRequest(@RequestParam int reauestId,Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			RequestTypeVO requestTypeVO=requestService.findRequestTypeById(reauestId);
			if((requestTypeVO!=null) &&  requestTypeVO.getStatus()==1) {
				String page="customer/"+requestTypeVO.getName().toLowerCase();
				switch(requestTypeVO.getName()) {
					case "Check":
						Optional<CustomerAddressVO> optional = customerAddressService.findByCustomerId(loginVO.getUsername());
						CustomerAddressVO customerAddressVO = new CustomerAddressVO();
						if(optional.isPresent()) {
							Optional<CustomerRequestVO> customerRequestVO = customerRequestService.findIfRequestExist(loginVO.getUsername());
							if(customerRequestVO.isPresent()) {
								model.addAttribute("customerRequestVO", customerRequestVO.get());
								page = "customer/requestExist";
								break;
							}
							model.addAttribute("customerAddressVO", optional.get());
							page = "customer/confirmAddress";
							break;
						}
						model.addAttribute("customerAddressVO", customerAddressVO);
						page = "customer/customerAddress";
						break;
					case "Loan":
						break;
					case "Credit":
						break;
				}
				return page;
				
			}
			else {
				List<RequestTypeVO> requestTypeVOs = requestService.findActiveRequests();
				if(requestTypeVOs.isEmpty()) {
					model.addAttribute("message", "No request for customer is available right now.");
					return "customer/dashboard";
				}
				model.addAttribute("requestTypeVOs",requestTypeVOs);
				model.addAttribute("message", "Request is not available right now..");
				return "customer/raiseRequest";
				
			}
			
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@GetMapping("customer/raise/request")
	public String raiseRequest(Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			List<RequestTypeVO> requestTypeVOs = requestService.findActiveRequests();
			if(requestTypeVOs.isEmpty()) {
				model.addAttribute("message", "No request for customer is available right now.");
				return "customer/dashboard";
			}
			model.addAttribute("requestTypeVOs",requestTypeVOs);
			return "customer/raiseRequest";
			
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
//	@GetMapping("customer/request/checkbook")
//	public String requestCheckBook(Model model,HttpSession session) {
//		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
//		if(loginVO != null) {
//			Optional<CustomerAddressVO> optional = customerAddressService.findByCustomerId(loginVO.getUsername());
//			CustomerAddressVO customerAddressVO = new CustomerAddressVO();
//			if(optional.isPresent()) {
//				Optional<CustomerRequestVO> customerRequestVO = customerRequestService.findIfRequestExist(loginVO.getUsername());
//				if(customerRequestVO.isPresent()) {
//					model.addAttribute("customerRequestVO", customerRequestVO.get());
//					return "customer/requestExist";
//				}
//				model.addAttribute("customerAddressVO", optional.get());
//				return "customer/confirmAddress";
//			}
//			model.addAttribute("customerAddressVO", customerAddressVO);
//			return "customer/customerAddress";
//		}
//		model.addAttribute("message", "Please login first to continue");
//		return "customer/login";
//		
//	}
	
	@GetMapping("customer/account/summary")
	public String accountSummary(Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			List<TransactionVO> transactionVOs = transactionService.getSummary(loginVO.getUsername());
			if(transactionVOs.isEmpty()) {
				model.addAttribute("message", "You do not have any transaction yet...");
			}
			model.addAttribute("transactionVOs", transactionVOs);
			return "customer/accountSummary";
			
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
		
	}
	
	@PostMapping("customer/fund/transfer")
	public String transferFund(@ModelAttribute TransactionVO transactionVO,Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
//			if(transactionVO.getPayeeId() == 0) {
//				model.addAttribute("error","Please Select your Beneficiary by their NickName.");
//				return "customer/dashboard";
//			}
			transactionVO.setDebitCustomerId(loginVO.getUsername());
			String message = transactionService.transferFund(transactionVO);
			PayeeInfoVO payeeInfoVO = payeeService.getPayee(transactionVO.getPayeeId());
			String message1 = "Fund amount of $"+transactionVO.getAmount()+" have been transfered to "+payeeInfoVO.getPayeeNickName();
			if(message.equalsIgnoreCase(message1)) {
				model.addAttribute("message", message);
				return "customer/dashboard";
			}
			else {
				model.addAttribute("error", message);
				return "customer/dashboard";
			}
			
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@GetMapping("customer/load/transaction")
	public String loadPayees(Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			List<PayeeInfoVO> payeeInfoVOs =  payeeService.getPayeeList(loginVO.getUsername());
			if(payeeInfoVOs.isEmpty()) {
				model.addAttribute("error", "You do not have any beneficiary. Please go a head and add one.");
				return "customer/dashboard";
			}
			model.addAttribute("payeeInfoVOs", payeeInfoVOs);
			return "customer/fundTransfer";
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@PostMapping("customers/edit/payee")
	public String updatePayee(@ModelAttribute PayeeInfoVO payeeInfoVO,Model model,HttpSession session){
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			payeeInfoVO.setCustomerId(loginVO.getUsername());
			String message = payeeService.updatePayee(payeeInfoVO);
			return "redirect:/customer/payee/list?message="+message;
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@GetMapping("customers/edit/payee")
	public String editPayee(@RequestParam int id,Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			PayeeInfoVO payeeInfoVO = payeeService.getPayee(id);
			model.addAttribute("payeeInfoVO", payeeInfoVO);
			return "customer/editPayee";
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
//	@PostMapping("customers/remove/payee")
//	public String removePayee(@RequestParam int id,Model model,HttpSession session) {
//		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
//		if(loginVO != null) {
//			payeeService.removePayee(id);
//			return "redirect:/customer/payee/list";
//		}
//		model.addAttribute("message", "Please login first to continue");
//		return "customer/login";
//	}
	
	@GetMapping("customer/payee/list")
	public String payeeList(Model model,HttpSession session,@RequestParam(required=false) String message) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			List<PayeeInfoVO> payeeInfoVOs =  payeeService.getPayeeList(loginVO.getUsername());
			if(message != null) {
				if(message.equalsIgnoreCase("Payee information have been updated..") || 
						message.equalsIgnoreCase("New payee have been added to your account.")) {
					model.addAttribute("message", message);
				}
				else {
					model.addAttribute("error", message);
				}
			}
			model.addAttribute("payeeInfoVOs", payeeInfoVOs);
			return "customer/myPayees";
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@PostMapping("customer/add/payee")
	public String addPayeeinfo(@ModelAttribute PayeeInfoVO payeeInfoVO,Model model,HttpSession session) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			payeeInfoVO.setCustomerId(loginVO.getUsername());
			String message = payeeService.savePayee(payeeInfoVO);
			if(message.equalsIgnoreCase("New payee have been added to your account.")) {
				return "redirect:/customer/payee/list?message="+message;
			}
			else {
				return "redirect:/customer/add/payee?error="+message;
			}
			//return "redirect:/customer/payee/list?message="+message;
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	
	@GetMapping("customer/add/payee")
	public String addPayee(@RequestParam(required=false) String error,Model model) {
		if(error != null) {
			model.addAttribute("error",error);
		}
		return "customer/addPayee";
	}
	
	@PostMapping("customer/update/information")
	public String updateCustomerProfile(@ModelAttribute CustomerVO customerVO,HttpSession session,Model model,RedirectAttributes redirectAttributes) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			customerService.udpadteCustomerProfile(customerVO);
			CustomerSecurityQueAnsVO customerSecurityQueAnsVO = new CustomerSecurityQueAnsVO();
			customerSecurityQueAnsVO.setSecurityQuestion1(customerVO.getQuestion1());
			customerSecurityQueAnsVO.setSecurityQuestion2(customerVO.getQuestion2());
			customerSecurityQueAnsVO.setSecurityQuestionAnswer1(customerVO.getAnswer1());
			customerSecurityQueAnsVO.setSecurityQuestionAnswer2(customerVO.getAnswer2());
			customerSecurityQueAnsVO.setLoginid(customerVO.getUserid());
			securityQuestionService.save(customerSecurityQueAnsVO);
			//redirectAttributes.addFlashAttribute("message", "Your informantion have been updated..");
			return "redirect:/customer/update/information?message=Your informantion have been updated..";
		}
		model.addAttribute("message", "Please login first to update..");
		return "customer/login";
		
	}
	
	@GetMapping("customer/update/information")
	public String getCustomerProfile(HttpSession session,Model model,@RequestParam(required=false) String message) {
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		CustomerVO customerVO = customerService.findByUserid(loginVO.getUsername());
		List<SecurityQuestionsVO> questionsVOs=securityQuestionService.findAll();
//		Collections.shuffle(questionsVOs);
//		List<SecurityQuestionsVO> questionsVOs1=questionsVOs.subList(0, questionsVOs.size()/2);
//		List<SecurityQuestionsVO> questionsVOs2=questionsVOs.subList(questionsVOs.size()/2,questionsVOs.size());
//		model.addAttribute("questionsVOs1", questionsVOs1);
		model.addAttribute("questionsVOs", questionsVOs);
		model.addAttribute("customerVO", customerVO);
		model.addAttribute("message", message);
		return "customer/editProfile";
	}
	
	@PostMapping("customer/update/password")
	public String newPassword(@ModelAttribute ChangePasswordVO changePasswordVO,Model model,HttpSession session) {
//		LoginVO  loginVO2=(LoginVO)session.getAttribute("passwordSession");
//		String username=loginVO2.getUsername();
//		changePasswordVO.setLoginid(username);
//		if(changePasswordVO.getNewPassword().equals(changePasswordVO.getConfirmPassword())) {
//			loginService.changePassword(changePasswordVO);
//			return "customer/login";		
//		}
//		model.addAttribute("message","Passwords do not match");
//		return "customer/preset";
		loginService.changePassword(changePasswordVO);
		return "customer/login";
	}
	
	
//	@PostMapping("customer/check/answers")
//	public String resetPassword(@ModelAttribute CustomerSecurityQueAnsVO customerSecurityQueAnsVO, Model model,HttpSession session) {
//		LoginVO  loginVO2=(LoginVO)session.getAttribute("passwordSession");
//		String username=loginVO2.getUsername();
//		CustomerSecurityQueAnsVO queAnsVO = securityQuestionService.findAnsByLoginid(username);
//		String answer1 = queAnsVO.getSecurityQuestionAnswer1();
//		String answer2 = queAnsVO.getSecurityQuestionAnswer2();
//		if(answer1.equalsIgnoreCase(customerSecurityQueAnsVO.getSecurityQuestionAnswer1())){
//			if(answer2.equalsIgnoreCase(customerSecurityQueAnsVO.getSecurityQuestionAnswer2())){
//				return "customer/preset";
//		
//			}
//		
//		}
//		return "redirect:/customer/ask/securityQuestions?username="+username+"&message=Please enter correct answers";
//	}
	
	@PostMapping("/customer/ask/securityQuestions")
	public String getQuestions(@RequestParam String username,Model model,HttpSession session,@RequestParam(required=false) String message) {
//		Optional<LoginVO> optional=loginService.findUserByUsername(username);
//		if(optional.isPresent()) {
//			LoginVO  loginVO2=optional.get();
//			loginVO2.setPassword("");
//			session.setAttribute("passwordSession", loginVO2);
//		}
//		else {
//			model.addAttribute("message", "Your username is not correct");
//			return "customer/login";
//		}

		CustomerSecurityQueAnsVO queAnsVO = securityQuestionService.getQuestionByLoginid(username);
		if(queAnsVO == null) {
			model.addAttribute("message", "Your password was sent in your email after registration");
			return "customer/login";
		}
		//model.addAttribute("message", message);
		model.addAttribute("queAnsVO", queAnsVO);
		return "customer/answerquestion";
	}
	
	@PostMapping("/customer/changePassword")
 	public String saveCustomerQuestions(@ModelAttribute ChangePasswordVO changePasswordVO, Model model,HttpSession session) {
 		LoginVO  loginVO2=(LoginVO)session.getAttribute("userSessionVO");
 		String loginid=loginVO2.getUsername();
 		changePasswordVO.setLoginid(loginid);
 		String viewName ="customer/dashboard";
 		boolean status=loginService.checkPasswordValid(loginid,changePasswordVO.getCurrentPassword());
 		if(status) {
 			if(changePasswordVO.getNewPassword().equals(changePasswordVO.getConfirmPassword())) {
 				 //viewName ="customer/dashboard";
 				 viewName ="customer/login";
 				 loginService.changePassword(changePasswordVO);
 			}else {
 				model.addAttribute("error","Sorry , your new password and confirm passwords are not same!");
 				return "customer/login";	//login.html	
 			}
 		}else {
 			model.addAttribute("error","Sorry , your username and password are not valid!");
 			return "customer/login";	//login.html	
 		}
 		return viewName;
 	}
	
	
	@PostMapping("/customer/securityQuestion")
	public String saveCustomerQuestions(@ModelAttribute("customerSecurityQueAnsVO") CustomerSecurityQueAnsVO customerSecurityQueAnsVO, Model model,HttpSession session) {
		LoginVO  loginVO2=(LoginVO)session.getAttribute("userSessionVO");
		String loginid=loginVO2.getUsername();
		customerSecurityQueAnsVO.setLoginid(loginid);
 		securityQuestionService.save(customerSecurityQueAnsVO);
		//
		return "customer/changePassword";
	}

	// http://localhost:444/customer/account/registration?cuid=1585a34b5277-dab2-475a-b7b4-042e032e8121603186515
	@GetMapping("/customer/account/registration")
	public String showCustomerRegistrationPage(@RequestParam String cuid, Model model) {

		logger.debug("cuid = " + cuid);
		Optional<CustomerSavingVO> optional = customerEnquiryService.findCustomerEnquiryByUuid(cuid);
		CustomerVO customerVO = new CustomerVO();

		if (!optional.isPresent()) {
			return "customer/error";
		} else {
			// model is used to carry data from controller to the view =- JSP/
			CustomerSavingVO customerSavingVO = optional.get();
			customerVO.setEmail(customerSavingVO.getEmail());
			customerVO.setName(customerSavingVO.getName());
			customerVO.setMobile(customerSavingVO.getMobile());
			customerVO.setAddress(customerSavingVO.getLocation());
			customerVO.setAccType(customerSavingVO.getAccType());
			customerVO.setToken(cuid);
			logger.debug(customerSavingVO.toString());
			// model - is hash map which is used to carry data from controller to thyme
			// leaf!!!!!
			// model is similar to request scope in jsp and servlet
			model.addAttribute("customerVO", customerVO);
			return "customer/customerRegistration"; // thyme leaf
		}
	}

	@PostMapping("/customer/account/registration")
	public String createCustomer(@ModelAttribute CustomerVO customerVO, Model model) {
		// saving customer into database
		logger.debug(customerVO.toString());
		customerVO = customerService.createAccount(customerVO);
		// Write code to send email

		EmailVO mail = new EmailVO(customerVO.getEmail(), "javahunk2020@gmail.com",
				"Regarding Customer " + customerVO.getName() + "  userid and password", "", customerVO.getName());
		mail.setUsername(customerVO.getUserid());
		mail.setPassword(customerVO.getPassword());
		emailService.sendUsernamePasswordEmail(mail);
		System.out.println(customerVO);
		model.addAttribute("loginVO", new LoginVO());
		model.addAttribute("message", "Your account has been setup successfully , please check your email.");
		return "customer/login";
	}

	@GetMapping(value = { "/customer/account/enquiry", "/", "/mocha", "/welcome" })
	public String showCustomerEnquiryPage(Model model) {
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		// model is map which is used to carry object from controller to view
		model.addAttribute("customerSavingVO", customerSavingVO);
		return "customer/customerEnquiry"; // customerEnquiry.html
	}

	@PostMapping("/customer/account/enquiry")
	public String submitEnquiryData(@ModelAttribute CustomerSavingVO customerSavingVO, Model model) {
		//boolean status = customerEnquiryService.emailNotExist(customerSavingVO.getEmail());
		logger.info("Executing submitEnquiryData");
		//if (status) {
			CustomerSavingVO response = customerEnquiryService.save(customerSavingVO);
			logger.debug("Hey Customer , your enquiry form has been submitted successfully!!! and appref "
					+ response.getAppref());
			model.addAttribute("message",
					"Hey Customer , your enquiry form has been submitted successfully!!! and appref "
							+ response.getAppref());
//		} else {
//			model.addAttribute("message", "Sorry , this email is already in use " + customerSavingVO.getEmail());
//		}
		return "customer/success"; // customerEnquiry.html

	}

}
