package com.tencent.wxcloudrun.util;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;

public class AnalysisUtil {
    public static String getDynamicContent(WebDriver driver, String url) {
        driver.get(url);
        // 复合等待策略（显式等待 + Polling）
        new WebDriverWait(driver, 30)
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState")
                        .equals("complete"));
        // 等待特定元素加载
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(45))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        fluentWait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#video-player")
        ));

        return driver.getPageSource();
    }


    public static String safeCrawl(String url) throws Exception {
        WebDriver driver = null;
        try {
            driver = create();
            String content = getDynamicContent(driver, url);
            // 内容校验
            if (content.contains("cloudflare")) {
                throw new SecurityException("Detected anti-bot protection");
            }
            return content;
        } catch (TimeoutException e) {
            System.out.println("Page load timeout: {}");
            throw new Exception("");
        } catch (WebDriverException e) {
            System.out.println("WebDriver failure: {}");
            throw new Exception("");
        } finally {
            if (driver != null) {
                driver.quit(); // 必须彻底关闭释放资源
            }
        }
    }

    public static WebDriver create() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // 关键反检测配置
        options.addArguments("--headless=new"); // 无头模式
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches",
                Arrays.asList("enable-automation", "disable-popup-blocking"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1");

        // 禁用自动化特征
        options.setExperimentalOption("prefs", ImmutableMap.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));

        // 禁用非必要功能提升性能
        options.addArguments("--no-sandbox",
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--dns-prefetch-disable");
        WebDriver driver = new ChromeDriver(options);
        // 移除WebDriver特征
//        ((ChromeDriver) driver).executeCdpCommand(
//                "Page.addScriptToEvaluateOnNewDocument",
//                ImmutableMap.of(
//                        "source",
//                        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
//                )
//        );
        return driver;
    }
}
