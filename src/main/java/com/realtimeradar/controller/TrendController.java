package com.realtimeradar.controller;

import com.realtimeradar.dto.PlaceDTO;
import com.realtimeradar.entity.News;
import com.realtimeradar.dto.EventDTO;
import com.realtimeradar.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TrendController {

    private final NewsService newsService;
    private final PlaceService placeService;
    private final BlogHashtagService blogHashtagService;
    private final EventService eventService;
    private final WeatherService weatherService;
    private final YoutubeSearchService youtubeSearchService;

    @GetMapping("/realtime-news")
    public String showRealtimeNews(@RequestParam String region,
                                   @RequestParam(required = false) String youtubeKeyword,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model) {

        if (youtubeKeyword != null && !youtubeKeyword.isBlank() && page == -1) {
            List<Map<String, String>> videos = youtubeSearchService.searchYoutube(youtubeKeyword);
            model.addAttribute("videos", videos);
            model.addAttribute("region", region);
            return "home";
        }

        String englishRegion = switch (region) {
            case "서울" -> "seoul";
            case "수원" -> "suwon";
            case "인천" -> "incheon";
            case "광주" -> "gwangju";
            case "전주" -> "jeonju";
            case "장성" -> "jangseong";
            case "부산" -> "busan";
            case "대구" -> "daegu";
            case "창원" -> "changwon";
            case "대전" -> "daejeon";
            case "제주" -> "jeju";
            default -> "default";
        };

        List<String> hashtags = blogHashtagService.getLiveHashtags(region);
        List<Map<String, String>> hashtagList = hashtags.stream()
                .filter(tag -> tag != null && tag.startsWith("#") && tag.length() > 1)
                .map(tag -> {
                    String keyword = URLEncoder.encode(region + " " + tag.substring(1), StandardCharsets.UTF_8);
                    return Map.of(
                            "tag", tag,
                            "url", "https://section.blog.naver.com/Search/Post.naver?rangeType=ALL&orderBy=sim&keyword=" + keyword
                    );
                })
                .collect(Collectors.toList());

        model.addAttribute("hashtags", hashtagList);

        List<News> newsList = newsService.crawlLiveNews(region);
        model.addAttribute("newsList", newsList);

        List<PlaceDTO> places = placeService.getPlacesByRegion(region);
        model.addAttribute("places", places);

        List<EventDTO> eventList = eventService.crawlEvents(region);
        model.addAttribute("eventList", eventList);

        String previousDate = "";
        for (EventDTO event : eventList) {
            String currentDate = event.getStartDate();
            if (currentDate != null && !currentDate.isBlank()) {
                if (!currentDate.equals(previousDate)) {
                    System.out.println("\uD83D\uDCC5 " + currentDate);
                    previousDate = currentDate;
                }
            } else {
                System.out.println("\u26A0\uFE0F 날짜 없음 이벤트: " + event.getTitle());
            }
            System.out.println(event.getTitle());
            System.out.println(event.getLocation());
        }

        String weatherInfo = weatherService.getWeather(englishRegion);
        String weatherIcon = weatherService.getWeatherIcon(weatherInfo);
        model.addAttribute("weatherIcon", weatherIcon);
        model.addAttribute("weatherInfo", weatherInfo);

        List<EventDTO> thisWeekEvents = eventList.stream()
                .filter(e -> {
                    try {
                        LocalDate today = LocalDate.now();
                        LocalDate start = LocalDate.parse(e.getStartDate());
                        LocalDate end = LocalDate.parse(e.getEndDate());
                        return !(start.isAfter(today.plusDays(7)) || end.isBefore(today));
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .toList();

        model.addAttribute("weeklyEvents", thisWeekEvents);

        List<Map<String, String>> videos = youtubeKeyword != null && !youtubeKeyword.isBlank()
                ? youtubeSearchService.searchYoutube(youtubeKeyword)
                : null;
        model.addAttribute("videos", videos);

        String regionMainImage = switch (englishRegion) {
            case "seoul" -> "seoul/seoul_main.jpg";
            case "suwon" -> "suwon/suwon_main.jpg";
            case "incheon" -> "incheon/incheon_main.jpg";
            case "gwangju" -> "gwangju/gwangju_main.jpg";
            case "jeonju" -> "jeonju/jeonju_main.jpg";
            case "jangseong" -> "jangseong/jangseong_main.jpg";
            case "busan" -> "busan/busan_main.jpg";
            case "daegu" -> "daegu/daegu_main.jpg";
            case "changwon" -> "changwon/changwon_main.jpg";
            case "daejeon" -> "daejeon/daejeon_main.jpg";
            case "jeju" -> "jeju/jeju_main.jpg";
            default -> "default/default_main.jpg";
        };

        List<String> regionSideImages = switch (englishRegion) {
            case "seoul" -> List.of("seoul/seoul1.jpg", "seoul/seoul2.jpg", "seoul/seoul3.jpg", "seoul/seoul4.jpg");
            case "suwon" -> List.of("suwon/suwon1.jpg", "suwon/suwon2.jpg", "suwon/suwon3.jpg", "suwon/suwon4.jpg");
            case "incheon" -> List.of("incheon/incheon1.jpg", "incheon/incheon2.jpg", "incheon/incheon3.jpg", "incheon/incheon4.jpg");
            case "gwangju" -> List.of("gwangju/gwangju1.jpg", "gwangju/gwangju2.jpg", "gwangju/gwangju3.jpg", "gwangju/gwangju4.jpg");
            case "jeonju" -> List.of("jeonju/jeonju1.jpg", "jeonju/jeonju2.jpg", "jeonju/jeonju3.jpg", "jeonju/jeonju4.jpg");
            case "jangseong" -> List.of("jangseong/jangseong1.jpg", "jangseong/jangseong2.jpg", "jangseong/jangseong3.jpg", "jangseong/jangseong4.jpg");
            case "busan" -> List.of("busan/busan1.jpg", "busan/busan2.jpg", "busan/busan3.jpg", "busan/busan4.jpg");
            case "daegu" -> List.of("daegu/daegu1.jpg", "daegu/daegu2.jpg", "daegu/daegu3.jpg", "daegu/daegu4.jpg");
            case "changwon" -> List.of("changwon/changwon1.jpg", "changwon/changwon2.jpg", "changwon/changwon3.jpg", "changwon/changwon4.jpg");
            case "daejeon" -> List.of("daejeon/daejeon1.jpg", "daejeon/daejeon2.jpg", "daejeon/daejeon3.jpg", "daejeon/daejeon4.jpg");
            case "jeju" -> List.of("jeju/jeju1.jpg", "jeju/jeju2.jpg", "jeju/jeju3.jpg", "jeju/jeju4.jpg");
            default -> List.of("default/default1.jpg", "default/default2.jpg", "default/default3.jpg", "default/default4.jpg");
        };

        String regionFullName = switch (region) {
            case "서울" -> "서울특별시";
            case "수원" -> "경기도 수원시";
            case "인천" -> "인천광역시";
            case "광주" -> "광주광역시";
            case "전주" -> "전북특별자치도 전주시";
            case "장성" -> "전라남도 장성군";
            case "부산" -> "부산광역시";
            case "대구" -> "대구광역시";
            case "창원" -> "경상남도 창원시";
            case "대전" -> "대전광역시";
            case "제주" -> "제주특별자치도";
            default -> "대한민국";
        };

        String regionDescription = switch (region) {
            case "서울" -> "과거와 현재가 공존하며 하루가 다르게 변하는 서울을 여행하는 일은 매일이 새롭다. 도시 한복판에서 600년의 역사를 그대로 안고 있는 아름다운 고궁들과 더불어 대한민국의 트렌드를 이끌어나가는 예술과 문화의 크고 작은 동네들을 둘러볼 수 있는 서울은 도시 여행에 최적화된 장소다.";
            case "수원" -> "서울의 축소판이라도고 할 수 있는 경기도 수원시. 서울만큼이나 정치, 경제, 문화, 사회 등 다양한 부문에서 두루 발달한 도시이다. 그중에도 고고학적 가치를 지닌 수원화성은 수원의 자랑이며, 화성행궁 열차를 타고 대표 명소를 둘러보는 것도 좋다. 광교호수공원은 야경이 아름다워 밤에도 산책을 즐기는 사람들이 많으며, 수원 통닭골목은 저렴한 가격에 비해 양이 푸짐해 입소문을 타고 전국 각지에서 많은 이들이 찾아오고 있다.";
            case "인천" -> "살짝 비릿한 바다향이 매력적인 인천광역시. 지리적 특징을 잘 이용하여 내륙과 해안 지역의 관광이 두루 발달한 곳이다. 대표적인 해양관광지로는 을왕리 해수욕장을 비롯해 문화의 거리가 갖춰진 월미도 등이 있으며, 한국 속 작은 중국이라 불리는 차이나타운도 인천 여행지로 손꼽힌다. 이 외에도 인천 각처에 오랜 역사 유물들이 산재해 있어 역사를 테마로 여행 코스를 잡아보는 것도 좋다.";
            case "광주" -> "문화예술의 중심지라 불리는 광주광역시. 예향의 본고장답게 '맛'과 '멋'이 조화를 이루며 남도의 문화를 이끌어 왔다고 해도 과언이 아니다. 광주의 대표 축제인 광주비엔날레를 중심으로 광주김치대축제, 광주국제영화제 등 5대 축제를 즐길 수 있을 뿐 아니라 문화예술시장으로 재탄생한 대인시장의 야시장에서는 밤여행의 묘미를 즐길 수 있다.";
            case "전주" -> "한국의 멋이 살아있는 전주. 도심 한복판에 자리한 한옥마을에 들어서면 시대를 거슬러가는 기분이다. '경사스러운 터에 지어진 궁궐'이란 의미의 경기전에 들어서면 대나무가 서로 부대끼며 내는 소리에 귀 기울이게 된다. 전주 야경투어 명소의 대표인 전동성당과 한옥마을을 한눈에 내려다볼 수 있는 오목대 역시 빼놓을 수 없는 곳. 마을 전체가 미술관인 자만 벽화마을은 전주의 대표 포토 존이다.";
            case "장성" -> "학문과 선비의 고장 전남 장성군. 매년 봄, 소설 속 주인공으로 알려진 민중의 영웅 홍길동을 테마로 하는 축제가 황룡강변의 유채꽃을 배경으로 열린다. 울긋불긋 오색단풍으로 유명한 백양사는 천연기념물 매화 중 하나이자 국내에 4그루뿐인 고불매가 만개하는 곳이기도 하다. 치유의 숲이라 불리는 축령산 편백나무숲은 산림욕을 통해 잠시 일상에서의 스트레스를 내려놓을 수 있다.";
            case "부산" -> "우리나라 제2의 수도 부산광역시. 부산 대표 관광지로 손꼽히는 해운대는 밤에는 마린시티의 야경이 더해져 더욱 화려한 해변이 된다. 감천문화마을은 사진 찍기에 좋으며, 매해 가을마다 개최되는 아시아 최대 규모의 영화제인 부산국제영화제와 함께 부산의 구석구석을 즐겨보는 것도 좋다. 전통시장 투어가 있을 만큼 먹거리가 가득한 부산의 맛기행은 필수!";
            case "대구" -> "우리나라에서 가장 더운 지역 대구. 하지만 매년 여름 열리는 치맥 페스티벌과 함께라면 더위도 문제없다. 놀이동산 이월드는 가족과 함께 나들이하기에 좋으며, 두류공원도 산책코스로 제격! 음악 분수쇼로 유명한 수성못과 독특한 외관이 인상적인 전시공간 디아크는 대구의 야경 명소로 손꼽힌다. 우리나라 3대 시장인 서문시장 야시장도 대구의 대표 핫플레이스!";
            case "창원" -> "창원시는 세계 최대 벚꽃축제인 진해 군항제가 대표적이다. 벚꽃 아래 놓인 여좌천로망스다리는 대표 포토존이다. 아름다운 군무로 유명한 가창오리를 감상할 수 있는 주남저수지는 람사르총회를 통해 세계적인 주목을 받은 바 있다. 골목골목을 구경하는 재미가 있는 창동 예술촌과 해양드라마 세트장도 창원시에서만 만날 수 있는 이색 여행지이다.";
            case "대전" -> "다양한 테마 여행이 가능한 대전광역시. 맨발로 걸을 수 있는 계족산 황톳길은 온몸으로 자연을 느끼는 여행을 할 수 있으며, 대전 근현대 전시관과 남간정사 등 대전에 있는 역사 문화 공간을 코스로 잡아도 좋다. 아이들이 좋아하는 동물원이 있는 오월드와 가볍게 산책하기 좋은 유림공원도 있어 주말 가족 나들이 코스로도 손색이 없다.";
            case "제주" -> "섬 전체가 하나의 거대한 관광자원인 제주도. 에메랄드빛 물빛이 인상적인 협재 해수욕장은 제주 대표 여행지며, 파도가 넘보는 주상절리와 바다 위 산책로인 용머리 해안은 제주에서만 볼 수 있는 천혜의 자연경관으로 손꼽힌다. 드라마 촬영지로 알려진 섭지코스는 꾸준한 사랑을 받고 있으며 한라봉과 흑돼지, 은갈치 등은 제주를 대표하는 음식이다.";
            default -> "이 지역은 아직 소개가 준비 중입니다.";
        };

        model.addAttribute("region", englishRegion);
        model.addAttribute("regionMainImage", regionMainImage);
        model.addAttribute("regionSideImages", regionSideImages);
        model.addAttribute("regionFullName", regionFullName);
        model.addAttribute("regionDescription", regionDescription);

        return "home";
    }

    @GetMapping("/api/youtube-search")
    @ResponseBody
    public List<Map<String, String>> searchYoutubeOnly(@RequestParam String youtubeKeyword) {
        return youtubeSearchService.searchYoutube(youtubeKeyword);
    }

}
