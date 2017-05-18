package com.xiyuejia.project.controllers.gzf;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiyuejia.project.dao.gzf.TabLvjuUserDAO;
import com.xiyuejia.project.dao.gzf.TabLvjuValiacodeDAO;
import com.xiyuejia.project.model.gzf.TabLvjuUser;
import com.xiyuejia.project.model.gzf.TabLvjuValiacode;
import com.xiyuejia.project.model.gzf.TabLvjuValiacodeExample;

@Controller
@RequestMapping("/lvju")
public class LvJuController {
	private Logger logger = Logger.getLogger(LvJuController.class);

	@Autowired
	private TabLvjuUserDAO tabLvjuUserDAO;

	@Autowired
	private TabLvjuValiacodeDAO tabLvjuValiacodeDAO;

	/**
	 * 时间类
	 * 
	 * @return
	 */
	public Date getValite() {
		Date date = new Date();
		date.setTime(date.getTime() + 60 * 1000 * 60);
		return date;
	}
	
	public Date getValites(){
		Date date=new Date();
		date.setTime(date.getTime()+60*1000*15);
		return date;
	}
	
	/**
	 * 返回Json数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getJson", method = RequestMethod.POST)
	public Map<String, Object> getJson(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		TabLvjuValiacodeExample example = new TabLvjuValiacodeExample();
		TabLvjuValiacodeExample.Criteria criteria = example.createCriteria();
		example.setOrderByClause("code desc");

		List<TabLvjuValiacode> tabuser = tabLvjuValiacodeDAO.selectByExample(example);
		if (tabuser != null && tabuser.size() > 0) {
			map.put("flag", "true");
			map.put("tabuser", tabuser);
		} else {
			map.put("flag", "false");
			map.put("msg", "misss");
		}
		return map;
	}
	
	/**
	 * 发送验证码
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendCode"/* , method = RequestMethod.POST */)
	public Map<String, Object> sendCode(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");

		TabLvjuValiacode valiacode = new TabLvjuValiacode();
		valiacode.setRowid(UUID.randomUUID().toString());
		valiacode.setPhone(phone);
		valiacode.setCode(Integer.toString((int) ((Math.random() * 9 + 1) * 100000)));
		valiacode.setCreatetime(new Date());
		valiacode.setPasstime(getValite());
		valiacode.setFlag("1");
		valiacode.setInvitecodes(Integer.toString((int) ((Math.random() * 9 + 1) * 100000)));
		tabLvjuValiacodeDAO.insert(valiacode);
		map.put("flag", "true");
		map.put("msg", "验证码发送成功!");
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/sendCodes",method=RequestMethod.POST)
	public Map<String,Object> sendCodes(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone=request.getParameter("phone");
		
		TabLvjuValiacode code=new TabLvjuValiacode();
		code.setRowid(UUID.randomUUID().toString());
		code.setPhone(phone);
		code.setCreatetime(new Date());
		code.setPasstime(getValite());
		code.setFlag("1");
		code.setCode(Integer.toString((int)((Math.random()*9+1)*100000)));
		code.setInvitecodes(Integer.toString((int)((Math.random()*9+1)*100000)));
		tabLvjuValiacodeDAO.insert(code);
		map.put("flag", "true");
		map.put("tabuser", code);
		return map;
	}
	/**
	 * 检查验证码是否发送成功
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkCode"/* ,method=RequestMethod.POST */)
	public Map<String, Object> checkCode(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");
		String code = request.getParameter("code");

		Map<String, String> parame = new HashMap<>();
		parame.put("phone", phone);
		parame.put("code", code);
		List<TabLvjuValiacode> tabuser = tabLvjuValiacodeDAO.checkByCode(parame);

		if (tabuser != null && tabuser.size() > 0) {
			TabLvjuValiacode valiacode = tabuser.get(0);
			valiacode.setFlag("2");
			tabLvjuValiacodeDAO.updateByPrimaryKeySelective(valiacode);
			map.put("flag", "true");
			map.put("tabuser", tabuser);
			map.put("msg", "验证码发送成功!");
		} else {
			map.put("flag", "false");
			map.put("msg", "验证码发送失败!");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/checkCodes",method=RequestMethod.POST)
	public Map<String,Object> checkCodes(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone=request.getParameter("phone");
		String code=request.getParameter("code");
		
		Map<String,String> parame=new HashMap<>();
		parame.put("phone", phone);
		parame.put("code", code);
		List<TabLvjuValiacode> tabuser=tabLvjuValiacodeDAO.checkByCode(parame);
		
		if(tabuser!=null&&tabuser.size()>0){
			
		}
		return map;
	}
	/**
	 * 注册
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/register"/* ,method=RequestMethod.POST */)
	public Map<String, Object> register(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		String invitecode = request.getParameter("invitecode");

		Map<String, Object> parame = new HashMap<>();
		parame.put("phone", phone);
		List<TabLvjuUser> tabuser = tabLvjuUserDAO.searchUser(parame);

		if (tabuser != null && tabuser.size() > 0) {
			map.put("flag", "false");
			map.put("msg", "您的手机号码已经被注册!");
		} else {
			TabLvjuUser user = new TabLvjuUser();
			user.setRowid(UUID.randomUUID().toString());
			user.setPhone(phone);
			user.setPassword(DigestUtils.md5Hex(password));
			user.setCeatetime(new Date());
			user.setInvitecode(invitecode);
			tabLvjuUserDAO.insert(user);
			map.put("flag", "true");
			map.put("user", user);
			map.put("msg", "注册成功!");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/registers",method=RequestMethod.POST)
	public Map<String,Object> registers(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone =request.getParameter("phone");
		String password=request.getParameter("password");
		String invitecode=request.getParameter("invitecode");
		
		Map<String,Object> parame=new HashMap<>();
		parame.put("phone", phone);
		List<TabLvjuUser> tabuser=tabLvjuUserDAO.searchUser(parame);
		
		if(tabuser!=null&&tabuser.size()>0){
			map.put("flag", "false");
			map.put("msg", "您的手机号码已经被注册!");
		}else{
			TabLvjuUser user=new TabLvjuUser();
			user.setRowid(UUID.randomUUID().toString());
			user.setPhone(phone);
			user.setCeatetime(new Date());
			user.setPassword(DigestUtils.md5Hex(password));
			user.setInvitecode(invitecode);
			tabLvjuUserDAO.insert(user);
			map.put("flag", "true");
			map.put("user", user);
			map.put("msg", "注册成功!");
		}
		return map;
	}
	/**
	 * 检查手机号码有没有被注册
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkRegister"/* ,method=RequestMethod.POST */)
	public Map<String, Object> checkRegister(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");
		String flag = request.getParameter("flag");

		if (flag.equals("true")) {
			List<TabLvjuUser> tabuser = tabLvjuUserDAO.checkIsOnly(phone);
			if (tabuser != null && tabuser.size() > 0) {
				map.put("flag", "false");
				map.put("msg", "您的手机号码已经被注册!");
			} else {
				map.put("flag", "true");
				map.put("msg", "未注册!");
			}
		} else {
			List<TabLvjuUser> tabuser = tabLvjuUserDAO.checkIsOnly(phone);
			if (tabuser != null && tabuser.size() > 0) {
				map.put("flag", "true");
				map.put("msg", "已注册!");
			} else {
				map.put("flag", "false");
				map.put("msg", "您的手机号码没有被注册!");
			}
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/checkRegisters",method=RequestMethod.POST)
	public Map<String,Object> checkRegisters(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone=request.getParameter("phone");
		String flag=request.getParameter("flag");
		
		if(flag.equals("true")){
			List<TabLvjuUser> tabuser=tabLvjuUserDAO.checkIsOnly(phone);
			if(tabuser!=null&&tabuser.size()>0){
				map.put("flag", "false");
				map.put("msg", "您的手机号码已经被注册!");
			}else{
				map.put("flag", "true");
				map.put("msg", "未注册!");
			}
		}else{
			List<TabLvjuUser> tabuser=tabLvjuUserDAO.checkIsOnly(phone);
			if(tabuser!=null&&tabuser.size()>0){
				map.put("flag", "true");
				map.put("msg", "已注册!");
			}else{
				map.put("flag", "false");
				map.put("msg", "您的手机号码没有被注册!");
			}
		}
		return map;
	} 
	/**
	 * 登录
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/login"/*, method = RequestMethod.POST*/)
	public Map<String, Object> login(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");

		Map<String, String> parame = new HashMap<>();
		if (phone != null && !phone.equals("")) {
			parame.put("phone", phone);

			if (password != null && !password.equals("")) {
				parame.put("password", password);

				List<TabLvjuUser> tabuser = tabLvjuUserDAO.selectByLogin(parame);
				if (tabuser != null && tabuser.size() > 0 && tabuser.size() == 1) {
					map.put("flag", "true");
					map.put("tabuser", tabuser);
				} else {
					map.put("flag", "false");
					map.put("msg", "您输入的手机号码或者密码错误!");
				}
			} else {
				map.put("flag", "false");
				map.put("msg", "请输入密码!");
			}
		} else {
			map.put("flag", "false");
			map.put("msg", "请输入手机号码!");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/logins",method=RequestMethod.POST)
	public Map<String,Object> logins(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone=request.getParameter("phone");
		String password=request.getParameter("password");
		
		Map<String,Object> parame=new HashMap<>();
		if(phone!=null&&!phone.equals("")){
			parame.put("phone", phone);
			if(password!=null&&!phone.equals("")){
				parame.put("password", password);
				
				List<TabLvjuUser> tabuser=tabLvjuUserDAO.selectByLogin(parame);
				if(tabuser!=null&&tabuser.size()>0&&tabuser.size()==1){
					map.put("flag", "true");
					map.put("tabuser", tabuser);
				}else{
					map.put("flag", "false");
					map.put("msg", "手机号码或者密码错误!");
				}
			}else{
				map.put("flag", "false");
				map.put("msg", "请输入密码!");
			}
		}else{
			map.put("flag", "false");
			map.put("msg", "请输入手机号码!");
		}
		return map;
	}
	/**
	 * 忘记密码
	 */
	@ResponseBody
	@RequestMapping(value = "/resetPassword"/*, method = RequestMethod.POST*/)
	public Map<String, Object> resetPassword(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");

		Map<String, String> parame = new HashMap<>();
		parame.put("phone", phone);
		List<TabLvjuUser> tabuser = tabLvjuUserDAO.searchUser(parame);

		if (tabuser != null && tabuser.size() > 0) {
			TabLvjuUser user = tabuser.get(0);
			user.setPassword(DigestUtils.md5Hex(password));
			tabLvjuUserDAO.updateByPhone(user);
			map.put("flag", "true");
			map.put("tabuser", tabuser);
			map.put("msg", "密码修改成功!");
		} else {
			map.put("flag", "false");
			map.put("msg", "密码修改失败!");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/resetPasswords",method=RequestMethod.POST)
	public Map<String,Object> resetPasswords(HttpServletRequest request){
		Map<String,Object> map=new HashMap<>();
		String phone=request.getParameter("phone");
		String password=request.getParameter("password");
		
		Map<String,String> parame=new HashMap<>();
		parame.put("phone", phone);
		List<TabLvjuUser> tabuser=tabLvjuUserDAO.searchUser(parame);
		
		if(tabuser!=null&&tabuser.size()>0){
			TabLvjuUser user=tabuser.get(0);
			user.setPassword(DigestUtils.md5Hex(password));
			tabLvjuUserDAO.updateByPrimaryKeySelective(user);
			map.put("flag", "true");
			map.put("tabuser", tabuser);
		}else{
			map.put("flag", "false");
			map.put("msg", "修改密码失败!");
		}
		return map;
	}
}
