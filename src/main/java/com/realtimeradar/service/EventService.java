package com.realtimeradar.service;

import com.realtimeradar.dto.EventDTO;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EventService {

    public List<EventDTO> crawlEvents(String region) {
        List<EventDTO> eventList = new ArrayList<>();

        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            String url = "https://search.naver.com/search.naver?query=" + region + "+축제";
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".data_box")));

            List<WebElement> eventElements = driver.findElements(By.cssSelector(".data_box"));

            for (WebElement element : eventElements) {
                try {
                    String title = element.findElement(By.cssSelector(".area_text_box .this_text")).getText().trim();

                    // ✅ 날짜 추출
                    List<WebElement> dateSpans = element.findElements(By.cssSelector(".rel_info dd span.info_date"));
                    if (dateSpans.isEmpty()) {
                        log.warn("⚠️ 날짜 정보 없음 → 제목: {}", title);
                        continue;
                    }

                    String rawStartDate = dateSpans.get(0).getText().replaceAll("\\(.*?\\)", "").trim();
                    String rawEndDate = (dateSpans.size() > 1)
                            ? dateSpans.get(1).getText().replaceAll("\\(.*?\\)", "").trim()
                            : rawStartDate;

                    if (rawStartDate.isBlank() || rawEndDate.isBlank()) {
                        log.warn("⚠️ 시작/종료 날짜가 비어 있음 → 제목: {}", title);
                        continue;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
                    LocalDate start = LocalDate.parse(rawStartDate, formatter);
                    LocalDate end = LocalDate.parse(rawEndDate, formatter);

                    // ✅ 장소 추출
                    List<WebElement> ddElements = element.findElements(By.cssSelector(".rel_info dd"));
                    String location = ddElements.size() > 1 ? ddElements.get(1).getText().trim() : "";

                    EventDTO dto = new EventDTO(title, "", start.toString(), end.toString(), location, region);
                    eventList.add(dto);

                } catch (Exception innerEx) {
                    log.warn("⚠️ 날짜 파싱 실패: {} → 내용: '{}'", innerEx.getMessage(), element.getText());
                }
            }

        } catch (Exception e) {
            log.error("❌ 행사 크롤링 실패: ", e);
        } finally {
            driver.quit();
        }

        return eventList;
    }
}
