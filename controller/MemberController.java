package admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import admin.model.PurchaseHistory;
import admin.model.PurchaseHistoryDao;
import member.model.Member;
import member.model.MemberDao;
import utility.Paging;
import video.model.VideoDao;

@Controller
public class MemberController {
	@Autowired
	private MemberDao mdao;
	@Autowired
	private VideoDao vdao;
	@Autowired
	private PurchaseHistoryDao phdao;
	@Autowired
	private ServletContext sc;

// 조회
	@RequestMapping(value = "member_list.adm")
	public ModelAndView searchMember(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");

		int totalCount = mdao.getTotalCount(map);
		String url = request.getContextPath() + "/member_list.adm";

		ModelAndView mnv = new ModelAndView();

		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, whichColumn, keyword, null);
		System.out.println();
		System.out.println("offset: " + pageInfo.getOffset() + ",");
		System.out.println("limit: " + pageInfo.getLimit() + ",");

		List<Member> memberList = mdao.getMemberList(pageInfo, map);

		mnv.addObject("memberList", memberList);
		mnv.addObject("pageInfo", pageInfo);
		mnv.setViewName("MemberList");
		return mnv;
	}

	@RequestMapping(value = "member_detail.adm")
	public String getMemberInfo(@RequestParam(value = "mid", required = true) int mid, Model model) {

		Member member = mdao.getMemberInfo(mid);
		model.addAttribute("member", member);
		return "MemberDetail";
	}

// 삽입
	@RequestMapping(value = "insert_member.adm", method = RequestMethod.GET)
	public String goToInsert() {
		return "MemberInsertForm";
	}

	@RequestMapping(value = "insert_member.adm", method = RequestMethod.POST)
	public ModelAndView insertMember(@Valid Member member, BindingResult result) {
		System.out.println("멤버이름 뭔데: " + member.getName());
		ModelAndView mnv = new ModelAndView();
		if(result.hasErrors()) {
			System.out.println("유효성 검사 실패");
			mnv.setViewName("MemberInsertForm");
			return mnv;
		}
		int mid = mdao.makeMemberIdx();
		
		if(mid==0) {
			mid = 1;
		}
		
		System.out.println("mid?:" + mid);
		System.out.println("사진 뭐야: " + member.getProfile_img());
		System.out.println("연락처 뭐야: " + member.getContact());
		System.out.println("포인트 얼만데: " + member.getPoint());
		member.setMid(mid);
		mdao.insertMemberAdm(member);
		System.out.println("야");
		
		// 경로: /resources/member/77
		String uploadPathStr = sc.getRealPath("/resources") + File.separator + "member" + File.separator + mid;
		File uploadPath = new File(uploadPathStr);
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
			System.out.println("uploadPath?: " + uploadPath);
		}
		

		// 경로: /resources/member/77/aaa.jpg
		File profile = new File(uploadPathStr + File.separator + member.getProfile_img());

		MultipartFile mpf = member.getProfile();
		try {
			mpf.transferTo(profile);
			System.out.println("사진 넘어가냐고:" + member.getProfile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mnv.setViewName("redirect:/member_list.adm");
		return mnv;
	}

// 수정
	@RequestMapping(value = "update_member.adm", method = RequestMethod.GET)
	public String getMember(@RequestParam(value = "mid", required = true) String email, Model model) {
		System.out.println("이메일 주소: " + email);
		Member member = mdao.getMember(email);
		model.addAttribute("member", member);
		System.out.println("프로필 이미지: " + member.getProfile_img());
		return "redirect:/member_list.adm";
	}

	@RequestMapping(value = "update_member.adm", method = RequestMethod.POST)
	public ModelAndView updateMember(
			@ModelAttribute("member") @Valid Member member, BindingResult result, int mid) {
		ModelAndView mnv = new ModelAndView();
		if (result.hasErrors()) {
			System.out.println("업데이트 에러 발생 : " + result);
			mnv.setViewName("MemberDetail");
			return mnv;
		}

		mdao.updateMember(member);
		
		String uploadPathStr = sc.getRealPath("/resources") + File.separator + "member" 
							+ File.separator + mid;
		File uploadPath = new File(uploadPathStr);
		if(!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		
		File new_profile_img = new File(uploadPathStr + File.separator + member.getProfile_img());
		File old_profile_img = new File(uploadPathStr + File.separator + member.getProfile());

		old_profile_img.delete();
		System.out.println("왜 안 바뀌는데");

		MultipartFile mpf = member.getProfile();

		try {
			mpf.transferTo(new_profile_img);

		} catch (IllegalStateException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		mnv.setViewName("redirect:/member_list.adm");
		return mnv;
	}

// 삭제
	@RequestMapping(value = "delete_member.adm", method = RequestMethod.GET)
	public String deleteMember(@RequestParam(value = "mid", required = true) int mid) {
		System.out.println("mid 나와라: " + mid);
		Member member = mdao.getMemberInfo(mid);

		String deletePath = sc.getRealPath("/resources") + File.separator + "member" + File.separator + mid;
		
		System.out.println("회원 deletePath: " + deletePath);
		File deleteFile = new File(deletePath + File.separator + member.getProfile_img());
		File deleteFolder = new File(deletePath);
		
		deleteFile.delete();
		deleteFolder.delete();
		mdao.deleteMember(mid);

		return "redirect:/member_list.adm";
	}
	
// 주문내역
	// 주문내역 조회
	@RequestMapping(value = "/purchase_history.adm")
	public ModelAndView searchPurchaseHistory(
			@RequestParam(value="mid", required=false) String mid,
			@RequestParam(value="vid", required=false) String vid,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, 
			HttpServletRequest request) {

		System.out.println("MID: " + mid);
		System.out.println("페이지 번호: " + pageNumber);
		System.out.println("페이지당 레코드수: " + pageSize);
		
		int totalCount = phdao.getTotalCount(mid);
		System.out.println("검색 결과 개수: " + totalCount);

		String url = request.getContextPath() + "/purchase_history.adm";
		System.out.println("purchaseHistory URL: " + url);
		
		// 페이징
		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, null ,null, null);
		
		// 주문내역 조회
		Map<String, String> purchaseHistoryMap = new HashMap<String, String>();
		purchaseHistoryMap.put("mid", mid);
		purchaseHistoryMap.put("vid", vid);
		List<PurchaseHistory> purchaseHistoryList = phdao.readPurchaseHistory(pageInfo, purchaseHistoryMap);
		System.out.println("purchaseHistoryList 크기 :" + purchaseHistoryList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("purchaseHistoryList", purchaseHistoryList);
		mnv.addObject("pageInfo", pageInfo);
		mnv.addObject("mid", mid);
		mnv.setViewName("PurchaseHistory");
		
		return mnv;
	}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         	
	// 주문내역 삽입
	
	// 주문내역 수정
	
	// 주문내역 삭제
	@RequestMapping(value="/delete_order.adm")
	public String delete(@RequestParam("order_no") String order_no) {
		phdao.getOneOrder(order_no);	
		phdao.deletePurchaseHistory(order_no);
		return "redirect:/purchase_history.adm";
	}
}
