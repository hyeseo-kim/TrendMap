package com.realtimeradar.service;

import com.realtimeradar.entity.News;
import com.realtimeradar.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;

    public List<News> crawlLiveNews(String region) {
        /**
         * 네이버 뉴스 검색 결과를 실시간으로 크롤링하여 News 객체 리스트로 반환합니다.
         *
         * ⚠️ 주의: 네이버 뉴스 페이지의 구조나 클래스명이 변경될 경우
         * 셀렉터(css selector)가 동작하지 않을 수 있으며,
         * 그에 따라 크롤링 결과가 null 또는 예외가 발생할 수 있습니다.
         *
         * 구조 변경에 대비하여, 주기적인 셀렉터 점검 또는 예외 로깅 분석이 필요합니다.
         */
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        List<News> newsList = new ArrayList<>();

        try {
            String searchUrl = "https://search.naver.com/search.naver?where=news&query=" + region + "+뉴스";
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.sds-comps-base-layout")));

            // ✅ 뉴스 카드 전체 기준
            List<WebElement> newsCards = driver.findElements(By.cssSelector("div.sds-comps-base-layout"));

            for (WebElement card : newsCards) {
                try {
                    String title = card.findElement(By.cssSelector("span.sds-comps-text-type-headline1")).getText();
                    String summary = card.findElement(By.cssSelector("span.sds-comps-text-type-body1")).getText();
                    String url = card.findElement(By.tagName("a")).getAttribute("href");

                    String imageUrl = null;
                    try {
                        WebElement img = card.findElement(By.cssSelector("div.sds-comps-image img"));
                        imageUrl = img.getAttribute("src");

                        if (imageUrl != null && (imageUrl.contains("default_placeholder") || imageUrl.startsWith("data:image"))) {
                            imageUrl = null;
                        }
                    } catch (NoSuchElementException ignore) {}

                    if (imageUrl == null || imageUrl.isBlank()) {
                        log.info("⛔ 이미지 없는 뉴스 제외: " + title);
                        continue;
                    }

                    News news = new News();
                    news.setTitle(title);
                    news.setSummary(summary);
                    news.setUrl(url);
                    news.setImageUrl(imageUrl);
                    news.setRegion(region);
                    news.setCrawledAt(LocalDateTime.now());

                    newsList.add(news);

                } catch (Exception e) {
                    log.warn("⚠️ 뉴스 카드 파싱 실패", e);
                }
            }

        } catch (Exception e) {
            log.error("❌ 전체 뉴스 크롤링 실패 - 사이트 구조 변경 또는 연결 오류 가능성", e);
        } finally {
            driver.quit();
        }

        return newsList;
    }

    @Transactional
    public void incrementClickCount(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setClickCount(news.getClickCount() + 1);
        newsRepository.save(news);
    }

}
