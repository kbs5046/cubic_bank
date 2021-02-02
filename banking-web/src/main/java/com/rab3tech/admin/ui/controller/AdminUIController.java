package com.rab3tech.admin.ui.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rab3tech.admin.service.CustomerSecurityQuestionsService;
import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.LoginVO;
import com.rab3tech.vo.SecurityQuestionsVO;

@Controller
public class AdminUIController {
	@Autowired
 	private CustomerSecurityQuestionsService securityQuestionsService;
	
	@Autowired
	private CustomerService customerService;
	
	
	@GetMapping("/admin/security/questions")
	public String showSecurityQuestions(Model model) {
		List<SecurityQuestionsVO> questionsVOs = securityQuestionsService.findSecurityQuestions();
		model.addAttribute("questionVOs", questionsVOs);
		return "admin/securityQuestions";
	}
	
	@GetMapping("/admin/security/uquestion")
	public String updateSecurityQuestionStatus(@RequestParam int qid) {
		securityQuestionsService.updateStatus(qid);
		return "redirect:/admin/security/questions";
	}
	
	@PostMapping("/admin/add/question")
	public String addSecurityQuestion(String question,HttpSession session) {
		LoginVO loginVO = (LoginVO)session.getAttribute("userSessionVO");
		String loginid=loginVO.getUsername();
 		securityQuestionsService.addSecurityQuestion(question,loginid);
		return "redirect:/admin/security/questions";
	}
	
	@GetMapping("admin/customer/list")
	public String getCustomerList(Model model, HttpSession session){
		LoginVO  loginVO=(LoginVO)session.getAttribute("userSessionVO");
		if(loginVO != null) {
			List<CustomerVO> customerVOs = customerService.findAllCustomers();
			model.addAttribute("customerVOs", customerVOs);
			return "admin/customerList";
			
		}
		model.addAttribute("message", "Please login first to continue");
		return "customer/login";
	}
	

}
