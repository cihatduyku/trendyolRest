package tests;

import io.restassured.http.ContentType;
import objects.Book;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.*;


public class TrendyolTests extends APITestCase {


    //1. Verify that the API starts with an empty store
    @Test
    public void startWithEmptyStore() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(equalTo(""));
    }

    //2. Verify that title and author are required fields.
    @Test
    public void titleRequiredTest() {
        Book book = new Book();
        book.setAuthor("cihat");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", equalTo("Field 'title' is required."));
    }

    @Test
    public void authorRequiredTest() {
        Book book = new Book();
        book.setTitle("test otomasyon");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", equalTo("Field 'author' is required."));
    }

    //3. Verify that title and author cannot be empty.
    @Test
    public void titleEmptyTest() {
        Book book = new Book();
        book.setAuthor("cihat");
        book.setTitle("");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", containsString("Field 'title' cannot be empty."));
    }

    @Test
    public void authorEmptyTest() {
        Book book = new Book();
        book.setAuthor("");
        book.setTitle("test otomasyon");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", containsString("Field 'author' cannot be empty."));
    }

    //4. Verify that the id field is read−only.
    @Test
    public void idReadOnlyTest() {
        Book book = new Book();
        book.setId(131313);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    //5. Verify that you can create a new book via PUT.
    @Test
    public void createNewBookTest() {
        Book book = new Book();
        book.setTitle("test otomasyon");
        book.setAuthor("cihat");

        Book newBook = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue())
                .body("title", equalTo(book.getTitle()))
                .body("author", equalTo(book.getAuthor()))
                .extract()
                .as(Book.class);


        int newBookId = newBook.getId();

        Book getBook = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(API_ROOT + "/api/books/" + newBookId)
                .peek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Book.class);

        assertEquals(newBook, getBook);
    }

    //6. Verify that you cannot create a duplicate book.
    @Test
    public void createDuplicateBookTest() {
        Book book = new Book();
        book.setTitle("test otomasyon");
        book.setAuthor("cihat");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue())
                .body("title", equalTo(book.getTitle()))
                .body("author", equalTo(book.getAuthor()))
                .extract()
                .as(Book.class);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(book)
                .put(API_ROOT + "/api/books/")
                .peek()
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

}