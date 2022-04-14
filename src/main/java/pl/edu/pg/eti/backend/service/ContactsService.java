package pl.edu.pg.eti.backend.service;

import java.util.List;

public interface ContactsService {
    List<String> getContacts();
    void addContact(final String name, final String address);
    String findContactName(final String name);
    String findContactAddress(final String address);
}
