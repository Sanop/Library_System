package util;

public class BooksTM {
    private String isbn;
    private String book_Name;
    private String author;
    private double price;

    public BooksTM(String isbn, String book_Name, String author, double price) {
        this.isbn = isbn;
        this.book_Name = book_Name;
        this.author = author;
        this.price = price;
    }

    public BooksTM(String isbn, String book_Name) {
        this.isbn = isbn;
        this.book_Name = book_Name;
    }

    public BooksTM() {
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBook_Name() {
        return book_Name;
    }

    public void setBook_Name(String book_Name) {
        this.book_Name = book_Name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BooksTM{" +
                "isbn='" + isbn + '\'' +
                ", book_Name='" + book_Name + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                '}';
    }
}
