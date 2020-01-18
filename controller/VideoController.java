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

import utility.Paging;
import video.model.Video;
import video.model.VideoDao;

@Controller
public class VideoController {
	@Autowired private VideoDao vdao;
	@Autowired	ServletContext sc;
	
	
	//조회
	@RequestMapping(value = "video_list.adm")
	public ModelAndView doSearch(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, 
			HttpServletRequest request) {
		
		System.out.println("선택 칼럼: " + whichColumn);
		System.out.println("키워드: " + keyword);
		System.out.println("페이지 번호: " + pageNumber);
		System.out.println("페이지당 레코드수: " + pageSize);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");
		System.out.println("검색키워드: " + keyword);

		int totalCount = vdao.getTotalCount(map);
		System.out.println("검색 결과 개수: " + totalCount);

		String url = request.getContextPath() + "/video_list.adm?";
		System.out.println("url: " + url);
		
		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, whichColumn, keyword, null);
		System.out.println();
		System.out.println("offset: " + pageInfo.getOffset() + ",");
		System.out.println("limit: " + pageInfo.getLimit() + ",");
		
		List<Video> videoList = vdao.getVideoList(pageInfo, map);    
		System.out.println("videoList 크기 : " + videoList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("videoList", videoList);
		mnv.addObject("pageInfo",  pageInfo);
		mnv.setViewName("VideoList");
		
		return mnv;
	}
	
	@RequestMapping(value = "video_detail.adm", method = RequestMethod.GET)
	public String goToDetail(@RequestParam(value = "vid", required = true) String vid, Model model) {

		Video video = vdao.getVideo(vid);
		model.addAttribute("video", video);
		return "VideoDetailView";
	}
	
	// 영화만 조회
	@RequestMapping(value = "movie_list.adm", method = RequestMethod.GET)
	public ModelAndView showMovieList(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, 
			HttpServletRequest request) {
		
		System.out.println("whichColumn: " + whichColumn);
		System.out.println("keyword: " + keyword);
		System.out.println("pageNumber: " + pageNumber);
		System.out.println("pageSize: " + pageSize);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");
		System.out.println("검색키워드: " + keyword);

		int totalCount = vdao.getTotalCount(map);
		System.out.println("검색 결과 개수: " + totalCount);

		String url = request.getContextPath() + "/movie_list.adm?";
		System.out.println("url: " + url);
		
		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, whichColumn, keyword, null);
		System.out.println();
		System.out.println("offset: " + pageInfo.getOffset() + ",");
		System.out.println("limit: " + pageInfo.getLimit() + ",");
		
		List<Video> movieList = vdao.getMovieList(pageInfo, map);    
		System.out.println("movieList 크기 : " + movieList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("movieList", movieList);
		mnv.addObject("pageInfo",  pageInfo);
		mnv.setViewName("MovieList");
		
		return mnv;
	}
	
	// TV 프로그램만 조회
	@RequestMapping(value = "tv_list.adm", method = RequestMethod.GET)
	public ModelAndView showTvList(
			@RequestParam(value = "whichColumn", required = false) String whichColumn,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			@RequestParam(value = "pageSize", required = false) String pageSize, 
			HttpServletRequest request) {
		
		System.out.println("whichColumn: " + whichColumn);
		System.out.println("keyword: " + keyword);
		System.out.println("pageNumber: " + pageNumber);
		System.out.println("pageSize: " + pageSize);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("whichColumn", whichColumn);
		map.put("keyword", "%" + keyword + "%");
		System.out.println("검색키워드: " + keyword);

		int totalCount = vdao.getTotalCount(map);
		System.out.println("검색 결과 개수: " + totalCount);

		String url = request.getContextPath() + "/tv_list.adm?";
		System.out.println("url: " + url);
		
		Paging pageInfo = new Paging(pageNumber, pageSize, totalCount, url, whichColumn, keyword, null);
		System.out.println();
		/*System.out.println("offset: " + pageInfo.getOffset() + ",");
		System.out.println("limit: " + pageInfo.getLimit() + ",");*/
		
		List<Video> tvList = vdao.getTvList(pageInfo, map);    
		System.out.println("tvList 크기 : " + tvList.size());
		
		ModelAndView mnv = new ModelAndView();
		mnv.addObject("tvList", tvList);
		mnv.addObject("pageInfo",  pageInfo);
		mnv.setViewName("TvList");
		
		return mnv;
	}
	
	//삽입
	@RequestMapping(value="insert_video.adm", method=RequestMethod.GET)
	public String goToInsert() {
		return "VideoInsertForm";
	}
	
