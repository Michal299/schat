package pl.edu.pg.eti.backend.contact;

import java.util.List;

public interface ContactsService {
    void addContact(String name, String address);
    boolean hasContactWithName(String name);
    boolean hasContactWithAddress(String address);
    String getContactAddress(String name);
    List<String> getContacts();
    String getContactByAddress(String address);
    void removeContact(String name);
}
