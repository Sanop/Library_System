package util;

public class BorrowerTM {
    private String borrower_ID;
    private String borrower_Name;
    private String borrower_Address;
    private String borrower_NIC;
    private String borrower_Contact;

    public BorrowerTM(String borrower_ID, String borrower_Name, String borrower_Address, String borrower_NIC, String borrower_Contact) {
        this.borrower_ID = borrower_ID;
        this.borrower_Name = borrower_Name;
        this.borrower_Address = borrower_Address;
        this.borrower_NIC = borrower_NIC;
        this.borrower_Contact = borrower_Contact;
    }

    public BorrowerTM(String borrower_ID, String borrower_Name) {
        this.borrower_ID = borrower_ID;
        this.borrower_Name = borrower_Name;
    }

    public BorrowerTM() {
    }

    public String getBorrower_ID() {
        return borrower_ID;
    }

    public void setBorrower_ID(String borrower_ID) {
        this.borrower_ID = borrower_ID;
    }

    public String getBorrower_Name() {
        return borrower_Name;
    }

    public void setBorrower_Name(String borrower_Name) {
        this.borrower_Name = borrower_Name;
    }

    public String getBorrower_Address() {
        return borrower_Address;
    }

    public void setBorrower_Address(String borrower_Address) {
        this.borrower_Address = borrower_Address;
    }

    public String getBorrower_NIC() {
        return borrower_NIC;
    }

    public void setBorrower_NIC(String borrower_NIC) {
        this.borrower_NIC = borrower_NIC;
    }

    public String getBorrower_Contact() {
        return borrower_Contact;
    }

    public void setBorrower_Contact(String borrower_Contact) {
        this.borrower_Contact = borrower_Contact;
    }

    @Override
    public String toString() {
        return "BorrowersController{" +
                "borrower_ID='" + borrower_ID + '\'' +
                ", borrower_Name='" + borrower_Name + '\'' +
                ", borrower_Address='" + borrower_Address + '\'' +
                ", borrower_NIC='" + borrower_NIC + '\'' +
                ", borrower_Contact='" + borrower_Contact + '\'' +
                '}';
    }
}
