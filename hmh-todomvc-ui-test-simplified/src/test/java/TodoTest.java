import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class TodoTest {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://todomvc.com/examples/react/dist/");
    }

    @Test
    public void testAddTodo() {
        WebElement newTodo = driver.findElement(By.className("new-todo"));
        newTodo.sendKeys("Buy Milk" + Keys.ENTER);
         WebElement todoLabel = new WebDriverWait(driver, Duration.ofSeconds(50))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo-list li label")));
        List<String> todos = driver.findElements(By.cssSelector(".todo-list li label"))
                                   .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(todos.contains("Buy Milk"));
    }

    @Test
    public void testEditTodo() {
        WebElement newTodo = driver.findElement(By.className("new-todo"));
        newTodo.sendKeys("Buy Milk" + Keys.ENTER);

        // Wait for the todo item to be added
        WebElement todoLabel = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo-list li label")));

        // Double-click the label to enter edit mode
        Actions actions = new Actions(driver);
        actions.doubleClick(todoLabel).perform();

        // Wait for the edit input field to be visible
        WebElement editInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo-list li .edit")));

        // Clear the existing text and enter new text
        editInput.clear();
        editInput.sendKeys("Buy Coffee" + Keys.ENTER);

        // Verify the updated todo item
        List<String> todos = driver.findElements(By.cssSelector(".todo-list li label"))
                                   .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(todos.contains("Buy Coffee"));
    }

    @Test
    public void testDeleteTodo() {
        WebElement newTodo = driver.findElement(By.className("new-todo"));
        newTodo.sendKeys("Buy Milk" + Keys.ENTER);
         WebElement todoLabel = new WebDriverWait(driver, Duration.ofSeconds(50))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo-list li label")));

        WebElement destroyButton = driver.findElement(By.cssSelector(".todo-list li .destroy"));
        // Cast driver to JavascriptExecutor and execute the script
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", destroyButton);

        List<WebElement> todos = driver.findElements(By.cssSelector(".todo-list li"));
        Assert.assertEquals(todos.size(), 0);
    }

    @Test
    public void testFilterActive() {
        WebElement newTodo = driver.findElement(By.className("new-todo"));
        newTodo.sendKeys("Buy Milk" + Keys.ENTER);
        newTodo.sendKeys("Buy Coffee" + Keys.ENTER);

         WebElement todoLabel = new WebDriverWait(driver, Duration.ofSeconds(50))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo-list li label")));

        WebElement firstTodoCheckbox = driver.findElement(By.cssSelector(".todo-list li:first-child .toggle"));
        firstTodoCheckbox.click();

        driver.findElement(By.linkText("Active")).click();
        List<String> activeTodos = driver.findElements(By.cssSelector(".todo-list li label"))
                                         .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertTrue(activeTodos.contains("Buy Coffee"));
        Assert.assertFalse(activeTodos.contains("Buy Milk"));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
