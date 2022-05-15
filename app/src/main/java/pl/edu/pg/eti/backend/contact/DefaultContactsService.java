package pl.edu.pg.eti.backend.contact;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DefaultContactsService implements ContactsService {

    private final Map<String, String> contactsDetails;
    private final Logger log = LoggerFactory.getLogger(DefaultContactsService.class);
    private final String contactsFilePath;

    public DefaultContactsService(final String contactsFilePath) {
        contactsDetails = new TreeMap<>();
        this.contactsFilePath = contactsFilePath;
        readContacts();
    }

    @Override
    public void addContact(String name, String address) {
        contactsDetails.put(name, address);
        flushContactsToFile();
    }

    @Override
    public boolean hasContactWithName(String name) {
        return contactsDetails.containsKey(name);
    }

    @Override
    public boolean hasContactWithAddress(String address) {
        return contactsDetails.containsValue(address);
    }

    @Override
    public String getContactAddress(String name) {
        return contactsDetails.get(name);
    }

    @Override
    public List<String> getContacts() {
        return new ArrayList<>(contactsDetails.keySet());
    }

    @Override
    public String getContactByAddress(String address) {
        for (Map.Entry<String, String> entry : contactsDetails.entrySet()) {
            if (entry.getValue().equals(address)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void removeContact(String name) {
        contactsDetails.remove(name);
        flushContactsToFile();
    }

    private void readContacts() {
        final JSONParser jsonParser = new JSONParser();
        try (final FileReader reader = new FileReader(contactsFilePath)) {
            final JSONArray contactsObjects = (JSONArray) jsonParser.parse(reader);
            contactsObjects.forEach(contact -> parseContactObject((JSONObject) contact));
        } catch (FileNotFoundException exception) {
            log.error("Cannot read file with contacts: {}. Error: ", contactsFilePath, exception);
        } catch (IOException exception) {
            log.error("Error when reading a file: {}. Error: ", contactsFilePath, exception);
        } catch (ParseException exception) {
            log.error("Invalid contacts file: {}. Error: ", contactsFilePath, exception);
        }
    }

    private void parseContactObject(final JSONObject contact) {
        final String name = (String) contact.get("name");
        final String address = (String) contact.get("address");

        contactsDetails.put(name, address);
    }

    private void flushContactsToFile() {
        final JSONArray contactsArray = new JSONArray();
        contactsDetails.forEach((name, address) -> contactsArray.add(mapContactObject(name, address)));

        try (FileWriter writer = new FileWriter(contactsFilePath)) {
            writer.write(contactsArray.toJSONString());
        } catch (IOException exception) {
            log.error("Error when saving contacts to a file: {}. Error: ", contactsFilePath, exception);
        }
    }

    private JSONObject mapContactObject(final String name, final String address) {
        final JSONObject contactObject = new JSONObject();
        contactObject.put("name", name);
        contactObject.put("address", address);
        return contactObject;
    }
}
