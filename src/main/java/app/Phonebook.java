package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import util.DBWorker;

public class Phonebook {

	private HashMap<String, Person> persons = new HashMap<String, Person>();

	private DBWorker db = DBWorker.getInstance();

	private static Phonebook instance = null;

	public static Phonebook getInstance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new Phonebook();
		}
		return instance;
	}

	protected Phonebook() throws ClassNotFoundException, SQLException {
		ResultSet db_data = this.db.getDBData("SELECT * FROM `person` ORDER BY `surname` ASC");
		while (db_data.next()) {
			this.persons.put(db_data.getString("id"), new Person(db_data.getString("id"), db_data.getString("name"),
					db_data.getString("surname"), db_data.getString("middlename")));
		}
	}

	public boolean addPerson(Person person) {
		ResultSet db_result;
		String query;

		if (!person.getSurname().equals("")) {
			query = "INSERT INTO `person` (`name`, `surname`, `middlename`) VALUES ('" + person.getName() + "', '"
					+ person.getSurname() + "', '" + person.getMiddlename() + "')";
		} else {
			query = "INSERT INTO `person` (`name`, `surname`) VALUES ('" + person.getName() + "', '"
					+ person.getSurname() + "')";
		}

		Integer affected_rows = this.db.changeDBData(query);

		if (affected_rows > 0) {
			person.setId(this.db.getLastInsertId().toString());

			this.persons.put(person.getId(), person);
			return true;
		} else {
			return false;
		}
	}

	public boolean updatePerson(String id, Person person) {
		Integer id_filtered = Integer.parseInt(person.getId());
		String query = "";

		if (!person.getSurname().equals("")) {
			query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname()
					+ "', `middlename` = '" + person.getMiddlename() + "' WHERE `id` = " + id_filtered;
		} else {
			query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname()
					+ "' WHERE `id` = " + id_filtered;
		}

		Integer affected_rows = this.db.changeDBData(query);

		if (affected_rows > 0) {
			this.persons.put(person.getId(), person);
			return true;
		} else {
			return false;
		}
	}

	public boolean deletePerson(String id) {
		if ((id != null) && (!id.equals("null"))) {
			int filtered_id = Integer.parseInt(id);

			Integer affected_rows = this.db.changeDBData("DELETE FROM `person` WHERE `id`=" + filtered_id);

			if (affected_rows > 0) {
				this.persons.remove(id);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean addNumber(Person person, String number) {
		Integer filtered_id = Integer.parseInt(person.getId());
		String query = "";

		query = "INSERT INTO `phone` (`owner`, `number`) VALUES (" + filtered_id + ", '" + number + "')";

		Integer affected_rows = this.db.changeDBData(query);

		if (affected_rows > 0) {
			String phone_id = this.db.getLastInsertId().toString();

			person.getPhones().put(phone_id, number);
			this.persons.put(person.getId(), person);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateNumber(String id, String idPhone, String number) {
		if ((idPhone != null) && (!idPhone.equals("null"))) {
			int filtered_id = Integer.parseInt(idPhone);
			Integer affected_rows = this.db
					.changeDBData("UPDATE `phone` SET `number` = '" + number + "' WHERE `id`=" + filtered_id);

			if (affected_rows > 0) {
				if ((id != null) && (!id.equals("null"))) {
					this.persons.get(id).getPhones().put(idPhone, number);
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean deleteNumber(String id, String idPhone) {
		if ((idPhone != null) && (!idPhone.equals("null"))) {
			int filtered_id = Integer.parseInt(idPhone);
			Integer affected_rows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + filtered_id);

			if (affected_rows > 0) {
				if ((id != null) && (!id.equals("null"))) {
					this.persons.get(id).getPhones().remove(idPhone);
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public HashMap<String, Person> getContents() {
		return persons;
	}

	public Person getPerson(String id) {
		return this.persons.get(id);
	}

}
