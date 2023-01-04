package backendtv.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Collection of data, stores a {@code Map} of String to String pairs,
 * where first member is the key and the second the value of one field.</p>
 *
 * <p>The Class acts like a real database collection and perform basic
 * actions like inserting, finding and modifying a member
 * or a specific field from the member.</p>
 *
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class DataCollection {
    private final List<Map<String, String>> members;

    /**
     * <p>Creates a new workspace for the members.</p>
     */
    public DataCollection() {
        members = new ArrayList<>();
    }

    /**
     * <p>Inserts a new member in the collection workspace.</p>
     *
     * @param fields a {@code Map} object containing all data of
     *               one member from the collection.
     */
    public void insert(final Map<String, String> fields) {
        if (fields == null) {
            return;
        }

        members.add(new HashMap<>(fields));
    }

    /**
     * <p>Finds the first member that matches the input (key, value).</p>
     *
     * @param fieldKey field key of the selected member.
     * @param fieldValue field value of the selected member.
     * @return a copy object of the found member or null if
     * member does not exist.
     */
    public Map<String, String> findOne(final String fieldKey, final String fieldValue) {
        if ((fieldKey == null) || (fieldValue == null)) {
            return null;
        }

        for (var member : members) {
            if (member.containsKey(fieldKey) && member.get(fieldKey).equals(fieldValue)) {
                return new HashMap<>(member);
            }
        }

        return null;
    }

    /**
     * <p>Finds all members that match the input (key, value).</p>
     *
     * @param fieldKey field key of selected members.
     * @param fieldValue field value of selected members.
     * @return a {@code List} of copy objects of found members or
     * null is no member was found.
     */
    public List<Map<String, String>> find(final String fieldKey, final String fieldValue) {
        if ((fieldKey == null) || (fieldValue == null)) {
            return null;
        }

        final List<Map<String, String>> foundMembers = new ArrayList<>();

        for (var member : members) {
            if (member.containsKey(fieldKey) && member.get(fieldKey).equals(fieldValue)) {
                foundMembers.add(new HashMap<>(member));
            }
        }

        if (foundMembers.isEmpty()) {
            return null;
        }

        return foundMembers;
    }

    /**
     * <p>Fetches all members from the collection and returns
     * copies of them.</p>
     *
     * @return a {@code List} of copy objects of all members
     * from the database collection or null is no member
     * was returned.
     */
    public List<Map<String, String>> getMembers() {
        final List<Map<String, String>> foundMembers = new ArrayList<>();

        for (var member : members) {
            foundMembers.add(new HashMap<>(member));
        }

        if (foundMembers.isEmpty()) {
            return null;
        }

        return foundMembers;
    }

    /**
     * <p>Modifies all the fields of a member that matches
     * the input (key, value) pair from the collection.</p>
     *
     * @param fieldKey field key of the member.
     * @param fieldValue field value of the member.
     * @param fields new field data for the selected member.
     * @return true if the member was modified and false if
     * input data is null or member was not found.
     */
    public boolean modifyMember(final String fieldKey, final String fieldValue,
                                final Map<String, String> fields) {
        if ((fieldKey == null) || (fieldValue == null) || (fields == null)) {
            return false;
        }

        for (var member : members) {
            if (member.containsKey(fieldKey) && member.get(fieldKey).equals(fieldValue)) {
                member.clear();
                member.putAll(fields);

                return true;
            }
        }

        return false;
    }

    /**
     * <p>Modifies the first member that matches the (key, value) pair
     * and changes a field value, however if the changing field.</p>
     *
     * @param fieldKey field key of the selected member.
     * @param fieldValue field value of the selected member.
     * @param changeFieldKey field key that will be changed.
     * @param changeFieldValue field value to change the selected field.
     * @return true if the field of the member was modified or false
     * if input data is null, or member does not exist.
     */
    public boolean modifyField(final String fieldKey, final String fieldValue,
                               final String changeFieldKey, final String changeFieldValue) {
        if ((fieldKey == null) || (fieldValue == null)
                || (changeFieldKey == null) || (changeFieldValue == null)) {
            return false;
        }

        for (var member : members) {
            if (member.containsKey(fieldKey) && member.get(fieldKey).equals(fieldValue)
                    && member.containsKey(changeFieldKey)) {
                member.put(changeFieldKey, changeFieldValue);

                return true;
            }
        }

        return false;
    }

    /**
     * <p>Deletes a member from the collection workspace.</p>
     * @param fieldKey field key of the selected member.
     * @param fieldValue field value of the selected member.
     */
    public void delete(final String fieldKey, final String fieldValue) {
        if ((fieldKey == null) || (fieldValue == null)) {
            return;
        }

        members.removeIf(member -> {
            if (!member.containsKey(fieldKey)) {
                return false;
            }

            return member.get(fieldKey).equals(fieldValue);
        });
    }
}
