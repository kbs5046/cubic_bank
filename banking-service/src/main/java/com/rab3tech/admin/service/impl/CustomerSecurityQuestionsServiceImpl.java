package com.rab3tech.admin.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.admin.dao.repository.CustomerSecurityQuestionsRepository;
import com.rab3tech.admin.service.CustomerSecurityQuestionsService;
import com.rab3tech.dao.entity.SecurityQuestions;
import com.rab3tech.vo.SecurityQuestionsVO;

@Service
@Transactional
public class CustomerSecurityQuestionsServiceImpl implements CustomerSecurityQuestionsService {

	@Autowired
	private CustomerSecurityQuestionsRepository customerSecurityQuestionsDao;
	
	@Override
	public List<SecurityQuestionsVO> findSecurityQuestions() {
		List<SecurityQuestions> securityQuestions = customerSecurityQuestionsDao.findAll();
		List<SecurityQuestionsVO> questionsVOs = new ArrayList<>();
		for(SecurityQuestions question: securityQuestions) {
			SecurityQuestionsVO questionsVO = new SecurityQuestionsVO();
			BeanUtils.copyProperties(question, questionsVO);
			questionsVOs.add(questionsVO);		
		}
		return questionsVOs;
	}

	@Override
	public void updateStatus(int qid) {
		SecurityQuestions securityQuestions = customerSecurityQuestionsDao.findById(qid).get();
		if("yes".equalsIgnoreCase(securityQuestions.getStatus())) {
			securityQuestions.setStatus("no");
		}
		else {
			securityQuestions.setStatus("yes");
		}
	}

	@Override
	public void addSecurityQuestion(String question, String loginid) {
		SecurityQuestions securityQuestions = new SecurityQuestions(0,question,"yes",loginid,new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()));
		customerSecurityQuestionsDao.save(securityQuestions);
		
	}

}
