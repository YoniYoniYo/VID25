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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import admin.model.Banner;
import admin.model.BannerDao;

@Controller
public class BannerController {
	@Autowired ServletContext sc;
	@Autowired @Qualifier("myBannerDao") private BannerDao bdao;
	
	//조회
	@RequestMapping(value="/banner_list.adm")
	public ModelAndView doSearch(){
		
		List<Banner> bannerList = bdao.getBannerList();
		System.out.println("bannerList 크기 :" + bannerList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("bannerList", bannerList);
		mnv.setViewName("BannerList");
		return mnv;
	}
	
	//검색
	/*@RequestMapping(value = "banner_list.adm")
	public ModelAndView doSearch(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			HttpServletRequest request) {
		
		System.out.println("whichColumn: " + whichColumn);
		System.out.println("keyword: " + keyword);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");
		System.out.println("검색키워드: " + keyword);

		int totalCount = bdao.getTotalCount(map);
		System.out.println("검색 결과 개수: " + totalCount);

		String url = request.getContextPath() + "/video_list.adm";
		System.out.println("url: " + url);
		
		ModelAndView mnv = new ModelAndView();
		mnv.setViewName("BannerList");
		
		return mnv;
	}*/

	//삽입
	@RequestMapping(value="/insert_banner.adm", method=RequestMethod.GET) 
	public String doAction() {
		return "BannerInsertForm";
	}
	
	@RequestMapping(value="/insert_banner.adm", method=RequestMethod.POST)
	public ModelAndView doAction(@Valid Banner banner, BindingResult result) {
		System.out.println(getClass() + " : " + banner.getImage());
		ModelAndView mnv = new ModelAndView();
		System.out.println("미리보기 되는거니: " + banner.getUpload());
		if(result.hasErrors()) {
			System.out.println("삽입 에러 발생");
			mnv.setViewName("BannerInsertForm");
			return mnv;
		}

		banner.setBid(bdao.makeBannerIdx());
	
		bdao.insertBanner(banner);

		String uploadPath = sc.getRealPath("/resources") + File.separator + "banner";
		System.out.println(uploadPath);
		
		File file = new File(uploadPath + File.separator + banner.getImage());
		
		MultipartFile multi = banner.getUpload();
		try {
			multi.transferTo(file);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		mnv.setViewName("redirect:/banner_list.adm");
		return mnv;
	}
	
	
	//수정
	@RequestMapping(value = "/update_banner.adm", method = RequestMethod.GET)
	public String getBanner(@RequestParam(value = "bid", required = true) int bid, 
			Model model) {

		Banner banner = bdao.getBanner(bid);
		model.addAttribute("banner", banner);
		return "BannerUpdateForm";
	}

	@RequestMapping(value = "/update_banner.adm", method = RequestMethod.POST)
	public ModelAndView updateBanner(
			@ModelAttribute("banner") @Valid Banner banner, BindingResult result) {
		
		System.out.println(banner.getImage());
		ModelAndView mnv = new ModelAndView();
		if (result.hasErrors()) {
			System.out.println("업데이트 에러 발생");
			mnv.setViewName("BannerUpdateForm");
			return mnv;
		}

		String uploadPath = sc.getRealPath("/resources") + File.separator + "banner";
		System.out.println("업로드 패스: " + uploadPath);
		File newBanner = new File(uploadPath + File.separator + banner.getImage());
		File oldBanner = new File(uploadPath + File.separator + banner.getUpload2());
		
		// 파일 이미지 변경 없을 때 기존 이미지는 날아가지 않도록 조건 걸기
		if(!(banner.getUpload().isEmpty())) {
			oldBanner.delete();
			
			MultipartFile mpf = banner.getUpload();   
			
			try {
				mpf.transferTo(newBanner);
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		if(banner.getUpload().isEmpty()) {
			banner.setImage(banner.getUpload2());
		}
		
		bdao.updateBanner(banner);
		
		mnv.setViewName("redirect:/banner_list.adm");
		
		return mnv;
	}
	
	
	//삭제
	@RequestMapping(value="/delete_banner.adm", method=RequestMethod.GET)
	public String doAction(@RequestParam(value="bid", required=true) int bid){
		
		System.out.println("bid:" + bid);
		Banner banner = bdao.getBanner(bid);
		
		String deletePath = sc.getRealPath("/resources") + File.separator + "banner";
		System.out.println(deletePath);
		File deleteFile = new File(deletePath + File.separator + banner.getImage());
		
		deleteFile.delete();
		
		bdao.deleteBanner(bid);
		
		return "redirect:/banner_list.adm";
	}
	
}
