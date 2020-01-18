package admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import utility.Paging;
import video.model.Video;
import video.model.VideoDao;

@Controller
public class AdminController {

	@RequestMapping(value = "login.adm", method = RequestMethod.GET)
	public String admin() {
		return "AdminLogin"; // 관리자는 암호를 입력하세요
	}

	@RequestMapping(value = "login.adm", method = RequestMethod.POST)
	public ModelAndView doLogin(@RequestParam("adminPassword") String adminPassword, HttpServletResponse response,
			HttpSession session) throws IOException {
			System.out.println(adminPassword);
		if (adminPassword.equals("3698453")) {
			// 로그인 됐음..
			session.setAttribute("admin", "admin");
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter writer;
			System.out.println("관리자 로그인 성공");
			writer = response.getWriter();
			
			System.out.println("왜 안 되냐고");
			writer.print("<script type='text/javascript'>");
			writer.print("alert('로그인 되었습니다. 관리자 홈으로 이동합니다.')");
			writer.print("</script>");
			ModelAndView mnv = new ModelAndView();
			mnv.setViewName("redirect:/home.adm");
			return mnv;

		} else {
			// 비밀번호 다른 경우...
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print("<script type='text/javascript'>");
			writer.print("alert('관리자만 접근할 수 있습니다.')");
			writer.print("</script>");
			System.out.println("관리자만 접근할 수 있습니다.");
			writer.flush();

			return new ModelAndView("AdminLogin");
		}
	}
	
	@Autowired
	VideoDao vdao;
	@RequestMapping(value = "home.adm")
	public ModelAndView AdminHome(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, 
			HttpServletRequest request) {
		
		System.out.println("검색 컬럼: " + whichColumn);
		System.out.println("키워드: " + keyword);
		System.out.println("페이지 번호: " + pageNumber);
		System.out.println("페이지당 레코드 수: " + pageSize);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");
		System.out.println("검색키워드: " + keyword);
		
		int totalCount = vdao.getTotalCount(map);
		System.out.println("비디오 총 갯수: " + totalCount);

		String url = request.getContextPath() + "/home.adm?";
		System.out.println("url은?: " + url);
		
		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, whichColumn, keyword, null);
		System.out.println();
//		System.out.println("offset: " + pageInfo.getOffset() + ",");
//		System.out.println("limit: " + pageInfo.getLimit() + ",");
		
		List<Video> videoLongList = vdao.getVideoLongList(pageInfo, map);    
		System.out.println("videoLongList 크기 : " + videoLongList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("videoLongList", videoLongList);
		mnv.addObject("pageInfo",  pageInfo);
		mnv.setViewName("VideoLongList");
		return mnv;
	}

}