	@RequestMapping(value="insert_video.adm", method=RequestMethod.POST)
	public String insert(@Valid Video video, BindingResult result) {
			if(result.hasErrors()){
				System.out.println("비디오 수정 오류 발생");
				return "VideoInsertForm";
			}
			
		// System.out.println("poster_img: " + video.getPoster_img());
		// System.out.println("still_img: " + video.getStill_img());
		
		 //키를 먼저 받아옴  : 여러 트랜잭션이 동시다발적으로 일어날 때 getLastVideo로 받아온 vid를 쓰는 것은 위험
		int vid = vdao.makeVideoIdx();
		video.setVid(vid);
		vdao.insertVideo(video);
		
		
		//이미지명을 겹치지 않게 하기 위해 vid로 폴더를 따로 만듦 → 대안 : 이미지 이름 자체를 랜덤키(혹은 해시코드 값)로 바꿔서 저장해보기
		// 경로: /resources/video/17
		String uploadPathStr = sc.getRealPath("/resources") + File.separator + "video"
				 + File.separator  + vid; // File.separator : 운영체제간에 다른 세퍼레이터르 알아서 맞춰줌
		File uploadPath = new File(uploadPathStr);
		if(!uploadPath.exists()) {
			uploadPath.mkdirs();
			System.out.println("uploadPath: " + uploadPath);
		}
		
		File thumbnail = new File(uploadPathStr + File.separator + video.getThumbnail_img());
		File poster = new File(uploadPathStr + File.separator + video.getPoster_img());
		File still = new File(uploadPathStr + File.separator + video.getStill_img()); 
	
		try {
			//이미지 파일 실제 업로드
			video.getThumbnail().transferTo(thumbnail);
			video.getPoster().transferTo(poster);
			video.getStill().transferTo(still);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* 
		String[] fileName = {video.getThumbnail_img(), video.getPoster_img(), video.getStill_img()};
		MultipartFile[] realFile = {video.getThumbnail(), video.getPoster(), video.getStill()}; //메서드 체이닝으로 .transferTo할 것
		
		for(int i=0; i<3; i++) {
			File file = new File(uploadPathStr + File.separator + fileName[i]);
			MultipartFile mpf = realFile[i];
			try {
				mpf.transferTo(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return "redirect:/admin";	
	}

	//수정
	@RequestMapping(value = "update_video.adm", method = RequestMethod.GET)
	public String getVideo(@RequestParam(value = "vid", required = true) String vid, Model model) {
		System.out.println("vid 맞니: " + vid);
		Video video = vdao.getVideo(vid);
		model.addAttribute("video", video);
	
		System.out.println(video.getThumbnail_img());
		System.out.println(video.getPoster_img());
		System.out.println(video.getStill_img());
		System.out.println(video.getRelease_date());
		return "VideoUpdateForm";
	}

	@RequestMapping(value = "update_video.adm", method = RequestMethod.POST)
	public ModelAndView updateVideo(
			@ModelAttribute("video") @Valid Video video, BindingResult result, int vid) {

		ModelAndView mnv = new ModelAndView();
		if (result.hasErrors()) {
			System.out.println("업데이트 에러 발생");
			mnv.setViewName("VideoUpdateForm");
			return mnv;
		}
		
		vdao.updateVideo(video);
		
		String uploadPathStr = sc.getRealPath("/resources") + File.separator + "video" 
				+ File.separator + vid;
		
		File new_thumbnail = new File(uploadPathStr + File.separator + video.getThumbnail_img());
		File new_poster = new File(uploadPathStr + File.separator + video.getPoster_img());
		File new_still = new File(uploadPathStr + File.separator + video.getStill_img());
		
		File old_thumbnail_img = new File(uploadPathStr + File.separator + video.getOld_thumbnail());
		File old_poster_img = new File(uploadPathStr + File.separator + video.getOld_poster());
		File old_still_img = new File(uploadPathStr + File.separator + video.getOld_still());
		
		// 파일 이미지 변경 없을 때 기존 이미지는 날아가지 않도록 조건 걸기
		if(old_thumbnail_img != null) {
			video.getThumbnail();
		}
		if(old_poster_img != null) {
			video.getPoster();
		}
		if(old_still_img != null) {
			video.getStill();
		}
		
		old_thumbnail_img.delete();
		old_poster_img.delete();
		old_still_img.delete();
		
		MultipartFile mpf1 = video.getThumbnail();
		MultipartFile mpf2 = video.getPoster();
		MultipartFile mpf3 = video.getStill();
		
		try {
			mpf1.transferTo(new_thumbnail);
			mpf2.transferTo(new_poster);
			mpf3.transferTo(new_still);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*mnv.setViewName("redirect:/video_list.adm");*/
		mnv.setViewName("redirect:/home.adm");
		return mnv;
	}
		
	
	//삭제
	@RequestMapping("delete_video.adm")
	public String delete(@RequestParam("vid") String vid) {
		boolean isDeleted = false;
		isDeleted = vdao.deleteVideo(vid);
		//관련 이미지 파일도 같이 삭제
		if(isDeleted) {
			String path = sc.getRealPath("/resources/" + vid);
			System.out.println("path: " + path);
			File folder = new File(path);
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			folder.delete();
		} else {
			System.out.println("비디오 삭제 실패");
		}
		return "redirect:/video_list.adm";
	}
}
