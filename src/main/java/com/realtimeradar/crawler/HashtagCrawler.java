package com.realtimeradar.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class HashtagCrawler {

    public List<String> crawlBlogHashtags(String region) {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        List<String> hashtags = new ArrayList<>();

        try {
            String keyword = URLEncoder.encode(region + " 핫플", StandardCharsets.UTF_8);
            String url = "https://section.blog.naver.com/Search/Post.naver?pageNo=1&rangeType=ALL&orderBy=sim&keyword=" + keyword;
            driver.get(url);

            Thread.sleep(4000);

            List<WebElement> titleElements = driver.findElements(By.cssSelector("span.title"));

            for (WebElement el : titleElements) {
                String titleText = el.getText().trim();
                log.info("📰 블로그 제목: {}", titleText);

                String[] words = titleText.split("[ \\p{Punct}]");
                for (String word : words) {
                    word = word.trim();
                    if (!word.isEmpty()
                            && word.length() >= 2
                            && word.length() <= 10
                            && !word.matches(".*\\d.*")
                            && word.matches("^[가-힣]+$")) {
                        hashtags.add("#" + word);
                    }
                }
            }

            Set<String> unique = new LinkedHashSet<>(hashtags);
            hashtags = new ArrayList<>(unique).subList(0, Math.min(10, unique.size()));

            log.info("✅ [{}] 지역 해시태그 {}개 추출 완료", region, hashtags.size());
            hashtags.forEach(tag -> log.info("👉 {}", tag));

        } catch (Exception e) {
            log.error("❌ 블로그 키워드 크롤링 실패", e);
        } finally {
            driver.quit();
        }

        if (hashtags.isEmpty()) {
            return List.of("#" + region + "핫플", "#맛집", "#카페", "#데이트", "#포토존");
        }

        return hashtags;
    }
}
