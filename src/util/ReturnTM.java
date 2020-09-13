package util;

public class ReturnTM {
    private String isbn;
    private String title;
    private String lendDate;
    private String returnDate;
    private String status;

    public ReturnTM(String isbn, String title, String lendDate, String returnDate, String status) {
        this.isbn = isbn;
        this.title = title;
        this.lendDate = lendDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public ReturnTM() {
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLendDate() {
        return lendDate;
    }

    public void setLendDate(String lendDate) {
        this.lendDate = lendDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReturnTM{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", lendDate='" + lendDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
