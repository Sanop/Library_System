package util;

public class LendTM {
    private String borrowerID;
    private String isbn;
    private String lendDate;
    private String returnDate;

    public LendTM(String borrowerID, String isbn, String lendDate, String returnDate) {
        this.borrowerID = borrowerID;
        this.isbn = isbn;
        this.lendDate = lendDate;
        this.returnDate = returnDate;
    }


    public LendTM() {
    }

    public String getBorrowerID() {
        return borrowerID;
    }

    public void setBorrowerID(String borrowerID) {
        this.borrowerID = borrowerID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    @Override
    public String toString() {
        return "LendTM{" +
                "borrowerName='" + borrowerID + '\'' +
                ", bookName='" + isbn + '\'' +
                ", lendDate='" + lendDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                '}';
    }
}
