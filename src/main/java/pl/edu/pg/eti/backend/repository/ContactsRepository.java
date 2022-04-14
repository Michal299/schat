package pl.edu.pg.eti.backend.repository;

public interface ContactsRepository {
    void addContact(String name, String address);
    void getContactName(String address);
    void getContact(String name);

}
